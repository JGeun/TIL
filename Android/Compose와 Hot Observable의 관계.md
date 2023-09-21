## 배경

스낵바나 토스트처럼 매번 노출되어야 하는 컴포넌트들을 위해서 SharedFlow를 사용했었는데 Compose에서 collect되지 않는 문제가 발생했습니다. 그 이유를 하나하나 파악해 보고자 합니다.

원인을 파악하기에 전에 잠깐!
제가 앞으로 테스트하는 환경의 ViewModel은 다음과 같이 통일하겠습니다

```kotlin
class MainViewModel : ViewModel() {

    private val _testFlow = MutableSharedFlow<String>(replay = 1)
    val testFlow = _testFlow.asSharedFlow()

    fun inputTest(value: String) {
        viewModelScope.launch {
            _testFlow.emit(value)
        }
    }
}
```

## XML + SharedFlow

컴포즈에서 테스트하기 앞서 xml과 sharedflow를 사용해서 Toast Message를 띄우는 예제를 만들어보겠습니다.

MainActivity의 코드는 버튼을 클릭했을 때 textList의 문자를 순차적으로 _testFlow에서 emit할 수 있도록 구현했습니다.

````kotlin
class MainActivity : AppCompatActivity() {

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val viewModel by viewModels<MainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val textList = listOf("first", "first", "second")
        var index = 0

        binding.flowInputBtn.setOnClickListener {
            viewModel.inputTest(textList[index])
            index = if (index + 1 > textList.lastIndex) 0 else index + 1
        }

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.testFlow.collect {
                    binding.flowText.text = it

                    Toast.makeText(this@MainActivity, it, Toast.LENGTH_SHORT).show()
                }

            }
        }
    }
}
````

실행시켜보면 같은 값이 "first"가 두번 들어와도 잘 실행되는 것을 확인할 수 있습니다.

## Compose + SharedFlow

이번에도 거의 동일한 UI를 만들었지만 차이점은 testList에 전부 "first"만 넣었고 토스트 대신 로그가 찍히게 만들었습니다. 아까와 같다면 first가 계속 collect 되어야 할텐데 과연 동일하게
나올지 확인해보겠습니다.

```kotlin
@Composable
fun TestScreen(
    viewModel: MainViewModel = viewModel<MainViewModel>()
) {
    val textList = listOf("first", "first", "first")
    var index by remember { mutableStateOf<Int>(0) }
    val testText by viewModel.testFlow.collectAsStateWithLifecycle(null)
    Log.e("Collect Text", testText ?: "null")

    Column {
        Button(
            onClick = {
                viewModel.inputTest(textList[index])
                index = if (index + 1 > textList.lastIndex) 0 else index + 1
            }
        ) {
            Text(text = "input")
        }
    }
}

```

놀랍게도 first가 한번만 collect되는 것을 볼 수 있었습니다.

## Compose + SharedFlow - emit 2번하기

이번에는 viewModel에 변형을 줘서 emit을 두번 해주겠습니다.

```kotlin
viewModelScope.launch {
    _testFlow.emit(value + "1")
    _testFlow.emit(value + "2")
}
```

Compose 일 때는 마지막으로 방출된 경우만 collect 되고 있습니다.

이걸 보면서 저는 이러면 SharedFlow를 쓰는 의미가 없는데..? 라고 생각하고 있었습니다.

이제부터 왜 이런 경우가 발생하는지 탐구해보겠습니다.

## 의심스러운 collectAsStateWithLifeCycle

> collectAsStateWithLifeCycle 메소드를 파악해보자라고 생각해본 순간 주석에 딱!! 적혀있습니다.
>
> Collects values from this Flow and represents its latest value via State in a lifecycle-aware manner.
>
>Flow로부터 values를 수집하고 가장 마지막 value만을 수명 주기를 인식할 수 있는 방식으로 State를 통해 최신 값을 나타냅니다.
>
>Every time there would be new value posted into the Flow the returned State will be updated causing recomposition of
> every State.value usage whenever the lifecycleOwner's lifecycle is at least minActiveState.
>
>흐름에 새 값이 게시될 때마다 반환된 상태가 업데이트되어 lifecycleOwner의 수명 주기가 최소 minActiveState일 때마다 모든 State.value 사용이 재구성됩니다.
> collectAsStateWithLifeCycle의 구성을 보면 다음과 같습니다. 만약 해당 코드를 보고서 collect 여러번 하는데 왜 최신 값만 나오는거지? 라고 생각하셨다면 produceState에 대한
> 이해가 필요한 걸수도 있습니다. (제가 처음에 보고 이게 왜..? 라는 생각을 했습니다 ㅎㅎ)

