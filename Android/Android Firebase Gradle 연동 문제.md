이번에 제가 프로젝트에 Firebase를 연동하면서 gradle이 완전 바뀐걸 확인했습니다.

```kotlin
plugins {
    id 'com.android.application' version '7.2' apply false
    id 'com.android.library' version '7.2' apply false
    id 'org.jetbrains.kotlin.android' version '1.5.31' apply false
}
```

그래서 이게 뭘까... 하면서 일단 넣어봤지만 결과는 에러가 나타났습니다
```kotlin
plugins {
    id 'com.android.application' version '7.2' apply false
    id 'com.android.library' version '7.2' apply false
    id 'org.jetbrains.kotlin.android' version '1.5.31' apply false
    id 'com.google.gms:google-services' version '4.3.10' apply false
}

//error
plugin id 'com.google.gms:google-services' is invalid: Plugin id contains invalid char ':'
(only ASCII alphanumeric characters, '.', '_' and '-' characters are valid)
```
이게 뭐야... 왜 안되지 자꾸 왜 이런 에러가 뜨지???? 하면서 엄청 검색을 해봤는데요 🤔

알고보니 플러그인이 변경되면서 ':'와 같은 문자들은 invalid 처리 되도록 해놨더라구요

도대체 어떻게 할까 고민하다가 


```kotlin
plugins {
    id 'com.android.application' version '7.2' apply false
    id 'com.android.library' version '7.2' apply false
    id 'org.jetbrains.kotlin.android' version '1.5.31' apply false
    id 'com.google.gms.google-services' version '4.3.10' apply false
}
```
':'이 아닌 '.'으로 처리하였더니 해결되었습니다.

알고보니 [안드로이드 Gradle 플러그인 출시 노트](https://developer.android.com/studio/releases/gradle-plugin?hl=ko#updating-plugin) 가 나왔습니다.

역시 개발자라면 공식 문서를 지속적으로 확인해야한다는 생각이 많이 드는 에러였던 것 같아요..! 😥