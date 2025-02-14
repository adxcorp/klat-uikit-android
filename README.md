
# Klat-UIKit-Android

![Platform](https://img.shields.io/badge/platform-Android-green.svg)
![Languages](https://img.shields.io/badge/language-Kotlin-purple.svg)
![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)

## Klat UIKit SDK  소개

Klat UIKit SDK는 Kotlin 언어로 작성되었으며, 채팅 기능을 Android 앱에 쉽고 빠르게 통합할 수 있는 유저 인터페이스 키트 (User Inerface Kit) 입니다. 이 저장소에서는 Klat UIKit SDK를 Android 앱 프로젝트에 구현하기 전에 알아야 할 필수 정보와 샘플 앱을 테스트을 위한 절차에 대해서 설명하고 있습니다.

<br />

## Klat UIKit SDK 최소 요구사항

- Gradle 7.5
- AGP 7.1.3
- Kotlin 1.7.0
- minSdk 21
- Klat Android SDK 1.0.0

<br />

## Klat UIKit SDK 설치
Android용 Klat UIKit SDK는 jitpack 또는 모듈 추가를 통해 설치할 수 있습니다.

 ### - jitpack 
1. 프로젝트 단 build.gradle 파일에 dependencyResolutionManagement 섹션에서 아래의 Repository를 추가합니다.
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url "https://jitpack.io" }
    }
}
```

2. 모듈 단 build.gradle 파일에 dependencies 섹션에서 아래의 Dependency를 추가합니다.
```kotlin
dependencies {
    implementation("com.github.adxcorp:klat-uikit-android:1.0.0")
}
```

### - 모듈 추가
1. 해당 레포지토리를 clone 및 zip파일로 다운받은 후 원하는 경로에 저장합니다.
2. 프로젝트 단 build.gradle 파일에 해당 다운받은 레포지토리의 경로를 설정해줍니다.
```kotlin
ex)
include(":klat")
project(":klat").projectDir = file("/Users/neptune/AndroidStudioProjects/klat-uikit-android/app")
```

<br />

## 샘플 앱 주요 기능

### - 샘플 앱 주요 기능
- 텍스트 메시지 전송
- 앨범 사진 / 카메라 촬영 사진 전송
- 메시지 제어 및 부가 기능
    <br />: 텍스트 메시지 복사 
    <br />: 메시지 삭제 
    <br />: 10가지 이모지 지원
- 채팅 채널 목록 표시 
- 채팅 채널 생성
- 검색어를 이용한 채팅 채널 필터링
- 채팅 채널 정보 변경 및 제어
    <br />: 메시지 전송 못하게 막기 
    <br />: FCM 푸시 메시지 활성화/비활성화
    <br />: 채널 나가기, 채널 삭제
- 사용자 제어 
    <br />: 음소거  (Mute / Unmute)
    <br />: 강제 퇴장 (Ban)
    <br />: 운영자 (Channel Owner) 권한 부여
    
> 다크 모드 (Dark Mode) 미지원. 

> 채팅 채널을 나타내는 객체 정보에 채팅에 참여한 멤버 정보가 없는 특수 채널 (예: 슈퍼 채널)에 대해서는 샘플 코드가 정상적으로 동작하지 않음. 

<br />

## 샘플 앱으로 시작하기
샘플 앱을 테스트하려면 아래에 기술된 절차를 따라주십시오.
>Klat UIKit SDK를 자신의 Android 앱에 적용하기 전에 GitHub 저장소에서 샘플 앱을 다운로드하여, 어떤 기능을 적용할 수 있을지 미리 확인하시는 것을 권장합니다.

>샘플 앱 프로젝트에는 Kotlin 언어로 작성된 Klat UIKit 소스코드를 포함하고 있으므로 추가적으로 Klat UIKit SDK를 설치할 필요가 없습니다.

### 1. [Klat 대시보드](https://www.klat.kr/) > App ID 확인 및 익명 로그인 활성화
```
1-1. Klat 대시보드 로그인 또는 회원 가입
1-2. Klat 대시보드 > Apps > `새로운 앱 만들기` 버튼을 클릭하여 Klat 애플리케이션 생성
1-3. Klat 대시보드 > Apps > [생성된 앱 이름] > Settings > `App ID` 확인
1-4. Klat 대시보드 > Apps > [생성된 앱 이름] > Settings > `익명 로그인 (Anonymous user)` 활성화
```
### 2. 애플리케이션 식별자 (App ID), 채널 식별자 (Channel ID), 유저 식별자 (User ID) 입력
```
2-1. Klat UIKit 샘플 앱 프로젝트 파일 열기
2-2. `Application.kt` 파일 
      > `initialize 메소드 
      > `YOUR_APP_ID` 문자열을 위의 이전 단계에서 생성한 `App ID`로 교체
2-3. Android 샘플 앱 실행 후, 로그인 화면에서 유저 식별자 (`User ID`) 및 닉네임 (`Nick Name`) 입력한 다음에 로그인 버튼 (Sign In) 클릭
```

### 3. [Klat 대시보드](https://www.klat.kr/) > 채널 생성 및 멤버 추가
```
3-1. Klat 대시보드 > Apps > [생성된 앱 이름] > Channel > `채널 생성` 클릭
3-2. 채널 타입은 `PRIVATE` 선택, `채널명` 입력, `멤버 설정`에서 익명으로 로그인 된 유저를 검색하여 추가한 다음에 `생성` 버튼 클릭
     * 이 단계에서 샘플 앱의 채널 목록에 채널이 추가됩니다.
3-3. 샘플 앱 테스트
```
<br />

## 예제 코드 

### - Klat SDK 초기화 
1. Application 클래스를 상속받은 후, YOUR_APP_ID 자리에 생성한 App ID로 교체합니다.
 ```kotlin
class KlatApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        KlatUiKit.initialize(applicationContext, "YOUR_APP_ID")
    }
}
```

2. AndroidManifest.xml에 name속성에 생성한 Application을 상속받은 클래스를 설정해줍니다.
```xml
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:tools="http://schemas.android.com/tools">

    <application
        android:name=".KlatApplication">
    ...
    ...
/>
```

예제 코드는 다음 레포지토리를 참고해주세요.

### - 익명 및 토큰 로그인
 ```kotlin
KlatUiKit.connect(
    activity = this,
    loginRequest = LoginRequest(
        userId = binding.tvUserId.text.toString(),
        userNickname = binding.tvUserId.text.toString(),
        userToken = null
    )
)
```
1. `userToken = null`인 경우 익명로그인 입니다. (기본 값은 null 입니다.)
2. `userToken`이 유효한 값이면 토큰 로그인으로 진행됩니다.

### - 채팅 목록 및 채팅 화면으로 이동
```kotlin                
// 로그인 성공 후, 채팅 목록 화면 및 채통 화면으로 이동
KlatUiKit.connect(
    activity = this,
    oneToOneChannelId = null
    loginRequest = LoginRequest(
        userId = binding.tvUserId.text.toString(),
        userNickname = binding.tvUserId.text.toString()
    )
)
```
1. `oneToOneChannelId = null`인 경우 채팅 목록 화면으로 이동합니다.
2. `oneToOneChannelId`이 유효한 값이면 채팅 화면으로 이동합니다.

<br />

## 작성자

Neptune Company

<br />
