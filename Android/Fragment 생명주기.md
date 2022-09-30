### Fragment란?
프래그먼트란 액티비티 내에 배치되어 사용자 인터페이스를 구성하는 안드로이드 구성요소 중 하나입니다. 액티비티 내의 일부 영역을 차지하여 화면을 구성할 수 있으며 독립적으로 동작할 수 있어 매우 유용합니다.

###<프래그먼트 수명주기>

CREATED: Fragment의 생명주기가 CREATED된 상태라면 이미 onAttach()를 통해 FragmentManager에 추가된 상태입니다. 이 상태에서 데이터를 초기화,복구하거나 저장된 상태를 불러옵니다.

STARTED: Fragment 안의 View들이 표시되지만 '포커스'가 없으므로 사용자 입력에 응답할 수 없습니다.

RESUMED: Activity와 마찬가지로 사용자와 Fragment가 상호작용하는 단계입니다.

STARTED: Fragment의 onPause()를 호출하는 단계이지만 Activity와 다르게 PAUSE가 아닌 STARTED 상태입니다. 사용자로부터 Fragment의 포커스를 잃은 상태입니다.

CREATED: 상태명은 다르지만 Activity와 마찬가지로 더 이상 Fragment가 포그라운드에서 보이지 않게 되는 상태입니다.

DESTROYED:  앞에서 Fragment 안에 있는 View들이 detach되고 이제 Fragment가 FragmentManager로부터 detach되어 소멸하게 됩니다.

#### <Callback>

onCreate(): 프래그먼트가 인스턴스화되었고 CREATED 상태입니다. 그러나 이에 상응하는 뷰가 아직 만들어지지 않았습니다.

onCreateView(): 이 메서드에서 레이아웃을 확장합니다. 프래그먼트가 CREATED 상태로 전환되었습니다.

onViewCreated(): 뷰가 만들어진 후 호출됩니다. 이 메서드에서 일반적으로 findViewById()를 호출하여 특정 뷰를 속성에 바인딩합니다.

onStart(): 프래그먼트가 STARTED 상태로 전환되었습니다.

onResume(): 프래그먼트가 RESUMED 상태로 전환되었고 이제 포커스를 보유합니다(사용자 입력에 응답할 수 있음).

onPause(): 프래그먼트가 STARTED 상태로 다시 전환되었습니다. UI가 사용자에게 표시됩니다.

onStop(): 프래그먼트가 CREATED 상태로 다시 전환되었습니다. 객체가 인스턴스화되었지만 더 이상 화면에 표시되지 않습니다.

onDestroyView(): 프래그먼트가 DESTROYED 상태로 전환되기 직전에 호출됩니다. 뷰는 메모리에서 이미 삭제되었지만 프래그먼트 객체는 여전히 있습니다.

onDestroy(): 프래그먼트가 DESTROYED 상태로 전환됩니다.

#### 여기서 FragmentMananger란?
프래그먼트 관리자는 앱 프래그먼트에서 프래그먼트를 추가, 삭제 또는 교체하고 백 스택에 추가하는 등의 작업을 실행하는 클래스 입니다. Jetpack Navigation 라이브러리를 사용하는 경우 FragmentManager와의 직접적인 상호작용은 거의 필요하지 않습니다. 개발자를 대신해 이 라이브러리가 Fragment를 사용하기 때문입니다.


### Activity에서 Fragment 붙이면 생명주기는 어떻게 될까?
Activity: onCreate (Fragment 설정) ->

Fragment: onAttach -> onCreate -> onCreateView -> onViewCreated -> onStart ->

Activity: onStart -> onResume

=> Activity에서 호출한 생명주기 다음으로 Fragment가 onStart 되고난 후에 Activty가 다음 생명주기가 진행되는 것을 확인할 수 있었습니다.


### 반대로 화면이 잠깐 내려가게 되면?
Fragment: onPause -> Activity: onPause ->

Fragment: onStop -> Activty: onStop

=> 화면을 내리게 되면 Fragment, Activity 순서대로 차례차례 생명주기에 따라 종료되는 것을 확인할 수 있었습니다.

### 앱이 종료되면?
Activity: onDestroy -> Fragment: onDestroyView -> onDestroy -> onDetach

=> 화면이 종료되면 Activity가 먼저 종료되고 다음에 Fragment가 처리되는 것을 확인할 수 있었습니다.