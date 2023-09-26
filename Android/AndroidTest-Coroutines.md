## 코루틴 테스트를 하기 앞서 알아둬야할 것들

코루틴을 사용하는 단위 테스트 코드는 주의가 필요합니다. 비동기로 실행될 수 있고 여러 스레드에서 발생할 수 있기 때문입니다.

테스트를 시작하기 앞서 테스트에서 라이브러리를 추가해줘야 합니다.

```kotlin
testImplementation "org.jetbrains.kotlinx:kotlinx-coroutines-test:$coroutines_version"
```

코루틴을 테스트할 때는 테스트용 코루틴 빌더인 runTest를 사용합니다. runTest에서 테스트 코드를 래핑하면 기본 정지 함수를 테스트할 수 있고 코루틴의 지연을 자동으로 건너뛰므로 아래의 테스트가 1초보다
훨씬 빠르게 완료됩니다.

```kotlin
suspend fun fetchData(): String {
    delay(1000L)
    return "Hello world"
}

@Test
fun dataShouldBeHelloWorld() = runTest {
    val data = fetchData()
    assertEquals("Hello world", data)
}
```

그러나 테스트 중인 코드에서 발생하는 상황에 따라 추가로 고려해야할 사항이 있습니다.

코드가 runTest에서 만드는 최상위 테스트 코루틴 외에 새 코루틴을 만들 때 적절한 TestDispatcher를 선택하여 새 코루틴이 예약되는 방식을 제어해야 합니다.
코드가 코루틴 실행을 다른 디스패처로 이동하면 (ex: withContext) runTest는 일반적으로 계속 작동하지만 지연을 더 이상 건너뛰지 않으며 테스트는 여러 스레드에서 실행되므로 예측 가능성이 떨어집니다.
이러한 이유로 테스트에서 실제 디스패처를 교체하려면 테스트 디스패처를 삽입해야 합니다.

## TestDispatcher란 무엇일까

TestDispatcher는 테스트 목적으로 사용하는 CoroutineDispatcher 구현입니다. 새 코루틴의 실행을 예측할 수 있도록 테스트 중에 새 코루틴을 만드는 경우 TestDispatchers를 사용해야
합니다.

사용할 수 있는 TestDispatcher 구현에는 StandardTestDispatcher, UnconfinedTestDispatcher 가 존재합니다. 이 두 가지는 새로 시작된 코루틴의 예약을 다르게
실행합니다. 둘 다 TestCoroutineScheduler를 사용하여 가상 시간을 제어하고 테스트 내에서 실행 중인 코루틴을 관리합니다.

테스트에서 사용하는 스케줄러 인스턴스는 하나만 있어야 하며 모든 TestDispatchers 간에 공유되어야 합니다.

최상위 테스트 코루틴을 시작하기 위해 runTest는 TestScope를 만듭니다. runTest는 TestScope의 디스패처에서 사용하는 스케줄러의 대기열에 추가되는 코루틴을 추적하고 이 스케줄러에 대기 중인
작업이 있는 한 반환하지 않습니다.

### StandardTestDispatcher

새 코루틴을 시작하면 코루틴이 기본 스케줄러의 대기열에 추가되어 테스트 스레드를 사용할 수 있을 때마다 실행
자체적으로 작업을 실행하지 않고 항상 스케줄러에게 작업을 전달
runTest는 기본적으로 StandartTestDispatcher를 사용
실행 순서에 대해 완전한 제어 가능하지만, 코루틴이 자동으로 실행X
코루틴의 실행 순서나 디테일한 제어가 필요한 테스트에 적합

```kotlin
@Test
fun standardTest() = runTest {
        val userRepo = UserRepository()

        launch { userRepo.register("Alice") }
        launch { userRepo.register("Bob") }

        assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ❌ Fails

    }
```

아래의 테스트 코드를 실행해보면 테스트가 실패했다는 결과가 나옵니다.