```kotlin
@Composable
fun <T> Flow<T>.collectAsStateWithLifecycle(
    initialValue: T,
    lifecycle: Lifecycle,
    minActiveState: Lifecycle.State = Lifecycle.State.STARTED,
    context: CoroutineContext = EmptyCoroutineContext
): State<T> {
    return produceState(initialValue, this, lifecycle, minActiveState, context) {
        lifecycle.repeatOnLifecycle(minActiveState) {
            if (context == EmptyCoroutineContext) {
                this@collectAsStateWithLifecycle.collect { this@produceState.value = it }
            } else withContext(context) {
                this@collectAsStateWithLifecycle.collect { this@produceState.value = it }
            }
        }
    }
}
```

## produceState란?

> Return an observable snapshot State that produces values over time from keys.
>
>시간이 지남에 따라 키에서 값을 생성하는 관찰 가능한 스냅샷 상태를 반환합니다.
>
>producer is launched when produceState enters the composition and is cancelled when produceState leaves the
> composition. If keys change, a running producer will be cancelled and re-launched for the new source. producer should
> use ProduceStateScope.value to set new values on the returned State.
>
>producer는 producerState가 컴포지션에 들어갈 때 시작되고 producerState가 컴포지션을 떠나면 취소됩니다. 키가 변경되면 실행 중인 생산자가 취소되고 새 소스에 대해 다시 시작됩니다.
> 생산자는 ProduceStateScope.value를 사용하여 반환된 State에 새 값을 설정해야 합니다.
>
>The returned State conflates values; no change will be observable if ProduceStateScope.value is used to set a value
> that is equal to its old value, and observers may only see the latest value if several values are set in rapid
> succession.
> 반환된 State는 값을 병합합니다. ProduceStateScope.value를 사용하여 이전 값과 동일한 값을 설정하는 경우 변경 사항을 관찰할 수 없으며, 여러 값이 빠르게 연속적으로 설정된 경우 관찰자는 최신
> 값만 볼 수 있습니다.
> 즉 마지막 문구에 적혀있듯이 collect는 되지만 이전 값과 동일한 값을 설정하는 경우 변경 사항을 관찰할 수 없으며, 여러 값이 빠르게 연속적으로 설정된 경우에는 관찰자는 최신 값만 볼 수 있습니다. 이는
> 리컴포지션을 최소로 발생시키기 위함이라고도 볼 수 있습니다.

## 만약 매번 collect하고 싶으면 어떻게 할까?

매번 collect를 하고 싶다면 2가지 방법이 있습니다.

1. 다른 값 emit -> delay -> 원하는 값 emit

예시코드처럼 충분한 delay만 준다면 무시되는 케이스는 거의 드물 것입니다.

```kotlin
viewModelScope.launch {
    _testFlow.emit(value + "1")
    delay(100L)
    _testFlow.emit(value + "2")
}
```

2. lifecycleOwner를 사용하는 방법

Activity에서 매번 collect가 성공한 것처럼 똑같은 환경을 제공하면 됩니다. 하지만 이렇게 했을 경우 Recomposition 단계에서 emit되는 값들이 손실될 가능성이 있습니다.

```kotlin
val lifecycleOwner = LocalLifecycleOwner.current

LaunchedEffect(viewModel.testFlow, lifecycleOwner) {
    lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
        viewModel.testFlow.collect { value ->
// value 사용
        }
    }
}
```

## 결론
Compose에서는 리컴포지션이 성능을 좌우하기 때문에 상태가 최소한으로 변경되는 것을 중시합니다. 따라서 collectAsState를 사용하면 최신값이면서 이전과 다른 값 만을 collect하게 됩니다.
Compose를 개발하다보면 XML로 개발할 때처럼 당연시 생각되는 것들이 있는데 항상 상태 라는 것에 초점을 맞춰서 개발을 해야할 것 같습니다.