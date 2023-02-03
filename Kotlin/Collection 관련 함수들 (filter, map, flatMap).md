### filter

주어진 람다의 조건을 만족하는 원소만 필터링하는 기능입니다.

List와 Set을 필터링하는 경우에는 List로, Map을 필터링하는 경우에는 Map으로 반환합니다.

filter 함수는 요소의 값만 확인할 수 있습니다.

```kotlin
val list = listOf("A", "B", "C")
val newList = list.filter {
    it != "A"
}
println(newList)

// input: [A, B, C]
// Result: [B, C]
```

### map

각 원소를 원하는 형태로 변환해서 새 컬렉션을 만듭니다.

새로 만들어지는 컬렉션은 원본 컬렉션과 원소의 개수는 같지만 각 원소는 주어진 람다(함수)에 따라 변환됩니다.

```kotlin
val list = listOf("A", "B", "C")
val newList = list.map {
    "$it!"
}
println(newList)

// Input: [A, B, C]
// Result: [A!, B!, C!]
```

### flatMap

람다 내에 Iterable(반복가능)한 형태만 넣을 수 있습니다.

각 조건별로 만들어진 배열들을 하나로 만들어준다고 생각하면 됩니다.

ex) A -> A! -> [A, !], B -> B! -> [B, !], C -> C! -> [C, !]

```kotlin 
val list = listOf("A", "B", "C")
val newList = list.flatMap {
"$it!".toList()
}
println(newList)

// Input: [A, B, C]
// Result: [A, !, B, !, C, !]
```