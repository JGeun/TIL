### 공부배경

코틀린을 사용하다보면 null체크를 스코프함수인 let을 통해 간편하게 체크하곤 합니다. 하지만 문득 if로 비교하는 것과 ?.let으로 비교하는 것에 차이는 없는 것일까 확인해보고 싶어서 각 코드를 바이트코드로
변환하여 비교해보고자 합니다.

### 바이트 코드 명령어

우선 바이트코드 변환에 앞서 명령어들이 어떤 의미를 가지는지 간단히 정리하겠습니다.

- aload : local variable 을 stack 에 push 한다
- ldc : constant pool 에서부터 #index 에 해당하는 데이터를 가져온다
- astore : local variable 에 값을 저장한다.
- invokespecial : instance Method 를 호출하고 결과를 stack 에 push한다.
- new : 새로운 객체를 생성한다.
- invokevirtual : 메서드를 호출한다.
- dup : stack 에 있는 top 을 복사한다.

### != null

해당 코드의 예시를 아래와 같이 구현했습니다. 우측 번호는 라인넘버입니다. 디컴파일 했을 때 크게 변하는 것은 보이지 않습니다.

```kotlin
// 기존 코드
1 fum main() {
    2
    val x: String? = ""
    3
    4    if (x != null) {
    5 print ("test")
    6
}
    7
}

// 디컴파일된 코드
public final class MainKt {
    public static final void main()
    {
        String x = "";
        String var1 = "test";
        System.out.print(var1);
    }

// $FF: synthetic method
    public static void main(String[] var0)
    {
        main();
    }
}
```

해당 코드를 ByteCode로 변환해보면 아래와 같습니다. ByteCode 역시 크게 눈에 띄는 부분은 없습니다.

```kotlin
L0
    LINENUMBER 2 L0 
    LDC ""
    ASTORE 0
L1
    LINENUMBER 4 L1
L2
    LINENUMBER 5 L2
    LDC "test"
    ASTORE 1
L3
    GETSTATIC java / lang / System . out : Ljava / io / PrintStream;
    ALOAD 1
    INVOKEVIRTUAL java / io / PrintStream . print (Ljava / lang / Object;)V
L4
L5
    LINENUMBER 7 L5
    RETURN
L6
    LOCALVARIABLE x Ljava / lang / String; L1 L6 0
    MAXSTACK = 2
    MAXLOCALS = 2
```

### ?.let

이제 let을 사용해서 비교해보도록 하겠습니다. let을 디컴파일해보면 int var3라는 이상한 변수가 하나 생성된 것을 확인할 수 있습니다.

```kotlin
// 기존 코드 1 fun main() { 2 val x: String? = ""
3
4 x ?. let {
    5 print ("test")
    6
} 7 }

// Java로 디컴파일된 코드 public final class MainKt { public static final void main() { String x = ""; int var3 = false; String
var4 = "test"; System.out.print(var4); }


// $FF: synthetic method public static void main(String[] var0) { main(); } }
```

위의 코드를 바이트코드로 변환하면 아래와 같습니다. != null 의 바이트코드와 비교했을 때 var3 변수가 생성되고 NOP 처리되는 부분으로 인해 바이트 코드가 길어진 것을 확인할 수 있습니다.

> JVM may use NOP bytecodes for JIT optimizations to ensure code blocks that are at synchronization safepoints are
> properly aligned to avoid false sharing
>
> JVM은 JIT 최적화를 위해 NOP 바이트 코드를 사용하여 잘못된 공유를 방지하기 위해 동기화 안전 지점에 있는 코드 블록이 적절하게 정렬되도록 할 수 있습니다

```kotlin
L0
    LINENUMBER 2 L0
    LDC ""
    ASTORE 0
L1
    LINENUMBER 4 L1
    ALOAD 0
    ASTORE 1
L2
L3
    ALOAD 1
    ASTORE 2
L4
    ICONST_0
    ISTORE 3
L5
    LINENUMBER 5 L5 
    LDC "test"
    ASTORE 4
L6
    GETSTATIC java/lang/System.out : Ljava/io/PrintStream;
    ALOAD 4
    INVOKEVIRTUAL java/io/PrintStream.print (Ljava/lang/Object;)V
L7
L8
    LINENUMBER 6 L8
    NOP
L9
L10
    LINENUMBER 4 L10
    NOP
L11
L12
    LINENUMBER 7 L12
    RETURN
L13
    LOCALVARIABLE it Ljava/lang/String; L4 L9 2
    LOCALVARIABLE $i$a$-let-MainKt$main$1 I L5 L9 3
    LOCALVARIABLE x Ljava/lang/String; L1 L13 0
    MAXSTACK = 2
    MAXLOCALS = 5
```

그럼 이제 반대로 x 가 null일 경우로 코드를 작성해보겠습니다. 기존 var3, var4는 사라지고 String x = (String)null; 코드만 남게 되었습니다.

```kotlin
fun main() {
    val x: String? = null

    if (x != null) {
        print("test")
    }

    x?.let {
        print("test")
    }

}

// 디컴파일된 코드 public final class MainKt { public static final void main() { String x = (String)null; }

// $FF: synthetic method public static void main(String[] var0) { main(); } }
```

### 정리
개발자들이 코틀린을 사용하면서 스코프 함수를 많이 애용하는데 오히려 스코프함수가 기존 != null 코드보다 성능이 좋지 않다는 것을 확인할 수 있었습니다.

#### 추가적으로

저도 이부분을 공부하면서 NOP가 왜 생겼는지 이해가 잘 되지 않아서 추후 공부하다가 깨달음이 있다면 다시 정리해보겠습니다. 아시는 분이 계시다면 댓글로 알려주시면 감사합니다.

<참고>

https://wonit.tistory.com/589

https://stackoverflow.com/questions/10422086/what-is-the-nop-in-jvm-bytecode-used-for

https://stackoverflow.com/questions/55648139/null-vs-let-performance-for-immutable-variables-in-kotlin