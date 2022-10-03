### 배경
보통 fragment를 사용할 때 replace를 주로 사용해왔는데  add()와 replace()가 과연 어떤 차이점이 있는지 체크해보기로 했습니다.


### add() 와 replace()의 차이
add()는 기존의 프래그먼트 위에 추가하는 것이고 replace()는 이전 프래그먼트들을 제거한 후에 새로운 프래그먼트를 추가한다는 차이점이 있습니다.


### 기존의 fragment 설정
Activity: onCreate() -> Fragment: onAttach - onCreate - onCreateView - onViewCreated - onViewStateRestored - onStart

-> Activity: onStart - onResume

### A fragment에서 B fragment로 replace()할 경우
A onPause - onStop -> B onAttach ~ onStart -> A onDestroyView - onDestroy - onDetach -> B onResume

A onPause - onStop -> B onAttach ~ onStart -> A onDestroyView - onDestroy - onDetach -> B onResume

###A fragment 위에 B fragment를 add()하고 back 버튼을 클릭할 경우 (이전 Activity로 가게될 경우)

B fragment의 onResume까지는 순서대로 실행됩니다. 하지만 back버튼을 클릭할 경우 신기하게도 B fragment보다 A fragment가 먼저 종료되는 것을 볼 수 있었습니다.

A onPause -> B onPause -> Activity onPause -> A onStop -> B onStop ->

activity: onStop - onDestroy -> A on DestroyView - onDestroy - onDetach

-> B onDestroyView - onDestroy - onDetach

### A fragment 위에 B fragment를 add() 하고 C fragment로 replace할 경우
이 때는 위의 경우와 다르게 B가 먼저 종료되었습니다.

B onPause - onStop -> A onPause - onStop -> C onAttach ~ onStart ->

B onDestroy ~ onDetach -> A onDestroy ~ onDetach -> C onResume