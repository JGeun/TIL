### 배경

binding을 사용하기 이전에 Android에서는 findViewById를 자주 사용해왔습니다.

하지만 findViewById를 반복적으로 호출하게 될 경우 성능 저하가 발생한다고 합니다.

왜 성능저하가 발생하는걸까요?

### findViewById

Activity - findViewById

```java
@SuppressWarnings("TypeParameterUnusedInFormals")
@Override
public<T extends View> T findViewById(@IdRes int id){
        return getDelegate().findViewById(id);
        }
        Activity의 경우 getDelegate()를 파고 들어가면 AppCompatDelegate를 생성하여 그것에서 findViewById를 실행합니다.

// AppCompatActivity.java
@NonNull
public AppCompatDelegate getDelegate(){
        if(mDelegate==null){
        mDelegate=AppCompatDelegate.create(this,this);
        }
        return mDelegate;
        }
```

Activity는 오직 하나의 AppCompatDelegate 인스턴스와 연결할 수 있으며 create(Activity, AppCompatCallback)으로부터 반환된 인스턴스는 Activity가 destroy될
때까지 유지됩니다.

### View - findViewById

```java
@Nullable 
public final <T extends View> T findViewById(@IdRes int id) {
    if (id == NO_ID) {
        return null; 
    } 
    return findViewTraversal(id); 
}

//findViewTraversal 
protected <T extends View> T findViewTraversal(@IdRes int id) {
    if (id == mID) {
        return (T) this; 
    }
    return null; 
}
 ```

View의 findViewById는 findViewTraversal 메서드를 호출합니다. findViewTraversal을 보면 view에 지정된 mID와 매개변수가 같으면 return하고 일치하지 않으면 null을
return 합니다. View 클래스의 findViewById를 반복적으로 호출하게 될 경우 크게 성능저하가 발생하지 않을 것으로 예상됩니다.

이번에는 ConstraintLayout, LinearLayout과 같이 ViewGroup을 상속받은 Layout을 살펴봅시다.

### ViewGroup - findViewTraversal

```java
@Override protected <T extends View> T findViewTraversal(@IdRes int id) {
    
    if (id == mID) {
        return (T) this; 
    }

    final View[] where = mChildren;
    final int len = mChildrenCount;

    for (int i = 0; i < len; i++) {
        View v = where[i];

        if ((v.mPrivateFlags & PFLAG_IS_ROOT_NAMESPACE) == 0) {
            v = v.findViewById(id);

            if (v != null) {
               return (T) v;
            }
        }
     }

     return null;

}
```

DecorView는 setContentView에서 설정한 레이아웃의 최상단 View이기 때문에 ConstraintLayout, LinearLayout 등이 ViewGroup에 해당되어 ViewGroup의
findViewTraversal의 메서드를 호출하게 됩니다.

Layout에 포함되어 있는 자식(mChildren)과 카운트(mChildCount)를 멤버변수로 가지고 있습니다. 그 카운트만큼 반복문을 통해 해당하는 자식 View를 찾고 존재할 경우 return, 없을 경우
null을 return 하고 있습니다.

여기서 Layout이 깊을 수록 ViewGroup이 많을수록 반복문이 많이 돌면서 비용손실이 발생하게 되어 성능 저하의 원인이 됩니다.

 