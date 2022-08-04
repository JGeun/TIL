## Enum Class: Enumerated Type

열거형의 준말로 class 내에 상태를 구분하기 위한 객체들을 이름을 붙여 여러개 생성하고 그 중 하나의 상태를 선택해 나타내는 방식입니다.

1. enum class 안에는 상수를 나타내는 대문자로 나타냅니다.

```kotlin
enum class Color {
    RED,
    BLUE,
    GREEN
}
```

2. Enum의 객체들은 고유한 속성을 가질 수 있다.

```kotlin
enum class Color(val red: Int, val green: Int, val blue: Int) {
    RED(255, 0, 0),
    GREEN(0, 255, 0),
    BLUE(0, 0, 255)
}
```

3. 일반 클래스처럼 함수도 선언 가능하다.

```kotlin
enum class Color(val red: Int, val green: Int, val blue: Int) {
    RED(255, 0, 0),
    GREEN(0, 255, 0),
    BLUE(0, 0, 255); // <- ;이 핵심

    fun isRed() = this == Color.RED
}
```

Enum class의 단점

```kotlin
enum class Result {
    SUCCESS,
    FAILED(val exception: Exception);
}
```

이런 식으로 각 객체들이 서로 다른 형태를 가질 수 없습니다.

## Sealed class

Enum과 같은 value당 제한 (소속을 갖는 set으로의 정의)를 가지면서 각 value의 형태를 다르게 확장성 있도록 가져가기 위해서 sealed class를 사용합니다

```kotlin
sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Failed(val exception: Exception) : Result<Nothing>()
    object InProgress : Result<Nothing>()
}
```

세 가지 타입이 Result란 범위로 묶입니다. 각각의 요소가 되는 항목들 역시 다른 형태의 값을 지닐 수 있습니다.

단, sealed class를 상속받는 클래스들은 같은 kt 파일 또는 package 안에 존재해야 합니다. 이는 내부적으로 Result 클래스가 private한 constructor를 갖기 때문입니다. (외부에서
sealed class를 상속받는다면 compile error가 발생합니다.)

Sealed class의 When문 주의점

When문을 사용하면 sealed class/interface를 안전하고 쉽게 구문할 수 있습니다.

```kotlin

fun main() {
    val result = Result.Success("result is Success")
    processResult(result)
}

fun processResult(result: Result<String>) {
// expression
    val expression = when (result) {
        is Result.Success -> println(result.data)
        is Result.Failed -> println(result.exception)
// is Result.InProgress -> println("processing!!")
    }

    // statement
    when (result) {
        is Result.Success -> println(result.data)
        is Result.Failed -> println(result.exception)
        // is Result.InProgress -> println("processing!!")
    }
}


sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()
    data class Failed(val exception: Exception) : Result<Nothing>()
    object InProgress : Result<Nothing>()
}

// 'when' expression must be exhaustive, add necessary 'InProgress' branch or 'else' branch instead
```

when문을 expression을 사용 시 누락된 값이 있다면 compile에러가 발생합니다.

하지만 statement 형태로 쓰는 경우 compile error가 발생하지 않아 when문으로 처리하는 모든 코드들을 인지하기 어렵습니다.

따라서 이런 runtime error를 방지하기 위해서 아래와 같이 extension property를 추가합니다.

```kotlin
fun processResult(result: Result<String>) {
// statement
    when (result) {
        is Result.Success -> println(result.data)
        is Result.Failed -> println(result.exception)
// is Result.InProgress -> println("processing!!")
    }.exhaustive
}

val <T> T.exhaustive: T
    get() = this
```

When 구문의 제거

When문을 제거하고 쉽게 사용하는 방법이 있습니다

```kotlin
fun processResult(result: Result<String>) {
    result.onSuccess { data -> println(data) }
    result.onFailed { exception -> println(exception) }
    result.onProgress { println("progress!!") }
}

inline fun <reified T : Any> Result<T>.onSuccess(action: (data: T) -> Unit) {
    if (this is Result.Success) {
        action(data)
    }
}

inline fun <reified T : Any> Result<T>.onFailed(action: (exception: Exception) -> Unit) {
    if (this is Result.Failed) {
        action(exception)
    }
}

inline fun <reified T : Any> Result<T>.onProgress(action: () -> Unit) {
    if (this is Result.InProgress) {
        action()
    }
}
```

inline function을 생성할 경우 아래와 같이 when문 없이 간단하게 사용이 가능해집니다.

Subset을 갖는 sealed class

enum과는 다르게 sealed class는 내부에 subset을 갖도록 만들 수도 있습니다.

```kotlin
sealed class Result<out T : Any> {
    data class Success<out T : Any>(val data: T) : Result<T>()

    sealed class Failed(val exception: Exception, val message: String?) : Result<Nothing>() {
        class NetworkError(exception: Exception, message: String?) : Failed(exception, message)
        class StorageIsFull(exception: Exception, message: String?) : Failed(exception, message)
    }
    object InProgress : Result<Nothing>()
}
```