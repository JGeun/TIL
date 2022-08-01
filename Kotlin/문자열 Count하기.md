Java에서 문자열 Count 하는 방법은 굉장히 귀찮았습니다. 재귀, 반복문 등등 물론 lambda식을 활용하여 구현할 수도 있었지만 굉장히 코드가 길었습니다.



반대로 Kotlin에서는 간단하게 해결할 수 있습니다.
```kotlin
fun main() {

	val str = "hello my name is JGeun"
    
    println(str.count{it == 'l'})
    
    // result: 2
}
```