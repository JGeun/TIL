## Espresso가 뭐지?

Espresso는 Android UI 테스트를 작성할 수 있도록 돕는 라이브러리 입니다. 저희가 앱이 잘 동작하는 지 직접 테스트 했었다면, Espresso는 코드 동작의 순서를 명시하여 테스트를 실행했을 때
에뮬에서 명시된 순서대로 동작하는 걸 확인할 수 있습니다.

테스트 코드를 통해 모든 테스트 케이스를 개발자가 직접 테스트를 하지 않아도 되니 휴먼 에러가 발생할 확률이 적다 추후 리팩토링 시 예전에 작성된 테스트 코드와 동일하게 작동하는지도 확인할 수 있어서 에러 발생
확률을 낮출 수 있다

```kotlin
@Test
fun greeterSaysHello() {
    onView(withId(R.id.name_field)).perform(typeText("Steve"))
    onView(withId(R.id.greet_button)).perform(click())
    onView(withText("Hello Steve!")).check(matches(isDisplayed()))
}
```

## Espresso 구성요소

Espresso

- onView() 및 onData()를 통한 뷰와의 상호작용을 위한 진입점

- 반드시 뷰와 연결되지 않아도 되는 API를 노출 ex) pressBack()

ViewMatchers

- Matcher<? super View> 인터페이스를 구현하는 객체의 컬렉션

- 하나 이상을 onView() 메서드에 전달하여 현재 뷰 계층 구조 내에서 뷰를 찾을 수 있음

ViewActions

- ViewInteraction.perform() 메서드에 전달할 수 있는 ViewAction 객체의 컬렉션 ex) click()

ViewAssertions

- ViewInteraction.check() 메서드에 전달할 수 있는 ViewAssertion 객체의 컬렉션

- 대부분 뷰 매처를 사용하여 현재 선택된 뷰의 상태를 어설션하는 matches 어설션을 사용

```kotlin
// withId(R.id.my_view) is a ViewMatcher // click() is a ViewAction // matches(isDisplayed()) is a ViewAssertion 
onView(withId(R.id.my_view)).perform(click())
    .check(matches(isDisplayed()))
```

## 기본적인 Espresso 메소드 뷰 찾기

onView(): id에 해당하는 뷰를 찾는 메소드

예제 코드에서도 확인할 수 있듯 Espresso는 뷰의 id를 기준으로 검색합니다. 하지만 특정 뷰에 R.id가 없거나 고유하지 않을 경우 에러가 발생할 수 있습니다.

```kotlin
androidx.test.espresso.AmbiguousViewMatcherException 
```

이럴 때는 뷰 중 하나에 "Hello!"라는 텍스트가 존재한다고 가정하면 다음과 같은 코드로 조합 매처를 통해 검색 범위를 좁힐 수 있습니다.

```kotlin
onView(allOf(withId(R.id.my_view), withText("Hello!")))
```

ViewActions

perform(): ViewAction을 실행합니다.

onView(...).perform(click()) // 클릭 onView(...).perform(typeText("Hello"), click()) // Hello를 입력 후 클릭

// scrollTo()는 ScrollView(세로 또는 가로) 내부에 있다면 뷰가 표시되어야 하는 작업 앞에 // scrollTo()를 사용하는 것이 좋습니다. onView(...).perform(
scrollTo(), click()) // 스크롤하여 뷰 표시 후 클릭

ViewAssertion

check(): 현재 선택된 뷰에 어설션을 적용할 수 있습니다. ViewMatcher객체를 사용하여 현재 선택된 뷰의 상태를 어설션합니다.

## 어댑터 뷰에서 데이터 로드 확인

AdapterView는 어댑터에서 동적으로 데이터를 로드하는 특수한 위젯 유형입입니다. AdapterView의 가장 일반적인 예는 ListView입니다. LinearLayout과 같은 정적 위젯과는 대조적으로
AdapterView 하위 요소의 하위 집합만 현재 뷰 계층 구조에 로드할 수 있습니다. 단순 onView() 검색은 현재 로드되지 않은 뷰를 찾지 않습니다.

