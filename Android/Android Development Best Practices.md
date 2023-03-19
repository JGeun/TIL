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

#### 11) 앱의 구성 변경 사항을 처리합니다.

때때로 Activity, Fragment 또는 AsyncTasks의 orientation 변경을 처리하는 것이 가장 짜증나는 일이 됩니다. orientation 변경이 제대로 처리되지 않으면 응용 프로그램의 예기치
않은 동작이 발생합니다.

이러한 변경이 발생하면 Android는 실행 중인 활동을 다시 시작합니다. 즉, 작업을 삭제하고 다시 만듭니다.

orientation 변경을 처리할 수 있는 다양한 옵션이 있습니다.

1. 화면 방향을 제한한다.

2. 활동이 다시 생성되지 않도록 합니다.

3. 기본 상태를 저장합니다.

4. 복잡한 객체들을 저장합니다.

#### 12) 클라이언트의 입력 양식과 같은 화면에서만 유효성 검사를 수행하세요.

사용자 이메일이 유효한지 또는 사용자의 연락처가 필수 길이인지와 같은 검증을 위해 백엔드에 요청을 해야하나요?

이런 경우를 고려하고 그에 맞게 로직을 구현하세요.

#### 13) activities에 대한 참조를 만들지 마세요. 이것은 activities들이 완료될 때 수거되는 가비지가 되는 것을 방해합니다.

매우 간단한 시나리오를 고려해 보세요. 활동에서 local broadcast receiver를 등록해야 합니다. 만약 broadcast receiver를 등록 취소하지 않으면, activity가 종료되더라도
activity에 대한 참조가 유지됩니다.

어떻게 해결할까요? 항상 activity의 onStop() 에서 receiver를 취소하는 걸 호출해야한다는 걸 기억하면 됩니다.

#### 14) 암시적 인텐트로 App Chosser를 사용하고 항상 NoActivityFound Exception을 처리합니다.

여러 앱이 intent에 응답할 수 있고 사용자가 매번 다른 앱을 사용하길 원하는 경우, 선택 대화상자를 명시적으로 표시해야 합니다.

또한 어떤 이유로든 사용할 수 있는 앱이 없는 경우, 앱이 중단되서는 안됩니다. try/catch를 통해 처리하고 사용자에게 토스트 메세지를 보여주세요.

#### 15) 모든 중요한 정보는 gradle.properties에 저장하고 버전 제어 시스템(git과 같은 시스템)에 넣지 마세요.

이렇게 하지마세요. 이러면 version control system에 나타나게 됩니다.

```kotlin
signingConfigs {
    release {
// DON'T DO THIS!!
        storeFile file ("myapp.keystore")
        storePassword "password123"
        keyAlias "thekey"
        keyPassword "password789"
    }
}
```

대신, garlde.properties 파일을 만드세요

```kotlin
KEYSTORE_PASSWORD = password123
KEY_PASSWORD = password789
```

파일이 자동적으로 Gradle에 의해 생성됩니다. build.gradle에서 다음과 같이 사용할 수 있습니다.

```kotlin
signingConfigs {
    release {
        try {
            storeFile file ("myapp.keystore")
            storePassword KEYSTORE_PASSWORD
                    keyAlias "thekey"
            keyPassword KEY_PASSWORD
        } catch (ex) {
            throw new InvalidUserDataException ("You should define KEYSTORE_PASSWORD and KEY_PASSWORD in gradle.properties.")
        }
    }
}
```

#### 16) 중간자 공격 (MITM - Man-in-the-midlle Attack)을 막기 위해 SSL 인증서 고정(pinning)을 구현하세요.

요청을 차단하기 위해 대부분 프록시 도구를 사용합니다. 프록시 도구는 장치에 자체 인증서를 설치하고 application은 해당 인증서를 유효한 인증서로 신뢰하며 프록시 도구가 application 트래픽을
가로채도록 허용합니다. 이런식으로 우리는 해커들이 우리 데이터를 조작하거나 알 수 있도록 돕게됩니다.

SSL 고정 구현을 활용하면 application이 사용자 지정 인증서를 신뢰하지 않으며 프록시 도구가 트래픽을 가로채도록 허용하지 않습니다.

