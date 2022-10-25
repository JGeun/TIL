### <배경>

최근 지인으로 부터 질문을 받았는데 특정 서비스를 만들면서 사람들이 핸드폰을 꺼도 동작할 수 있게 끔 Background Service를 통해 구현했는데 잘 되지 않는다. 는 
이야기를 듣게 되었습니다. Android를 처음 시작하는 대부분의 사람들은 보통 Android 서비스가 사람들이 보이지 않는 뒤에서 돌아가기 때문에 Background Service라고 생각하기 쉽습니다.
하지만 이는 잘못된 생각입니다. 

### <올바른 접근>
1. 사용자 편의성을 위해서 백그라운드로도 동작할 수 있도록, 걷는 동안 휴대폰 화면을 계속해서 제일 위에 켜놓는게 불편하여 백그라운드로 구현을 했다고 말씀해주셨었는데
    멜론앱같은 경우 Foreground를 통해서 사용자가 멜론앱을 종료시키고, 화면을 꺼논다고 해도 음악 재생이 가능합니다. 즉 사용자에게 Notification 형태로 보여주면서 Foreground 서비스로 접근하는 것이 올바른 접근입니다.
   

2. 백그라운드 같은 경우 Android 26부터 자체적으로 핸드폰 성능, 배터리 문제로 인해 서비스에 제한을 두고 있습니다.
   https://developer.android.com/about/versions/oreo/background?hl=ko#broadcasts
   <br/><br/>
   롱타임으로 백그라운드 작업을 하는 것이 아닌 위치정보엑세스 하는 수준으로만 접근하는 것이 맞습니다.
   https://developer.android.com/training/location/permissions?hl=ko
   

3. 마지막으로 앱 권한 설정에 따라 접근할 수 있는 service가 달라집니다.
   https://developer.android.com/training/location/permissions?hl=ko#request-background-location
   <br/><br/>
   백그라운드에 접근하기 위해서는 "항상 허용"이라는 옵션을 포함해야 합니다.
   https://velog.io/@jaeyunn_15/Android-Android-%EC%9C%84%EC%B9%98-%EA%B6%8C%ED%95%9C-%EB%B3%80%EA%B2%BD-%EC%82%AC%ED%95%AD
   <br/><br/>
   따라서 사용자가 위치설정권한을 바꾸면 앱이 멈추는게 당연하다고 생각합니다.