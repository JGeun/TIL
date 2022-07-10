Postman을 이용하여 Firebase Cloud Message(FCM) 푸쉬 알림을 보내보도록 하겠습니다.

Postman은 https://www.postman.com/  에서 다운받으실 수 있습니다.

#### 1. Firebase에서 서버키 가져오기
Firebase 홈페이지에서 프로젝트 대쉬보드 -> 설정버튼 -> 클라우드 메시징을 클릭하시면 서버키를 확인할 수 있습니다

![Postman_FCM_Firebase](img/Postman_FCM%20Push_Noti/postman_fcm_firebase.png)


#### 2. 디바이스 토큰 가져오기
Activity에서 아래의 코드를 추가한 후 실행합니다

```kotlin
FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
    if (task.isSuccessful) {
        println("token: ${task.result}")
    }
}
```

#### 3. Postman으로 FCM Push 알림 보내기
1) Postman에서 새로운 Request를 생성

2) POST 요청으로 변경하고 url에 "https://fcm.googleapis.com/fcm/send"로 넣습니다

3) Header탭에서 Authorization에는 "key=서버키"를 넣습니다

(Content-Type의 경우 선언을 안해줘도 되지만 해주고 body로 넘어가면 raw클릭 시  text가 아닌 json으로 변경됨)

![postman_fcm_header](img/Postman_FCM%20Push_Noti/postman_fcm_header.png)

Body에는 raw -> json으로 변경 후 아래와 같이 입력해주시면 됩니다

```json
{
  "to": "디바이스 토큰 값",
  "priority": "high",
  "data" : {
    "title" : "Postman",
    "message" : "fcm test"
  }
}
```

![postman_fcm_body](img/Postman_FCM%20Push_Noti/postman_fcm_body.png)


디바이스에서 확인하면 아래와 같이 메시지가 온 걸 확인할 수 있습니다

![postman_fcm_result](img/Postman_FCM%20Push_Noti/postman_fcm_result.png)


 