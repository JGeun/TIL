### 정리 배경
이번 드로이드 나이츠 2023에 참여해서 Track1 Marton Braun 님이 발표하신 "Git Good with Android Studio"을 듣게 되었습니다. 그 중에서도 Intelij에서 제공되는 "New ChangeList"가 굉장히 편리해보여서 어떻게 활용할 수 있는지 정리해보고자 합니다.


### New ChangeList란??
Intelij 기반 IDE에서 commit 탭을 열어주면 Changes라는 항목이 있습니다. 해당 항목의 옵션을 열어보면 다음과 같이 New ChangeList, Delete ChangeList가 나오는 것을 보실 수 있습니다.

<img height="300" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2Fu6SrQ%2Fbtsuf0pfp9U%2Fph7MqzUflAyBUSlV7dKRg1%2Fimg.png"/>

Intelij에 따르면 ChangeList를 다음과 같이 설명합니다.

> Changelists are displayed in the Changes view. Initially, there is a single default changelist called Changes. All new changes are automatically placed in the Changes changelist. There is also an Unversioned Files changelist that groups newly created files that haven't been added to your VCS yet.

> Changelists는 Changes view에 보여집니다. 처음에는 Changes라는 기본 changelist가 존재합니다. 모든 새로운 changes는 자동적으로 Changes changelist에 배치됩니다. VCS에 아직 추가되지 않은 새로 생성된 파일을 그룹화하는 Unversioned Files 변경 목록도 있습니다.



작업을 하다보면 A작업에 대한 수정과 B작업에 대한 수정이 혼재되어 어떤 수정사항이 특정 작업과 연관되는지 정리하기 어려울 때가 있습니다. 이럴 때 changelist로 관리할 수도 있고 changelist를 바로 커밋했을 때 커밋 메세지명이 changelist의 name과 동일하게 들어가서 간편합니다.

예시로 커밋을 만들어 보겠습니다.


우선 VersionFile을 만들어서 libraryVersion 객체를 만들었습니다.

<img height="50" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FIstkq%2FbtsudNjMyhr%2FystdCkQghGRbSNrK6AQQl0%2Fimg.png"/>

제가 해야할 작업은 버전을 올리는 것과 새로운 파일을 만들어서 출력하는 것이라고 가정합니다.

다음과 같이 작업을 진행하면 이 파일이 어떤 작업으로 인해 생성 및 수정이 되었는지 파악하기 쉽지 않습니다.

<img height="50" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbWJHGQ%2FbtsuehSvnCy%2FNkBZhzRJ4prma55WrictLk%2Fimg.png"/>

따라서 ChangeList를 사용해서 각 파일이 어떤 작업을 위해 생성 및 수정되었는지 분류할 수 있기 때문에 가독성이 좋아집니다.

<img height="50" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbWJHGQ%2FbtsuehSvnCy%2FNkBZhzRJ4prma55WrictLk%2Fimg.png"/>

뿐만 아니라 해당 changelist를 active 시켜주면 자동으로 해당 changelist의 name으로 커밋 메시지가 설정되는 것을 확인할 수 있습니다.

<img height="70" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FbVU709%2Fbtst7ynn9bD%2FkvkNxLF75aV5dyKRiaEqPK%2Fimg.png"/>

이렇게 커밋 작업을 하면 매번 커밋할 때의 번거로움을 해결할 수 있습니다.

<img height="50" src="https://img1.daumcdn.net/thumb/R1280x0/?scode=mtistory2&fname=https%3A%2F%2Fblog.kakaocdn.net%2Fdn%2FpEgBX%2FbtsudgzGj7R%2FI5kElO1K30akF6PICoOXHK%2Fimg.png"/>