테스트 스레드가 최상위 테스트 코루틴을 실행하는 동안 생성되지 않으면, 모든 새 코루틴은 테스트 코루틴이 완료된 후에만 실행되기 때문입니다. (runTest가 반환하기 전)

대기열에 추가된 코루틴이 실행되도록 테스트 코루틴을 생성하는 방법도 있습니다.

- advanceUntilIdle : 코루틴이 모두 실행되도록 대기열에 남은 항목이 없을 때까지 스케줄러에서 다른 코루틴을 모두 실행.
- advanceTimeBy : 주어진 시간을 진행하고 해당 지점 전에 실행되도록 예약된 코루틴을 실행.
- runCurrent : 예약된 코루틴을 실행. 아직 디스패치 않은 작업을 수행.

```kotlin
@Test
fun standardTest() = runTest {
        val userRepo = UserRepository()

        launch { userRepo.register("Alice") }
        launch { userRepo.register("Bob") }
        advanceUntilIdle() // Yields to perform the registrations

        assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ✅ Passes

    }
```

### UnconfinedTestDispatcher

새 코루틴이 UnconfinedTestDispatcher에서 시작되면 현재 스레드에서 빠르게 시작
즉 코루틴 빌더가 반환될 때까지 기다리지 않고 즉시 실행
새 코루틴이 실행되도록 테스트 스레드를 수동으로 생성하지 않아도 되기 때문에, 새 코루틴을 빠르게 실행하며 코루틴을 사용한 간단한 테스트에 적합
advanceUntilIdle, runCurrent 같은 함수를 호출할 필요X
실행 순서에 대한 완전한 제어가 불가능하지만, 코루틴이 자동으로 실행
이 디스패처를 runTest의 최상위 테스트 코루틴에 기본 디스패처 대신 사용하려면 인스턴스를 만들어 매개변수로 전달합니다.

```kotlin
@Test
fun unconfinedTest() = runTest(UnconfinedTestDispatcher()) {
        val userRepo = UserRepository()

        launch { userRepo.register("Alice") }
        launch { userRepo.register("Bob") }

        assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ✅ Passes
    }
```

UnconfinedTestDispatcher는 새 코루틴을 빠르게 시작하지만 코루틴이 정지되면 다른 코루틴이 실행을 다시 시작합니다.

예를 들어 이 테스트 내에서 실행된 새 코루틴은 Alice를 등록하지만 delay가 호출되면 정지됩니다. 이를 통해 최상위 코루틴이 어설션을 계속 진행할 수 있고 테스트는 Bob이 아직 등록되지 않았으므로
실패합니다.

```kotlin
@Test
fun yieldingTest() = runTest(UnconfinedTestDispatcher()) {
        val userRepo = UserRepository()

        launch {
            userRepo.register("Alice")
            delay(10L)
            userRepo.register("Bob")
        }

        assertEquals(listOf("Alice", "Bob"), userRepo.getAllUsers()) // ❌ Fails

    }
```

## 테스트 디스패처 삽입

테스트 중인 코드는 디스패처를 사용하여 스레드를 전환하거나(withContext 사용) 새 코루틴을 시작할 수 있습니다.

테스트에서 이러한 디스패처를 TestDispatchers의 인스턴스로 바꿉니다. 이렇게 하면 다음과 같은 몇 가지 장점이 있습니다.

코드가 단일 테스트 스레드에서 실행되므로 테스트의 확정성이 높아집니다.
새 코루틴의 예약 및 실행 방법을 제어할 수 있습니다.
TestDispatchers는 가상 시간용 스케줄러를 사용하여 자동으로 지연을 건너뛰고 개발자가 수동으로 시간을 앞당기도록 합니다.
명시적으로 Dispatcher를 사용할 경우 원하는 테스트 결과를 얻지 못할 수 있기 때문에 주의해야 합니다. 따라서 아래 코드처럼 테스트에서 TestDispatcher 구현을 삽입하여 IO 디스패처를 대체할 수
있도록 구현해야 합니다.

