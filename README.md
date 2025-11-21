# 캘린더 일정 관리 앱 - Android (Kotlin Jetpack Compose)

React로 작성된 캘린더 일정 관리 앱을 Kotlin Jetpack Compose로 완전히 변환한 Android 앱입니다.

## 주요 기능

### 1. 캘린더 화면
- 월별 캘린더 뷰
- 일정이 있는 날짜 표시
- 선택한 날짜의 일정 목록 표시
- 플로팅 액션 버튼으로 일정 추가

### 2. 일정 목록 화면
- 날짜별로 그룹화된 전체 일정 목록
- 전체 일정 개수 통계
- 오늘 일정 강조 표시

### 3. 인사이트 화면
- 전체 일정, 이번 주 일정, 다가오는 일정 통계
- 평균 일정 시간 계산
- 요일별 일정 분포 차트
- 시간대별 일정 분포
- 자주 가는 장소 Top 3

### 4. AI 검색 화면
- 자연어로 일정 검색 (UI만 구현)
- 채팅 형식의 대화형 인터페이스
- 제안 질문 제공

### 5. 일정 관리
- 일정 추가 다이얼로그
- 일정 수정 다이얼로그
- 일정 상세 보기 다이얼로그
- 일정 삭제 기능

## 기술 스택

- **언어**: Kotlin
- **UI**: Jetpack Compose
- **아키텍처**: MVVM (ViewModel)
- **최소 SDK**: 26 (Android 8.0)
- **타겟 SDK**: 34 (Android 14)

## 프로젝트 구조

```
android/
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/example/calendar/
│       │   ├── MainActivity.kt          # 메인 액티비티
│       │   ├── CalendarApp.kt          # 앱 메인 컴포저블
│       │   ├── models/
│       │   │   └── Event.kt            # 이벤트 데이터 모델
│       │   ├── viewmodels/
│       │   │   └── CalendarViewModel.kt # 뷰모델
│       │   ├── ui/
│       │   │   ├── components/
│       │   │   │   ├── TossHeader.kt           # 헤더 컴포넌트
│       │   │   │   ├── BottomTabBar.kt         # 하단 탭바
│       │   │   │   ├── AddEventDialog.kt       # 일정 추가 다이얼로그
│       │   │   │   ├── EditEventDialog.kt      # 일정 수정 다이얼로그
│       │   │   │   └── EventDetailDialog.kt    # 일정 상세 다이얼로그
│       │   │   ├── screens/
│       │   │   │   ├── CalendarPage.kt         # 캘린더 화면
│       │   │   │   ├── EventListPage.kt        # 일정 목록 화면
│       │   │   │   ├── AISearchPage.kt         # AI 검색 화면
│       │   │   │   └── InsightPage.kt          # 인사이트 화면
│       │   │   └── theme/
│       │   │       ├── Color.kt                # 색상 정의
│       │   │       ├── Theme.kt                # 테마 설정
│       │   │       └── Type.kt                 # 타이포그래피
│       └── res/
│           └── values/
│               ├── strings.xml
│               └── themes.xml
├── build.gradle.kts
├── settings.gradle.kts
└── gradle.properties
```

## 빌드 및 실행

### 사전 요구사항
- Android Studio Hedgehog (2023.1.1) 이상
- JDK 17
- Android SDK 34
- Kotlin 1.9.20

### 빌드 방법

1. Android Studio에서 `android` 디렉토리를 엽니다.

2. Gradle 동기화를 수행합니다:
   ```
   File > Sync Project with Gradle Files
   ```

3. 앱을 실행합니다:
   - 에뮬레이터 또는 실제 기기를 연결합니다.
   - Run 버튼을 클릭하거나 `Shift + F10`을 누릅니다.

### 빌드 명령어 (터미널)

```bash
# 디버그 빌드
cd android
./gradlew assembleDebug

# 릴리즈 빌드
./gradlew assembleRelease

# 앱 설치 및 실행
./gradlew installDebug
```

## 주요 의존성

```kotlin
// Compose
implementation(platform("androidx.compose:compose-bom:2024.02.00"))
implementation("androidx.compose.ui:ui")
implementation("androidx.compose.material3:material3")
implementation("androidx.compose.material:material-icons-extended")

// Navigation
implementation("androidx.navigation:navigation-compose:2.7.7")

// ViewModel
implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
```

## React 앱과의 차이점

### 구조적 차이
- **상태 관리**: React의 `useState`를 Compose의 `mutableStateOf`와 ViewModel로 변환
- **컴포넌트**: React 컴포넌트를 Compose 함수로 변환
- **스타일링**: CSS/인라인 스타일을 Compose Modifier로 변환
- **라우팅**: React Router 대신 탭 기반 네비게이션 사용

### 기능적 차이
- AI 검색 기능은 UI만 구현 (실제 API 호출 미구현)
- 차트는 Canvas 기반 커스텀 컴포넌트로 구현
- 날짜/시간 입력은 TextField로 구현 (DatePicker는 향후 추가 가능)

## 샘플 데이터

앱은 다음과 같은 샘플 일정으로 시작합니다:
- 팀 미팅 (2025-11-06, 9:00 AM - 10:00 AM)
- 프로젝트 리뷰 (2025-11-06, 2:00 PM - 3:30 PM)
- 고객 프레젠테이션 (2025-11-06, 4:00 PM - 5:00 PM)
- 점심 약속 (2025-11-08, 12:00 PM - 1:00 PM)

## 향후 개선 사항

1. **로컬 데이터베이스 연동** (Room Database)
2. **실제 AI API 연동** (OpenAI, Gemini 등)
3. **DatePicker/TimePicker 추가**
4. **알림 기능 구현**
5. **일정 카테고리/태그 기능**
6. **일정 반복 기능**
7. **다크 모드 지원**
8. **위젯 지원**

## 라이선스

이 프로젝트는 React 앱을 Kotlin Jetpack Compose로 변환한 예제입니다.

## 문의

문제나 제안사항이 있으시면 이슈를 등록해주세요.