클라이언트측 서버 인증서 검증에 따라 달라지는 방식입니다. 여기서 더 읽어보세요.

#### 17) SafetyNet Attestation API를 사용하여 서버가 진짜 안드로이드 장치에서 실행 중인 정품 앱과 상호작용하는지 확인하세요. (현재 SafetyNet Attestation API는 deprecated 되었습니다. Play Integrity API를 참고해주세요!)

API는 다음 사항을 확인합니다:

- 장치가 최고 관리자 권한 root가 도용될 수 있는지

- 장치가 모니터링되고 있는지

- 장치가 hard parameters를 인식했는지

- 소프트웨어가 Android와 호환되는지

- 장치가 악성 앱으로 부터 자유로운지

구현하기 전에 수행해야할 사항과 하지말아야할 사항을 확인하세요.

#### 18) 인증 토큰 등과 같은 중요한 정보를 저장하려면 SharedPreferences 대신 EncryptedSharedPreferences를 사용하세요.

#### 19) Android Keystore 시스템을 사용하여 데이터베이스 등의 스토리지에서 중요한 정보를 저장하고 검색하세요.

Android ketstore는 안전한 시스템 수준의 credential 저장소입니다. keystore를 사용하여 앱은 새로운 Private/Public 키 페어를 생성할 수 있고 이것을 사용해서
application 암호를 개인 저장소 폴더에 저장하기 전에 암호화합니다. AarogyaSetu를 개발하면서, 저는 매우 민감한 정보를 암호화하고 해독하기 위해 Keystore를 사용하는 것에 대해 배웠습니다.
여기서 구현된 부분을 확인할 수 있습니다.

참고: 기본적으로 켜져있는 Android Backup 메커니즘이 있습니다. Android는 앱 데이터를 사용자의 Google Drive에 업로드하여 보존합니다.앱 데이터는 사용자의 Google 계정
credentials으로 보호되며 다른 장치들에서 같은 credentials를 쉽게 다운로드할 수 있습니다. 하지만 Android Keystore를 사용하여 암호화한 경우 cypher key는 오직 특정 장치에만
사용되므로 복원할 수 없습니다. 우리는 ios와 keychain과 같은 keystore를 위한 동기화 매커니즘이 없습니다. 더 좋은 방법은 백엔드에 저장하는 것입니다.

#### 20) Google Play 서비스 메서드를 호출하기 위해 default security provider에서 발견된 취약점으로 부터 보호하기 위한 최신 업데이트가 있는 장치에서 앱이 실행되고 있는지 확인하세요

예를 들어 OpenSSL에서 발견된 취약점(CVE-2014-0224)은 어느 쪽도 모르게 보안 트래픽을 해독하는 "man-in-the-middle" 공격을 받을 수 있습니다. Google Play 서비스 버전
5.0에서는 수정 프로그램을 사용할 수 있지만 앱은 수정 프로그램이 설치되어 있는지 확인해야 합니다. Google Play 서비스 방법을 사용하면 앱이 해당 공격으로부터 보호된 장치에서 실행되고 있는지 확인할 수
있습니다. 이러한 취약점에 대비해 보호하기 위해, Google Play 서비스들은 장치의 보안 제공자를 자동으로 업데이트하여 알려진 공격으로부터 보호하는 방법을 제공합니다. 여기서 더 읽어보세요.

#### 21) 앱이 로봇에 의해 자동화되지 않도록 reCAPTCHA를 구현합니다.

reCAPTCHA는 고급 위험 분석 엔진을 사용하여 앱이 스팸 및 기타 남용 행위로부터 앱을 보호해주는 무료 서비스입니다. 서비스가 앱과 상호 작용하는 사용자가 인간이 아닌 봇일 수 있다고 의심할 경우, 앱을 계속
실행하기 전에 인간이 해결해야 하는 CAPTCHA를 제공합니다.

#### 22) feature에 대해 Unit tests를 작성하세요

몇 가지 이점을 나열하겠습니다:

1. Unit test는 개발 주기 초기에 버그를 수정하고 비용을 절약하는 데 도움이 됩니다.

