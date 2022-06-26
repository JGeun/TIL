1. 원하는 activity의 xml에 ViewPager를 추가해줍니다. (저는 tab과 Indicator도 추가했습니다)

```html
//Indicator의 경우 외부 라이브러리를 사용
https://github.com/ongakuer/CircleIndicator / LICENSE: Apache 2.0
```

```xml

<androidx.viewpager.widget.ViewPager
        android:id="@+id/viewpager"
        android:layout_width="match_parent"
        android:layout_height="0dp"
        android:layout_weight="1">

    <com.google.android.material.tabs.TabLayout
            android:id="@+id/tab_layout"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"/>

</androidx.viewpager.widget.ViewPager>

<me.relex.circleindicator.CircleIndicator
android:id="@+id/indicator"
android:layout_width="match_parent"
android:layout_height="30dp"
app:ci_drawable="@drawable/black_radius"
app:ci_drawable_unselected="@drawable/red_radius"/>
```

1.1 원하는 indicator 색 변경 - black_radius (color만 원하는 색으로 지정하시면 됩니다)

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape
        xmlns:android="http://schemas.android.com/apk/res/android"
        android:shape="oval">
    <solid android:color="@color/black"/>
</shape>
```

2. Activity에서 ViewPager를 연결해준다
```kotlin
binding.viewpager.apply {
    adapter = ScreenSlidePagerAdapter(supportFragmentManager, FRAGMENT_NUMS)
}

binding.tabLayout.setupWithViewPager(binding.viewpager) //tablayout 연결
binding.indicator.setViewPager(binding.viewpager) //indicator 연결

// viewpager 변경에 따른 indicator 변경
binding.viewpager.adapter!!.registerDataSetObserver(binding.indicator.dataSetObserver)
```

3. 이제 ScreenSlidePagerAdapter를 만듭니다.
- getItem에서 position에 따라 원하는 Fragment를 연결할 수 있습니다
```kotlin
class ScreenSlidePagerAdapter(fm: FragmentManager, private val count: Int) : FragmentStatePagerAdapter(fm) {
    override fun getCount(): Int = count

    override fun getItem(position: Int): Fragment = ScreenSlidePageFragment(position)

    override fun getPageTitle(position: Int): CharSequence {
        return "TAB ${(position + 1)}"
    }
}
```

4. 원하는 Fragment 만들기

-> 저는 간단하게 구현하기 위해 Fragment에 배경색을 position에 따라 설정하여 구분하기 쉽게 만들었습니다
```kotlin
class ScreenSlidePageFragment(val position: Int) : Fragment() {

    private lateinit var binding: FragmentScreenSlidePageBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentScreenSlidePageBinding.inflate(inflater, container, false)

        // 각 position을 구분하기 위해 색 지정
        when (position) {
            0 -> binding.root.setBackgroundResource(R.color.purple_200)
            1 -> binding.root.setBackgroundResource(R.color.teal_200)
            2 -> binding.root.setBackgroundResource(R.color.purple_700)
            3 -> binding.root.setBackgroundResource(R.color.green)
            4 -> binding.root.setBackgroundResource(R.color.blue)
            else -> binding.root.setBackgroundResource(R.color.red)
        }

        return binding.root
    }
}
```

<결과물>
![ViewPager Result](./img/How%20to%20use%20Viewpager/ViewPager%20Result.png)