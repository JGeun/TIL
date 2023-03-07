![Best](./img/BestPractices/best_practices_circle_image.png)

> 해당 글은 공부를 위해 원본을 번역하였습니다. [원본 글](https://proandroiddev.com/android-development-best-practices-7278e9cdbbe9)
>
>Android를 하면서 어떻게 개발을 해야하는지에 대한 방향성을 제시해줄 수 있을 것 같아 번역하게 되었습니다.

Android development는 모바일 개발 시장에 계속 지배하고 있습니다. 재밌는 프로젝트, 높은 보수, 그리고 수 많은 일자리 전망은 개발자들이 안드로이드 운영체제의 흥미로운 세계로의 여정을 시작하게 만드는
이유 중 일부일 뿐입니다. 일부 전문가들은 특히 코틀린의 추가와 구글의 정책 개선과 같은 최근 업데이트 이후로 안드로이드 기술을 배우기 더 없이 좋은 시기라고 말합니다.

안드로이드 개발을 한지 5년이 지났고, 새로운 것을 배우지 않은 날이 단 하루도 없었습니다. 세월이 흐르면서 제가 깨달은 것은 다음과 같습니다.

> 단지 코드를 작성하는 것은 충분하지 않습니다. 효율적인 방식으로 작성하는 것이 진정한 과제입니다.

자신만의 프로젝트를 만들고 싶단, 인정받는 회사에서 일하고 싶은 것과는 관계 없이, 앱을 개발하면서 절대 소홀히 해서는 안 되는 필수적인 측면들이 있습니다.

Best Practices들을 활용하면, 이미 성공적으로 해냈던 작업을 다시 반복하는 데 귀중한 시간을 낭비하지 않을 겁니다. 대신에 품질좋은 코드를 작성하고 작업을 완료하는 데 집중할 수 있습니다.

지금부터 제가 5년 동안의 여정동안 배운 몇 가지 모범 사례(best practices)를 소개하겠습니다.

### Tips and not Tricks

#### 1) 트렌드라서 선택하는 것이 아닌 필요에 따라 앱 아키텍처를 현명하게 선택하세요.

아키텍처는 애플리케이션이 핵심 기능을 수행하는 위치와 기능이 데이터베이스 및 사용자 인터페이스와 어떻게 상호작용하는 지를 정의합니다.

MVC, MVP, MVVM, MVI, Clean Architecture와 같은 많은 아키텍처가 있습니다. 이러한 아키텍처 중 프로젝트 요구 사항을 충족하고 표준 코딩 지침을 따르면서 코드를 깨끗하게 유지하는 경우,
어떠한 아키텍처도 나쁘지 않습니다.

#### 2) SVG 또는 WebP를 image drawables에 사용 하는 것을 고려하세요

여러 해상도를 지원하는 것은 때때로 개발자들에게 악몽입니다. 다른 해상도의 여러 이미지들을 포함하면 프로젝트의 사이즈도 증가합니다. 해결책은 SVG 이미지 또는 Webp와 같은 Vector Graphics를 사용하는
겁니다. 해당 이미지 형식들은 무손실 이미지들을 압축 함으로서 이미지 사이즈 문제를 해결하는 데 큰 차이를 만들 수 있습니다.

#### 3) layout을 현명하게 선택하고 재사용가능한 XML을 분리해서 <include> tag를 사용해서 붙이세요

ConstraintLayout, LinearLayout, RelativeLayout, FrameLayout CoordinatorLayout과 같이 다양한 Layout이 있습니다. 그 중 일부를 대상으로 성능 분석을
해본 결과, 시나리오/요구사항에 따라 레이아웃을 사용해야 한다는 것을 알게 되었습니다.

또한, XML의 일부가 다른 레이아웃에서 재사용되는 경우 별도의 레이아웃으로 추출하고 다른 레이아웃에서 코드가 복제되지 않도록 <include/> 태그를 사용하세요.

#### 4) Build Type, Product Flavors 및 Build Variants를 사용하는 방법에 대해 알아보고 더 빠르고 쉬운 개발을 위해 이를 최대한 활용하세요.

#### Build Type

코드를 컴파일하는 방법을 결정합니다. 예를 들어, 디버그 키로 .apk에 서명하려면 디버그 구성을 디버그 빌드 유형으로 지정합니다. 컴파일하여 릴리즈할 준비가 되었을 때 난독화된 코드를 사용하려면 릴리스 빌드 유형에
해당 구성을 추가합니다. HTTP 요청을 디버그 모드로 기록하고 릴리즈 모드에서 비활성화하려면 라이브러리 종속성의 빌드 유형 또는 호출 빌드 유형에 구성을 적용합니다.

> 모든 build type은 서로 다른 컴파일 구성을 가진 동일한 코드베이스와 동일한 UI 동작을 합니다.

```kotlin
buildTypes
{
    release {
        minifyEnabled true
        proguardFiles getDefaultProguardFile ('proguard-android.txt'), 'proguard-rules.pro'
    }

    debug {
        applicationIdSuffix ".debug"
    }
}
```

다음은 가장 간단한 예입니다:

디버그 모드에서 앱을 실행할 때, application 패키지명은 packagename.debug이고 릴리즈 모드를 실행한다면 aplication 패키지명은 packagename이 됩니다.

#### Product Flavor

고객 사용자를 위해 앱을 개발하고 있다고 가정해 보겠습니다. 고객 앱은 모든 것이 잘 되고 있습니다. 그러면 제품 소유자(Product Owner)는 어드민 유저를 위한 앱을 개발해야 한다고 말했습니다. 어드민 유저
앱은 고객 앱이 가지고 있는 모든 기능을 가지고 있어야 합니다. 그러나 어드민 사용자는 통계 페이지에 엑세스할 수 있으며 어드민 유저는 다른 색상과 리소스로 앱을 볼 수 있어야 합니다. 또한 어드민 앱의 분석 기능은
고객앱과 혼합되서는 안됩니다. 그러면 어떻게 처리하실 건가요? 정답은 Product Flavor입니다. 같은 앱이지만 다른 행동을 할 수 있습니다.

Gradle file을 편집하면:

```kotlin
android {
    ...
    defaultConfig { ... }
    buildTypes { ... }
    productFlavors {
        admin {
            ..
        }
        customer {
            ..
        }
    }
}
```

#### Build Variants

build types와 product flavors를 결합합니다. build.gradle을 업데이트한 후 프로젝트를 동기화합니다. 그러면 모든 빌드 유형이 표시됩니다.

#### 5) application을 디버그 하는데 Android Debug Bridge (ADB)를 배우고 사용하세요.

Android Debug Bridge (ADB)는 장치와 통신할 수 있는 다목적 명령줄 도구입니다.

일상적인 사용에는 적합하지 않을 수 있지만, 사용자나 개발자 경험에 크게 도움이 될 수 있는 작업을 Android 장치에서 수행합니다. 예를 들어, Play Store 외부에 앱을 설치하고, 앱을 디버그하고, 숨겨진
기능에 엑세스하고, 장치에서 직접 명령어를 실행할 수 있도록 Unix 셸을 불러올 수 있습니다.

ADB는 Android Studio Logcat보다 더 많은 세부 정보를 제공합니다. 한 번 사용해보고 나중에 저한테 고맙다고 할 수 있습니다 :-)