2. 개발자가 코드 기반을 이해하고 빠르게 변경할 수 있도록 지원합니다.

3. 좋은 unit test는 프로젝트 문서의 역할을 합니다.

4. 여러분이 사용하는 것에 대해 더 자신감을 갖게 됩니다.

여기 많은 장점들이 더 있습니다.

#### 23) 가능한 경우 언제든지 서버 측에서 보안 결정하도록 하세요

application의 클라이언트 측을 신뢰하지 마세요. 해커는 application의 코드 베이스를 쉽게 조작하거나 해킹할 수 있으며 코드를 조작할 수 있습니다. 따라서 가능할 때마다 백엔드 측을 확인하는 것이
좋습니다.

#### 24) 코드 난독화 및 최적화를 위해 Proguard를 최대한 활용하는 방법을 알아봅니다.

안드로이드 애플리케이션은 리버스 엔지니어링이 상당히 쉬우므로 이런 일이 발생하지 않도록 하려면 ProGuard를 주요 기능으로 사용해야 합니다.

주요기능 - 사람이 이해하기 어려운 행태로 소스 코드를 만드는 과정(클래스와 멤버 이름 변경)인 난독화

ProGuard는 2개의 중요한 기능을 가지고 있습니다: 사용하지 않는 코드를 제거하는 매우 유용한 축소 기능과 최적화 기능

그러나 최적화는 자바 바이트 코드에서 작동하며 안드로이드는 자바 바이트 코드에서 변환된 달빅 바이트 코드에서 실행되기 때문에 일부 최적화는 잘 작동하지 않을 것이다. 그러니 조심해야 합니다.

### 25) 네트워크 보안 구성을 사용하여 앱의 네트워크 보안을 개선합니다.

보안은 하나의 철제 벽이라기보다는 여러 겹의 보호에 관한 것입니다. Android 네트워크 보안 구성 기능은 앱이 암호화되지 않은 일반 템스트로 의도치 않게 중요한 데이터를 전송하지 못하도록 보호하는 간단한 계층을
제공합니다.

> "암호화되지 않은 통신"이 무엇을 의미하는지 모른다면 이렇게 생각해 보세요 - 사무실에서 UPS를 통해 모든 발송물을 발송하는 정책을 가지고 있다고 가정해보겠습니다.
> 새 인턴이 사무실에 합류하여 장비를 전국 사무실로 배송하는 업무를 맡게 됩니다.
> 그 정책은 의식하지 않고 모든 최선의 의도를 가지고 인턴은 알려지지 않은, 덜 비싼 서비스를 통해 발송되도록 모든 발송물을 설정합니다.
> Android 네트워크 보안 구성 기은은 모든 인바운드 및 아웃바운드 발송을 검사하고 장비가 검증되지 않은 배송 시스템에 들어가기 전에 발송을 중지하는 배송/수신 관리자와 같습니다.
> 이것은 신뢰할 수 없는 암호화되지 않은 연결의 사고를 방지하는 데 사용할 수 있습니다.

여기를 읽어보세요.

26. In-app Review API를 사용하여 사용자가 App Details 페이지로 돌아가지 않고 앱 내에서 리뷰를 남길 수 있습니다. 많은 개발자들에게, 등급과 리뷰는 사용자들에게 중요한 접점입니다. 매일
    수백만 개의 리뷰가 Google Play에 남아 개발자들에게 사용자들이 좋아하는 것과 개선되기를 원하는 것에 대한 귀중한 통찰력을 제공합니다. 사용자는 또한 자신에게 적합한 앱과 게임을 결정하는 데 도움이
    되는 등급과 리뷰에 의존합니다.

