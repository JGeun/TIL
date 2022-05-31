## 순환 참조

순환 참조란 두 가지 이상의 객체가 서로에 대한 참조 상태를 가지고 있을 때 발생하며, 순환 참조가 발생하게 되면 서로에 대한 참조가 해제되지 않기 때문에 메모리에서 유지되며 이로 인한 메모리 leak이 발생하게 됩니다.

<span style="color: #2D3748; background-color:#fff5b1;">아래 그림과 같이 빨간 줄이 순환참조를 발생시키는 원인입니다.</span>

![순환 참조](img/circular%20dependency.png)

코드로 한번 확인해볼까요?

```Kotlin
class A {
    init { val b = B() }
}

class B {
    init { val a = A() }
}

fun main() {
    val a = A()
}
```

A가 초기화할 때는 B가 할당되고, B가 초기화될 땐 A가 할당되도록 설정한 후 main에서 객체 A를 생성하였더니 아래와 같은 에러가 발생하게 됩니다.

![순환참조 에러](img/circular%20dependecy%20error.png)


이러한 문제가 바로 순환참조라고 합니다. 위와 같이 지속적으로 A와 B가 호출되기 때문에 메모리 누수가 발생하는 것입니다. 따라서 <span style="background-color:#fff5b1;">의존성을 설정할 때 순환참조가 일어나지 않도록 조심</span>해야합니다.

ps. Android Studio ide의 경우 의존성을 설정하다 순환참조가 발생할 경우 자체적으로 빌드를 막습니다. 그래도 의존성을 설정할 때는 항상 구조를 생각하며 조심해야 할 것 같습니다.

![순환참조 안드로이드 에러](img/circular%20dependency%20error%20in%20android.png)