```kotlin
// Example class demonstrating dispatcher use cases
class Repository(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    private val scope = CoroutineScope(ioDispatcher)
    val initialized = AtomicBoolean(false)

    // A function that starts a new coroutine on the IO dispatcher
    fun initialize() {
        scope.launch {
            initialized.set(true)
        }
    }

    // A suspending function that switches to the IO dispatcher
    suspend fun fetchData(): String = withContext(ioDispatcher) {
        require(initialized.get()) { "Repository should be initialized first" }
        delay(500L)
        "Hello world"
    }
}
```

주의: 새 코루틴이 완료될 때까지 진행하는 것이 가능한 유일한 이유는 새 코루틴이 TestDispatcher를 사용하기 때문입니다. 이를 통해 위 예시에서 initialize 메서드가 잘 설계된 API가 아님을 알
수 있습니다. 호출자가 기다려야 하는 비동기 작업을 시작하지만 이 작업이 완료되면 호출자에게 알릴 방법이 없습니다.
fetchData는 테스트 스레드에서 실행되며 테스트 중에 포함된 지연을 건너뛰므로 TestDispatcher에서 실행하는 것이 좋습니다.

```kotlin
class RepositoryTest {
    @Test
    fun repoInitWorksAndDataIsHelloWorld() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = Repository(dispatcher)

        repository.initialize()
        advanceUntilIdle() // Runs the new coroutine
        assertEquals(true, repository.initialized.get())

        val data = repository.fetchData() // No thread switch, delay is skipped
        assertEquals("Hello world", data)
    }
}
```

TestDispatcher에서 시작된 새 코루틴은 위와 같이 initialize를 사용하여 수동으로 진행할 수 있습니다. 그러나 프로덕션 코드에서는 불가능하거나 바람직하지 않습니다. 대신 이 메서드는 정지되거나(순차
실행의 경우) 또는 Deferred 값을 반환하도록(동시 실행의 경우) 다시 설계되어야 합니다.

예를 들어 async를 사용하여 새 코루틴을 시작하고 Deferred를 만들 수 있습니다.

```kotlin
class BetterRepository(private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO) {
    private val scope = CoroutineScope(ioDispatcher)

    fun initialize() = scope.async {
        // ...
    }
}
```

이를 통해 테스트와 프로덕션 코드에서 모두 이 코드의 완성을 안전하게 await할 수 있습니다.

```kotlin
@Test
fun repoInitWorks() = runTest {
        val dispatcher = StandardTestDispatcher(testScheduler)
        val repository = BetterRepository(dispatcher)

        repository.initialize().await() // Suspends until the new coroutine is done
        assertEquals(true, repository.initialized.get())
        // ...

    }
```

참고: 마찬가지로 launch를 사용하여 새 코루틴을 만들고 완료를 기다리기 위해 반환하는 Job에서 join을 호출할 수 있습니다.

## Main 디스패처 설정

로컬 단위 테스트에서 Andorid UI 스레드를 래핑하는 Main 디스패처를 사용할 수 없습니다. 이러한 테스트는 Android 기기가 아닌 로컬 JVM에서 실행되기 때문입니다. 테스트 중인 코드가 기본 스레드를
참조하면 단위 테스트 중에 예외가 발생합니다.

참고: 이는 로컬 단위 테스트에만 적용됩니다. 실제 UI 스레드가 제공되는 계측 테스트에서 Main 디스패처 교체해서는 안 됩니다.

때에 따라 다른 Dispatcher와 같은 방식으로 Main Dispatcher를 삽입하여 테스트에서 이를 TestDispatcher로 교체할 수 있습니다. 그러나 viewModelScope와 같은 일부 API는
내부적으로 하드코딩된 Main Dispatcher를 사용합니다.

```kotlin
class MainDispatcherRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }

}

class HomeViewModelTestUsingRule {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun settingMainDispatcher() = runTest { // Uses Main’s scheduler
        val viewModel = HomeViewModel()
        viewModel.loadMessage()
        assertEquals("Greetings!", viewModel.message.value)
    }

}
```