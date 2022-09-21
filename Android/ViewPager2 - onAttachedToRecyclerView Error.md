### 배경

Fragment에 ViewPager2를 붙이고 다른화면으로 전환 후에 다시 돌아오면 에러가 발생하면서 앱이 터지는 현상을 발견했습니다.

### 에러 내용

```kotlin
java.lang.IllegalArgumentException
at androidx . core . util . Preconditions . checkArgument (Preconditions.java:38)
at androidx . viewpager2 . adapter . FragmentStateAdapter . onAttachedToRecyclerView (FragmentStateAdapter.java:132)
at androidx . recyclerview . widget . RecyclerView . setAdapterInternal (RecyclerView.java:1243)
at androidx . recyclerview . widget . RecyclerView . setAdapter (RecyclerView.java:1194)
```

### 원인 코드

앱이 터지는 원인은 바로 이 lazy 부분이였는데요. onViewCreated 외부에서 lazy 형식으로 구현했기에 문제가 생긴다고 생각했습니다.

```kotlin
private val homeBannerAdapter by lazy {
    HomeBannerAdapter(this@HomeFragment, viewModel.homeBannerData.size)
}
```

### 해결 방법

처음에 ViewPager2의 Adapter가 상속받는 FragmentStateAdapter의 내부를 확인해봤습니다.

```kotlin
@CallSuper
@Override
public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
    checkArgument(mFragmentMaxLifecycleEnforcer == null);
    mFragmentMaxLifecycleEnforcer = new FragmentMaxLifecycleEnforcer ();
    mFragmentMaxLifecycleEnforcer.register(recyclerView);
}

@CallSuper
@Override
public void onDetachedFromRecyclerView(@NonNull RecyclerView recyclerView) {
    mFragmentMaxLifecycleEnforcer.unregister(recyclerView);
    mFragmentMaxLifecycleEnforcer = null;
}
```

에러가 터지는 부분은 onAttachedToRecyclerView 안에서 checkArgument였는데요.

```kotlin
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP_PREFIX)
public final class Preconditions {
    public static void checkArgument(boolean expression)
    {
        if (!expression) {
            throw new IllegalArgumentException ();
        }
    }
}
```

저는 homeBannerAdapter를 Lazy로 생성해서 mFragmentMaxLifecycleEnforcer가 해제되지 않았기에 생기는 문제라고 인식했습니다.

따라서 lazy를 해제하고 onViewCreated 내부에서 adapter를 할당하는 형식으로 수정했더니 해결할 수 있었습니다.

```kotlin
private var homeBannerAdapter: HomeBannerAdapter? = null

// onViewCreated 내에서
homeBannerAdapter = HomeBannerAdapter(this@HomeFragment, viewModel.homeBannerData.size)
```