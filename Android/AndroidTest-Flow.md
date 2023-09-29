_블로그 링크: https://jgeun97.tistory.com/340

## fake producer 만들기

테스트 대상이 흐름의 소비자인 경우 한 가지 일반적인 테스트 방법은 생산자를 fake로 대체하는 것입니다.

예를 들어 프로덕션 환경의 두 데이터 소스에서 데이터를 가져오는 저장소를 식별하는 클래스가 있다고 가정해 보겠습니다.

확정된 테스트를 만들려면 저장소와 종속 항목을 항상 동일한 모조 데이터를 내보내는 모조 저장소로 대체하면 됩니다.

테스트에서 Fake Repository를 삽입하여 실제 구현을 대체합니다.

```kotlin
@Test
fun myTest() {
    // Given a class with fake dependencies:
    val sut = MyUnitUnderTest(MyFakeRepository())
    // Trigger and verify
    // ...
}
```

## 테스트에서 flow emissions 어설션하기

테스트 대상이 흐름을 노출하는 경우에는 테스트에서 데이터 스트림 요소에 관해 어설션을 만들어야 합니다.

특정 테스트에서는 흐름에서 내보낸 항목 중 첫 번째 항목 또는 일정 수의 항목만 확인하면 됩니다.

first()를 호출하여 첫 번째 항목을 흐름에 사용할 수 있습니다. 다음 함수는 첫 번째 항목이 수신될 때까지 대기한 후 생산자에게 취소 신호를 전송합니다.

```kotlin
@Test
fun myRepositoryTest() = runTest {
        // Given a repository that combines values from two data sources:
        val repository = MyRepository(fakeSource1, fakeSource2)

        // When the repository emits a value
        val firstItem = repository.counter.first() // Returns the first item in the flow

        // Then check it's the expected item
        assertEquals(ITEM_1, firstItem)
    }
```

테스트에서 여러 값을 확인해야 하는 경우 toList()를 호출하면 흐름은 소스가 모든 값을 내보낼 때까지 대기했다가 이 값을 목록으로 반환합니다. 이 방법은 유한한 데이터 스트림에서만 작동합니다.

테스트에서 fake를 사용하면 연속적으로 Repository에서 값을 수신하는 수집 코루틴을 만들 수 있습니다. 여기서 Repository에서 노출한 흐름은 완료되지 않으므로 값을 수집하는 toList 함수는
반환되지 않습니다. TestScope.backgroundScope에서 collecting하면 테스트가 종료되기 전에 코루틴이 취소됩니다. 그렇지 않으면 runTest가 완료될 때까지 계속 대기하여 테스트가 응답을
중지하고 최종적으로 실패합니다.

```kotlin
class Repository(private val dataSource: DataSource) {
    fun scores(): Flow<Int> {
        return dataSource.counts().map { it * 10 }
    }
}

class FakeDataSource : DataSource {
    private val flow = MutableSharedFlow<Int>()
    suspend fun emit(value: Int) = flow.emit(value)
    override fun counts(): Flow<Int> = flow
}

@Test
fun continuouslyCollect() = runTest {
    val dataSource = FakeDataSource()
    val repository = Repository(dataSource)

    val values = mutableListOf<Int>()
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        repository.scores().toList(values)
    }

    dataSource.emit(1)
    assertEquals(10, values[0]) // Assert on the list contents

    dataSource.emit(2)
    dataSource.emit(3)
    assertEquals(30, values[2])

    assertEquals(3, values.size) // Assert the number of items collected

}
```

## StateFlow 테스트

일반적으로 StateFlow를 데이터 홀더로 취급하고 value 속성에 어설션하는 것이 좋습니다. 이렇게 하면 테스트가 특정 시점에 객체의 현재 상태를 확인하며 혼합이 발생하는지 여부에 따라 달라지지 않습니다.

예를 들어 Repository에서 값을 수집하고 StateFlow의 UI에 노출하는 ViewModel을 살펴보겠습니다.

```kotlin
class MyViewModel(private val myRepository: MyRepository) : ViewModel() {
    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score.asStateFlow()

    fun initialize() {
        viewModelScope.launch {
            myRepository.scores().collect { score ->
                _score.value = score
            }
        }
    }

}
```

이 Repository에 관한 fake는 다음과 같습니다.

```kotlin
class FakeRepository : MyRepository {
    private val flow = MutableSharedFlow<Int>()
    suspend fun emit(value: Int) = flow.emit(value)
    override fun scores(): Flow<Int> = flow
}
```

이 모조 구현을 사용하는 ViewModel을 테스트할 때 모조 구현에서 값을 내보내 ViewModel의 StateFlow에서 업데이트를 트리거한 다음 업데이트된 value에서 어설션할 수 있습니다.

```kotlin
@Test
fun testHotFakeRepository() = runTest {
        val fakeRepository = FakeRepository()
        val viewModel = MyViewModel(fakeRepository)

        assertEquals(0, viewModel.score.value) // Assert on the initial value

        // Start collecting values from the Repository
        viewModel.initialize()

        // Then we can send in values one by one, which the ViewModel will collect
        fakeRepository.emit(1)
        assertEquals(1, viewModel.score.value)

        fakeRepository.emit(2)
        fakeRepository.emit(3)
        assertEquals(3, viewModel.score.value) // Assert on the latest value

    }
```

## stateIn으로 생성된 StateFlow 테스트

backgroundScope를 사용해서 테스트하면 됩니다!

```kotlin
class MyViewModelWithStateIn(myRepository: MyRepository) : ViewModel() {
    val score: StateFlow<Int> = myRepository.scores()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000L), 0)
}

@Test
fun testLazilySharingViewModel() = runTest {
    val fakeRepository = HotFakeRepository()
    val viewModel = MyViewModelWithStateIn(fakeRepository)

    // Create an empty collector for the StateFlow
    backgroundScope.launch(UnconfinedTestDispatcher(testScheduler)) {
        viewModel.score.collect()
    }

    assertEquals(0, viewModel.score.value) // Can assert initial value

    // Trigger-assert like before
    fakeRepository.emit(1)
    assertEquals(1, viewModel.score.value)

    fakeRepository.emit(2)
    fakeRepository.emit(3)
    assertEquals(3, viewModel.score.value)

}
```