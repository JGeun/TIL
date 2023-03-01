### ApiKey를 왜 숨겨야 하는걸까?

깃허브의 모든 커밋과 리뷰는 기록이 남습니다. 만약 ApiKey와 같은 중요한 정보들이 제대로 처리하지 않고 기록에 남는다면 소중한 정보들이 노출되므로 조심해야 합니다. 이번 포스팅에서는 어떻게 하면 이런 정보들을
관리할 수 있는지에 대해 정리해보고자 합니다.

### 관리하는 방법

#### 1. gitignore에 기록이 남지 않았으면 하는 파일들을 명시

우선, 가장 먼저 깃으로의 노출을 막아야할 것은 local.properties입니다. 우리는 여기에 API 키를 포함한 중요 정보들을 저장하고 관리할 것입니다.

그 이후에 /build 경로를 막습니다. 해당 경로에 BuildConfig가 존재하는데, 저희가 local.properties에서 키값을 불러오면 BuildConfig에 명시되기 때문에 반드시 포함시켜서는 안됩니다.

보통 초기 안드로이드 프로젝트를 생성하면 gitignore에 기본적으로 포함되어있지만 혹시나 포함되어있지 않은 경우 반드시 추가해주셔야 합니다.

```kotlin
*.iml
    .gradle
/local.properties
/.idea / caches
/.idea / libraries
/.idea / modules.xml
/.idea / workspace.xml
/.idea / navEditor.xml
/.idea / assetWizardSettings.xml
    .DS_Store
/build < -
/captures
    .externalNativeBuild
    .cxx
local.properties < -
```

#### 2. local.properties에 명시

SDK 경로 밑에 API키나 기타 중요한 데이터들을 저장해주시면 됩니다.

이 때 주의할 점은 꼭 큰 타옴표("")로 변수를 저장해야 합니다.

sdk.dir=Android_Sdk

// 여기서부터 명시 apiKey="This is my apikey"

#### 3. build.gradle (app)

앱 수준의 Gradle 파일에서 local.properties에 새로 등록한 키 값을 불러옵니다.

<Groovy>

```kotlin
plugins {
    id 'com.android.application'
}

// Properties 객체 생성
Properties properties = new Properties()
properties.load(project.rootProject.file('local.properties').newDataInputStream())

android {
    ...

    defaultConfig {
        ...

        buildConfigField "String", "API_KEY", properties['api_key']
    }
}
```

<KTS>

KTS에는 총 2가지 방식이 존재합니다.

1. Properties 객체 생성 방법

2. local.properties 내부에서 key값을 가져오는 함수 구현

```kotlin
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// 1. Properties 객체 생성 방식 

val properties = Properties().apply {
    load(FileInputStream(rootProject.file("local.properties")))
}

android {
    ...

    defaultConfig {
        ...

        // 1. Properties 객체 생성 방식
        buildConfigField("String", "API_KEY", getApiKey("apiKey"))

        // 2. local.properties 내부에서 key값을 가져오는 함수 구현방식
        buildConfigField("String", "API_KEY", getApiKey("apiKey"))
    }

    ...

}

// 2. local.properties 내부에서 key값을 가져오는 함수 구현방식 
fun getApiKey(propertyKey: String): String { 
    return gradleLocalProperties(rootDir).getProperty(propertyKey)
}
```

#### 4. BuildConfig에서 명시했던 API_KEY에 대한 변수 확인

만약 BuildConfig가 생성되지 않았다면 상단 Build탭 -> Rebuild Project 를 눌러주시면 됩니다.

#### 5. 실제 사용방법

우선 매니페스트 영역에서는 "${변수}" 형태로 선언해주시면 됩니다.

<meta-data android:name="~~.API_KEY"
android:value="${API_KEY}" />

Class나 Interface내에서는 BuildConfig.API_KEY 형태로 사용하시면 됩니다.

val API_KEY = BuildConfig.API_KEY