지난 2년간 구글 플레이는 사용자들이 리뷰를 쉽게 남길 수 있도록 다양한 기능을 출시했고, 개발자들이 이에 상호작용하고 대응할 수 있도록 했습니다. 예를 들어, 사용자들은 이제 구글 플레이 홈페이지에서 리뷰를 남길
수 있습니다. 또한 사용자가 리뷰를 남기고 관리할 수 있는 중앙 집중식 위치를 제공하는 내 앱 & 게임 아래 리뷰 페이지를 시작했습니다. 개발자는 API를 사용하여 사용자가 앱 경험 내에서 리뷰를 작성하도록 요청할
시기를 선택할 수 있습니다. 사용자에게 메세지를 표시할 수 있는 가장 좋은 시기는 사용자가 철저하고 유용한 피드백을 제공할 수 있을 만큼 충분히 앱을 사용했을 때라고 생각합니다. 그러나 작업 도중이나 작업자의 주의가
필요할 때 작업자를 방해하지 마세요. 검토 흐름이 화면의 흐름을 대신하기 때문입니다.

27. 앱에 Modularization을 도입하세요

앱을 작은 모듈들로 나누고, 그 모듈들을 implementation project(":network-module") 과 같이 필요한 모듈들에 의존성으로서 제공하세요. 그러면 개발하는 동안 빠른 빌드와 재사용가능한
코드의 이점을 갖게됩니다. 나중에 이 기능을 확장하여 동적 전송 모듈을 제공할 수 있습니다.  (Credits: Vipul Thawre)

28. base 클래스를 너무 많이 사용하지 않도록 하세요. 모든 곳에서 base 클래스를 사용하면 코드에 촘촘한 web이 생성되어 나중에 리팩토링하기가 어렵습니다. 그래도 필요한 경우 독립 실행형 함수 (
    kotline file)를 생성하여 사용합니다.

예를들어 이것을 이해해 봅시다: ProfileFragment와 HomeFragment라는 2개의 fragments가 있습니다. 이것은 BaseFragment로 부터 확장된 것입니다. BaseFragment는
onCreate()메서드에서 fetchPosts() 기능을 가지고 있습니다. 이제 ProfileFragment가 생성될 때 게시물을 가져오지 않아야 한다고 결정하면 사용자가 로그인하지 않는 경우 대화 상자를 먼저
표시해야 합니다.

코드베이스가 크면 리팩토링하기가 어려울 수 있습니다. 다른 방법은 fun fetchPosts()로 코틀린 파일을 만든 다음 onCreate() 메서드나 swipeRefresh() 메서드에서 이 함수를 사용하는
것입니다. 또한 클래스는 하나의 추상 기본 클래스만 확장할 수 있습니다. 여기 & 여기를 더 읽어보세요. (Credits: sidsharma)

29. 다음과 같이 앱 수준 build.gradle 파일에 다음 스니펫을 추가하여 기본 레이아웃 폴더의 sourceSets를 만듭니다.

```xml
android {
    ...
    sourceSets {
        main {
            res.srcDirs = [
                'src/main/res',
                'src/main/res/layouts',
                file('src/main/res/layouts').listFiles()
            ]
        }
    }
}
```

이제 그림과 같이 각 폴더에서 활동 레이아웃, fragment 레이아웃 및 사용자 지정 레이아웃을 구분할 수 있습니다.

이렇게 하면 레이아웃 파일을 훨씬 쉽게 탐색하고 리소스를 분리할 수 있습니다. (Credits: Sourabh Pant)

30. Application에서 FirebaseFireStor를 사용하는 경우 보안 문제를 줄이기 위해 읽기 및 쓰기 규칙에 직접 true를 쓰지 마세요. 
    이것은 해커가 application에 침입하는 데 도움이 될 수 있습니다. 이 outlet을 지우려면 FireStore에 작성된 기본 규칙을 확인합니다.

rules_version = ‘2’; service cloud.firestore { match /databases/{database}/documents { match /{document=**
} { allow read, write: if false; } } 이렇게 하면 기록된 사용자만 DB에 쓸 수 있습니다.

규칙 작성에 대한 자세한 내용은 https://firebase.google.com/docs/rules

이것과 이것을 읽어보세요

#### The Critics principle 

팀원과 코드 리뷰를 할 때 친구가 되지 마세요. 그들의 가장 큰 적이 되어 언젠가 당신이 청소해야 할 수도 있는 실수를 그들이 하지 않게 하세요. 남의 것을 닦는 것은 당신의
손을 더럽힐 뿐입니다. 코드 검토에서 모범 사례를 시행합니다.