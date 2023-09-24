## 배경

안드로이드 개발을 하다보면 테스트코드를 작성하지 않고도 UI와 로직을 구현할 수 있습니다. 저 역시도 지금까지 이렇게 개발을 해왔는데 예전 코드를 리팩토링 혹은 에러를 수정할 때 내가 수정한 로직이 이전과 동일하게
작성하는지 체크하는 것이 매우 어려웠고, 실제로도 보장할 수 없다고 생각이 되어 지금부터 Test를 작성하는 법에 대해 공부해보려 합니다.

목표는 JUnit -> Espresso -> Hilt Test -> 클린 아키텍처 적용 시의 Test 까지 정리해보려고 합니다!

## JUnit이란?

Unit Test는 말 그래도 단위 테스트로, Test Driven Development로 개발하게 되면 기능 단위별로 테스트 코드를 구성해야 합니다.
대표적인 Unit Test Tool로 JUnit이 있으며, 자바 프로그래밍용 단위테스트 프레임워크입니다.
주로 안드로이드 프레임워크와의 dependency가 없는 테스트 코드에 사용되며, 로컬 JVM에서 실행됩니다.
만약, 안드로이드 프레임워크와의 복잡한 상호작용을 하는 dependecny가 있다며 Robolectric 을 사용하는 것이 좋고, 테스트에 최소한의 안드로이드 프로젝트와 dependency라면 Mockito와 같은
모의 프레임워크를 사용하는 것이 좋습니다.

### Junit 메소드

- @Before : @Test 메소드가 시작하기 전에 항상 호출되는 메소드
- @After : @Test 메소드가 종료되면 호출되는 메소드, 주로 메모리 release
- @Test : @Before가 완료되면 실제 코드 테스트 진행
- @Rule : 해당 Test클래스에서 사용하게 될 ActivityTestRule과 ServiceTestRule 정의
- @BeforeClass, @AfterClass : public static method로 정의해야 하며, 전체 테스트 클래스에서 테스트 실행 시 시작할 때와 끝날 때 한 번 실행되도록 하기 위한 어노테이션
- @Test(Timeout = ) : @Test 에 대한 timeout을 지정하게 되어 timeout안에 테스트 완료될 수 있도록 합니다. 타임 초과시 실패
- @RequiresDevice : 에뮬레이터를 사용하지 않고 기기만 사용 가능
- @SdkSupress : minSdkVersion을 이용
- @SmallTest, @MediumTest, @LargeTest : 테스트 성격 구분하여 테스트

여기까진 아마 검색을 통해서 많이 사용하셨을 거라 생각합니다.

그렇다면 Android Test에서 활용은 어떻게 할까요?

## AndroidX 테스트에 JUnit4 규칙 사용

지금부터 안드 공홈을 기준으로 정리해보도록 하겠습니다.

```kotlin
@RunWith(AndroidJUnit4::class.java)
@LargeTest
class MyClassTest {
    @get:Rule
    val activityRule = ActivityTestRule(MyClass::class.java)

    @Test
    fun myClassMethod_ReturnsTrue() {
        // ...
    }
}
```

예제 코드에서 ActivityTestRule을 사용했는데 그 이유는 다음과 같습니다.

>This rule provides functional testing of a single Activity. When launchActivity is set to true in the constructor, the
Activity under test will be launched before each test annotated with Test and before methods annotated with Before, and
it will be terminated after the test is completed and methods annotated with After are finished.
>
>이 규칙은 단일 Activity에 대한 기능 테스트를 제공합니다. 생성자에서 launchedActivity를 true로 설정하면, 테스트 대상 Activity는 Test가 명시된 각 test 전에 시작됩니다.
테스트에 주석이 달린 각 테스트는 시작 전에 Before가 명시된 메소드가 실행된 후 시작될 것이고, test가 완료된 후엔 After가 명시된 메소드가 실행된 후 종료될 것입니다.
>
>The Activity can be manually launched with launchActivity(Intent), and manually finished with finishActivity(). If the
Activity is running at the end of the test, the test rule will finish it.
>
>Activity는 launchActivity(Intent)와 함께 수동으로 시작되고, finishActivity()로 끝납니다. 테스트가 종료 시 Activity가 실행 중이라면 test rule이 이를 끝냅니다.
>
>During the duration of the test you will be able to manipulate your Activity directly using the reference obtained from
getActivity(). If the Activity is finished and relaunched, the reference returned by getActivity() will always point to
the current instance of the Activity.
>
>테스트가 진행되는 동안, getActivity()로 부터 얻은 레퍼런스를 사용해 직접 Activity를 조작할 수 있습니다. Activity가 종료되고 재실행된다면, getActivity()로 부터 반환된 레퍼런스는
Activity의 현재 instance를 가리킬 것입니다.
>
>하지만 이제는 ActivityTestRule은 Deprecated되었고 ActivityScenarioRule을 사용해야 합니다.
>
>ActivityScenarioRule은 TestRule과 동일하지만 Activity의 결과가 필요할 때 scenario.getResult()를 지원하지 않고
ActivityScenario.launchActivityForResult()를 사용해야 한다고 명시되어 있습니다.
>
>정리하자면 ActivityTestRule과 ActivityScenarioRule 모두 Activity에 대한 Test를 진행할 때 해당 Activity를 Test용으로 띄우는 것을 가능하게 해주는 것 같습니다.
만약에 저런 명시를 해주지 않는다면 아래와 같은 에러를 마주하게 됩니다.
>
>No activities found. Did you forget to launch the activity
by calling getActivity() or startActivitySync or similar?

## 정리
사실 JUnit은 코드 로직 체크 또는 Annotation에 대한 내용 밖에 없었습니다. 다음으로는 Espresso를 정리하고 코드까지 구현해보겠습니다!

## 참고
https://developer.android.com/training/testing/junit-rules?hl=ko

https://math-coding.tistory.com/180