Espresso는 문제의 어댑터 항목을 먼저 로드할 수 있는 별도의 onData() 진입점을 제공함으로써 이를 처리하여 이 위젯 또는 하위 요소에서 작업하기 전에 이 위젯에 포커스를 가져옵니다.

참고: 처음에 화면에 표시되는 어댑터 뷰의 항목은 이미 로드되었으므로 이러한 항목에 관한 onData() 로드 작업을 우회하도록 선택할 수 있습니다. 그러나 항상 onData()를 사용하는 것이 더 안전합니다.
경고: AdapterView의 맞춤 구현이 상속 계약, 특히 getItem() API를 위반하면 onData() 메서드에 문제가 발생할 수 있습니다. 이 경우 가장 좋은 조치는 애플리케이션 코드를 리팩터링하는
것입니다. 그렇게 할 수 없다면 일치하는 맞춤 AdapterViewProtocol을 구현할 수 있습니다. 자세한 내용은 Espresso에서 제공하는 기본 AdapterViewProtocols 클래스를 참조하세요.

### 어댑터 뷰 사용 예시

ListView가 매우 길다고 할 때 요소를 뷰 계층 구조에 제공하지 않을 수 있습니다. 여기서는 onData()를 사용하여 원하는 요소를 뷰 계층 구조에 강제로 적용합니다. Data Item이 문자열일 때 "
Americano"  문자열과 동일한 항목을 일치시키는 예시입니다.

```kotlin
onData(allOf(`is`(instanceOf(String::class.java)), `is`("Americano"))).perform(click())
```

## Espresso 유휴 리소스

Espresso는 백그라운드 스레드에서 실행 중인 작업을 포함하여 다른 비동기 작업을 인식하지 못하므로 이러한 상황에서 동기화를 보장할 수 없습니다. Espresson가 앱의 장기 실행 작업을 인식하게 하려면 각
작업을 유휴 리소스로 등록해야 합니다.

앱의 비동기 작업 결과를 테스트할 때 유휴 리소스를 사용하지 않으면 테스트의 신뢰성을 향상하기 위해 다음과 같은 잘못된 해결 방법 중 하나를 사용해야 할 수도 있습니다. ex) Thread.sleep() 호출 추가,
재시도 래퍼 구현, CountDownLatch 인스턴스 사용 등  (이유)

Espresso를 사용하면 이러한 신뢰할 수 없는 해결 방법을 테스트에서 삭제하고 대신 앱의 비동기 작업을 유휴 리소스로 등록할 수 있습니다.

사용 사례

인터넷 또는 로컬 데이터 소스에서 데이터 로드 데이터베이스 및 콜백과의 연결 설정 시스템 서비스 또는 IntentService 인스턴스를 사용하여 서비스 관리 복잡한 비즈니스 로직 실행(예: 비트맵 변환)
유휴 리소스 구현 예시

- CountingIdlingResource

활성 작업의 카운터를 유지 관리합니다. 카운터가 0이면 연결된 리소스가 유휴 상태로 간주됩니다. 이 기능은 Semaphore의 기능과 매우 유사합니다. 대부분의 경우 테스트 중 앱의 비동기 작업을 관리하는 데는 이
구현으로 충분합니다.

- UriIdlingResource

CountingIdlingResouce와 유사하지만 리소스가 유휴 상태로 간주되려면 먼저 특정 기간동안 카운터가 0 이어야 합니다. 이 추가 대기 기간은 스레드의 앱이 이전 요청의 응답을 수신한 후 즉시 새로운
요청할 수 있는 연속 네트워크 요청을 고려합니다.

- IdlingThreadPoolExecutor

