RecyclerView 작업을 하다보면 맨 마지막 item이 잘리는 경우가 발생합니다.

#### 1. ConstraintLayout 으로 상위 Layout이 구성되어있는 경우

RecyclerView를 Bottom을 연결하지 않고 height에 wrap_content를 입력하여 구현하는 경우 바닥이 잘리게 됩니다.

```kotlin
android:layout_height="wrap_content"
app:layout_constraintTop_toTopOf="parent"
app:layout_constraintStart_toStartOf="parent"
app:layout_constraintEnd_toEndOf="parent"
```

이럴 때는 layout_height="0dp"로 선언 후 bottom을 연결해주면 해결됩니다.

```kotlin
android:layout_height="0dp"
```

#### 2. 위의 경우가 해결되지 않을 경우

layout_constrainedHeight="true"를 추가적으로 넣어주시면 해결됩니다

```kotlin
app:layout_constrainedHeight="true"
```