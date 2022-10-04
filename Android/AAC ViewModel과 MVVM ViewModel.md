### MVVM ViewModel이란?

MVVM 패턴 (Model - View - ViewModel)은 MVP 패턴에서 파생된 패턴입니다. MVVM 패턴의 목표는 비즈니스 로직과 프레젠테이션 로직을 UI로 부터 분리하는 것입니다. 비즈니스 로직과 프레젠테이션 로직을 UI로 부터 분리하게 되면 테스트, 유지 보수, 재사용 측면에서 용이합니다.

![MVVM ViewModel](./img/MVVM%20ViewModel.png)c

기존의 MVP 패턴 (View - Presenter - Model)에서 View와 Presenter는 1:1 관계로 View에서 요청한 정보로 Model을 가공하여 View에 전달해줍니다. 즉 연결다리 역할을 하는 것입니다. 이러면 View와 Presenter 사이의 의존성이 높아져 문제가 발생합니다. 반대로 MVVM 패턴은 View 와 ViewModel 사이의 의존성이 없습니다. 따라서 테스트, 유지 보수, 재사용 측면에서 용이합니다.

### AAC ViewModel 이란?

공식문서에 의하면 ViewModel 클래스는 수명 주기를 고려하여 UI 관련 데이터를 저장하고 관리하도록 설계되었습니다.  ViewModel 클래스를 사용하면 화면 회전과 같이 구성을 변경할 때도 데이터를 유지할 수 있습니다.
즉 앱의 Lifecycle을 고려하여 UI 관련 데이터를 저장하고 관리하는 역할을 합니다.

AAC ViewModel 이란?
공식문서에 의하면 ViewModel 클래스는 수명 주기를 고려하여 UI 관련 데이터를 저장하고 관리하도록 설계되었습니다.  ViewModel 클래스를 사용하면 화면 회전과 같이 구성을 변경할 때도 데이터를 유지할 수 있습니다.
즉 앱의 Lifecycle을 고려하여 UI 관련 데이터를 저장하고 관리하는 역할을 합니다.

![AAC ViewModel LifeCycle](./img/AAC%20ViewModel%20LifeCycle.png)


ViewModel 객체의 범위는 ViewModel을 가져올 때 ViewModelProvider에 전달되는 Lifecycle로 지정됩니다. ViewModel은 범위가 지정된 Lifecycle이 영구적으로 경과될 때까지, 즉 활동이 끝날 때까지 메모리에 남아있습니다.


### MVVM ViewModel vs AAC ViewModel

간단하게 요약하자면 MVVM의 ViewModel은 View에 필요한 데이터를 관리하여 바인딩 해주고, 비즈니스 로직을 담당해 처리하는 요소입니다. 반면 AAC의 ViewModel은 Android의 생명주기를 고려하여 UI 관련 데이터를 저장하고 관리하는 요소로 요약할 수 있습니다.


실제로 저같은 경우도 AAC ViewModel을 사용하고 MVVM 패턴을 적용하였다고 생각한 적이 있기에 실제로 구분해서 이해할 필요가 있습니다. 이렇게 오해하는 이유는 AAC의 ViewModel로 MVVM 패턴의 ViewModel을 구현할 수 있기 때문입니다. ViewModel 내에서 ObservableField나 LiveData, StateFlow 등을 사용하여 데이터 바인딩을 해준다면 MVVM 패턴의 ViewModel로써 사용가능합니다.
ViewModel 객체의 범위는 ViewModel을 가져올 때 ViewModelProvider에 전달되는 Lifecycle로 지정됩니다. ViewModel은 범위가 지정된 Lifecycle이 영구적으로 경과될 때까지, 즉 활동이 끝날 때까지 메모리에 남아있습니다.