생성된 스레드 풀 내에서 실행 중인 작업의 총수를 추적하는 ThreadPoolExecutor의 맞춤 구현입니다. 이 클래스는 CountingIdlingResource를 사용하여 활성 작업의 카운터를 유지 관리합니다.

- IdlingScheduledThreadPoolExecutor

ScheduledThreadPoolExecutor의 맞춤 구현입니다. 이 구현은 IdlingThreadPoolExecutor 클래스와 동일한 기능을 제공하지만 나중에 실행되거나 주기적으로 실행되도록 예약된 작업을
추적할 수도 있습니다.

참고: 이러한 유휴 리소스 구현과 관련된 동기화 이점은 Espresso가 리소스의 isIdleNow() 메서드를 처음으로 호출한 후에만 적용됩니다. 따라서 이러한 유휴 리소스는 필요하기 전에 먼저 등록해야 합니다.

자체 유휴 리소스 만들기 앱 테스트에서 유휴 리소스를 사용할 때 맞춤 리소스 관리 또는 로깅을 제공해야 할 수도 있습니다. 이 경우 이전 섹션에 나열된 구현 만으로 충분하지 않을 수 있습니다. 충분하지 않으면 이러한
유휴 리소스 구현 중 하나를 확장하거나 자체 구현을 만들어야 합니다.

자체 유휴 리소스 기능을 구현하는 경우 권장사항, 특히 첫 번째 권장사항을 유의해야 합니다.

1. 유휴 겸사 외부에서 유휴 상태로의 전환을 호출합니다.

앱이 유휴 상태가 되면 isIdleNow() 구현 외부에서 onTransitionToIdle()을 호출하세요. 이렇게 하면 Espresson가 주어진 유휴 리소스가 유휴 상태인지 판별하기 위해 불필요한 두 번째
검사를 하지 않습니다.

```kotlin
fun isIdle() { // DON'T call callback.onTransitionToIdle() here!
}

fun backgroundWorkDone() { // Background work finished. callback.onTransitionToIdle() // Good. Tells Espresso that the
    app is idle.

// Don't do any post-processing work beyond this point. Espresso now // considers your app to be idle and moves on to
    the next test action.
}
```

2. 필요하기 전에 유휴 리소스를 등록합니다.

유휴 리소스와 관련된 동기화 이점은 Espresso가 리소스의 isIdleNow() 메서드를 처음으로 호출한 후에만 적용됩니다.

3. 유휴 리소스 사용을 완료한 후 유휴 리소스의 등록을 취소합니다.

시스템 리소스를 절약하려면 유휴 리소스가 더 이상 필요하지 않게 되는 즉시 이 리소스의 등록을 취소해야 합니다. 예를 들어 @Before 주석이 달린 메서드에서 유휴 리소스를 등록하면 @After 주석이 달린 해당
메서드에서 이 리소스의 등록을 취소하는 것이 가장 좋습니다.

4. 유휴 레지스트리를 사용하여 유휴 리소스를 등록하고 등록 취소 합니다.

앱의 유휴 리소스에 이 컨테이너를 사용하여 필요에 따라 반복적으로 유휴 리소스를 등록 및 등록 취소하고 일관된 동작을 계속 관찰할 수 있습니다.

-> Espresso는 앱의 유휴 리소스를 배치할 수 있는 컨테이너 클래스인 IdlingRegistry를 제공합니다.

앱 테스트에서 유휴 레지스트리에 포함된 유휴 리소스 대신 IdlingRegistry의 참조를 만듭니다. 각 빌드 변형에 사용하는 유휴 리소스 컬렉션의 차이를 유지합니다. 앱 서비스를 참조하는 UI 구성요소가 아니라
앱 서비스에서 유휴 리소스를 정의합니다.

5. 단순 앱 상태만 유휴 리소스 내에서 유지 관리합니다.

예를 들어 구현하고 등록하는 유휴 리소스에 View 객체의 참조를 포함해서는 안 됩니다.