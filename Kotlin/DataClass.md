### 데이터 클래스 (Data class)는 데이터를 다루는 데에 최적화된 class 로  '5가지 기능'을 내부적으로 제공합니다.
1. 내용의 동일성을 판단하는 equals() 의 자동 구현

2. 객체의 내용에서 고유한 코드를 생성하는 hashcode()의 자동구현

3. 포함된 속성을 보기쉽게 나타내는 toString()의 자동구현

4. 객체를 복사하여 똑같은 내용의 새 객체를 만드는 copy()의 자동구현 -> 아무 패러미터가 없다면 똑같은 내용으로 생성 (아무 패러미터가 없다면 똑같은 내용으로, 있다면  해당 패러미터를 교체하여 생성합니다)

```kotlin
fun main() {

    val a = Person("철수", 24)
    val a_compare = Person("철수", 24)
    println(a == a_compare)
    println("${a.hashCode()} ${a_compare.hashCode()}")
    
    val b = PersonData("영희", 30)
    val b_compare = PersonData("영희", 30)
    println(b == b_compare)
    println("${b.hashCode()} ${b_compare.hashCode()}")
    
    println(b.copy())
    println(b.copy("아린"))
    println(b.copy(age = 20))
}

class Person(val name: String, val age: Int)

data class PersonData(val name: String, val age: Int)

/* result
false
835648992 1134517053
true
50421747 50421747
PersonData(name=영희, age=30)
PersonData(name=아린, age=30)
PersonData(name=영희, age=20)
*/
```

기본 class인 Person으로 객체를 생성할 경우 똑같은 data를 넣어줬지만 hashCode가 다르고 비교하였을 때 false가 나타나게 됩니다. 하지만 data class인 PersonData로 비교할 경우 hashCode이 같고 비교값이 true로 확인할 수 있습니다.



또한 copy()이 패러미터가 없으면 값이 동일하며 패러미터 값에 따라 객체의 데이터가 변동되는 것을 확인할 수 있습니다

5. 속성을 순서대로 반환하는 componentX()의 자동구현

( 사용자가 직접 호출하기 위한 함수가 아닌 listOf(Data("A", 7), Data("B", 1)) 의 내용을 자동으로 꺼내쓸 수 있는 기능을 지원하기 위한 함수입니다 )

```kotlin
fun main() {

	val list = listOf(Person("보영", 30),
                     Person("희철", 40),
                     Person("민희", 23))
    
    for( (a, b) in list) {
        println("${a}, ${b}")
    }
}

class Person(val name: String, val age: Int)

/* error
Destructuring declaration initializer of type Person must have a 'component1()' function
Destructuring declaration initializer of type Person must have a 'component2()' function
*/
```

기본 class로 객체를 입력할 경우 "(a, b) in list"와 같이 component를 뽑아내야 하는 작업을 하지 못합니다.

```kotlin
fun main() {

    val list = listOf(PersonData("보영", 30),
                     PersonData("희철", 40),
                     PersonData("민희", 23))
    
    for( (a, b) in list) {
        println("${a}, ${b}")
    }
}

data class PersonData(val name: String, val age: Int)

/* result
보영, 30
희철, 40
민희, 23
*/
```

하지만 data class의 경우 내부적으로 component를 자동구현해주기 때문에 결과값이 잘 나오는 것을 확인할 수 있습니다.
