###  핸들러 사용 목적
- 특정 메세지를 Looper 의 MessageQueue 에 넣거나, Looper가 MessageQueue 에서 특정 메세지를 꺼내어 전달하면 이를 처리하는 기능

### 핸들러의 기능
    1) 메세지는 다른 스레드에 속한 Message Queue에서 전달됩니다.
    2)MessageQueue에 메세지를 넣을때는 Handler.sendMessage()를 사용합니다.
    3) Looper는 MessageQueue에서 **Loop()를 통해 반복적으로 처리할 메세지를 Handler에 전달**합니다.
    4) Handler는 handlerMessage를 통해 메세지를 처리합니다.

### Handler가 전달하는 객체 2가지
- `sendMessage()`메소드를 통해 메세지 큐에`Message`객체를 적재할 수 있다.
- `post`로 시작하는 메소드들을 통해`Runnable`객체를 직접 적재할 수 있다.

### Handler()가 deprecated된 이유
- 정리 중