#### 6) gradle.properties를 구성하여 빌드 속도를 높입니다.

개발자의 삶에서 긴 빌드 시간은 항상 문제입니다.

```kotlin
org.gradle.daemon = true
org.gradle.parallel = true
org.gradle.configureondemand = true
android.enableBuildCache = true
org.gradle.jvmargs = -Xmx3072m - XX:MaxPermSize = 512 m -XX:+HeapDumpOnOutOfMemoryError - Dfile.encoding = UTF - 8
org.gradle.caching = true
android.useAndroidX = true
android.enableJetifier = true
kapt.incremental.apt = true
kapt.use.worker.api = true 
```

제가 했던 개선 사항의 예시를 보여드리겠습니다. 속도 향상에 대한 자세한 내용은 여기를 참조하세요 :-)

#### 7) 앱 코드의 구조적인 문제들을 Lint를 통해 확인하세요.

Lint 도구는 Android 앱에서 앱의 안정성과 효율성에 여향을 줄 수 있는 구조화되지 않은 코드를 찾는데 도움을 줍니다.

The command for MAC:

```shell
./gradlew lint 
```

For Windows:

```shell
gradlew lint
```

#### 8) 모든 것은 DEBUG 모드에서만 기록합니다.

유용한 정보, 오류, workflows를 표시하거나 무언가를 디버깅할 때 로그를 사용합니다.

하지만, 우리가 기록하는 모든 정보는 잠재적인 보안 문제의 원인이 될 수 있습니다! 그러므로 코드가 활성화되기 전에 제거해야 합니다.

이러한 로그를 보관하려면 메세지를 기록하고 로그 흐름을 제어할 수 있는 Timber 라이브러리를 사용하거나 디버그 모드에서 로그를 출력하는 custom class를 만들 수 있습니다.

#### 9) 특정 메서드를 추출할 수 있거나 기능을 위한 클래스 수가 적은 경우 third party 라이브러리 전체를 추가하지 마세요.

해당 클래스를 프로젝트에 추가하고 그에 따라 수정하세요

#### 10) Android App에서 수시로 메모리 누수를 감지하고 수정하세요

"작은 누수가 큰 배를 침몰시킬 것이다." - 벤자민 프랭클린

Leak canary와 같은 메모리 도구를 사용해서 메모리 누수의 원인을 감지하세요.

안드로이드 프레임워크의 내부에 대한 지식은 각 누출의 원인을 좁힐 수 있는 독특한 능력을 제공하여 개발자들이 OutOfMemoryError 충돌을 극적으로 줄일 수 있도록 도와줍니다.