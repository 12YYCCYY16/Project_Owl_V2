# Project_Owl_V2

> **"밤의 미식가들을 위한 완벽한 동반자"**  
> 심야식당 OWL은 사용자의 현재 위치를 기반으로 심야에도 운영하는 음식점, 카페, 술집을 스마트하게 찾아주는 프리미엄 웹 서비스입니다.

---

## ✨ 주요 특징 (Key Features)

### 🗺️ 스마트 지도 서비스 (Google Maps Integration)
- **실시간 영업 정보**: Google Places API를 연동하여 현재 영업 중인 장소만 필터링하여 보여줍니다.
- **다이나믹 반경 설정**: 100m부터 1000m까지 사용자가 원하는 반경 내의 장소를 즉시 탐색합니다.
- **상세 정보 인터페이스**: 유리 질감(Glassmorphism) UI를 적용한 인포윈도우를 통해 영업 상태, 평점, 주소를 한눈에 확인하고 구글 검색으로 바로 연결됩니다.

### 🎡 오늘 뭐 먹지? (Smart Roulette)
- **메뉴 결정의 즐거움**: 한식, 일식, 치킨, 술집 등 카테고리별 가중치를 적용한 세련된 룰렛 애니메이션으로 메뉴 결정을 도와줍니다.
- **인터랙티브 경험**: 부드러운 회전 효과와 시각적 피드백으로 사용자 재미를 극대화했습니다.

### 🛡️ 강력한 보안 및 관리 시스템
- **Spring Security 기반 인증**: 세션 기반의 견고한 보안 체계를 바탕으로 회원가입 및 로그인을 관리합니다.
- **계층형 권한 관리 (RBAC)**: 일반 사용자(USER)와 관리자(ADMIN) 권한을 분리하여 차별화된 기능을 제공합니다.
- **관리자 전용 기능**: 관리자 전용 대시보드를 통한 회원 강제 탈퇴, 게시글 전체 관리 기능을 포함합니다.

### 📱 현대적인 UI/UX 디자인
- **Glassmorphism & Dark Mode**: 밤시간 사용 환경에 최적화된 Sleek한 다크 모드와 투명한 유리 질감 디자인을 채택했습니다.
- **Floating Navbar**: 데스크탑과 모바일 환경을 모두 고려한 플로팅 네비게이션 바로 최상의 접근성을 제공합니다.
- **반응형 웹**: 모바일 독(Dock) UI를 포함하여 모든 디바이스에서 완벽한 경험을 제공합니다.

---

## 🛠️ 기술 스택 (Tech Stack)

### Backend
- **Framework**: Spring Boot 2.7.x
- **Security**: Spring Security (Role-based Authorization)
- **Data**: Spring Data JPA, MySQL
- **Tooling**: Gradle, Lombok

### Frontend
- **Logic**: JavaScript (ES6+), jQuery
- **Styling**: Tailwind CSS (Modern Utility-first)
- **Template Engine**: Thymeleaf (with Spring Security integration)
- **Design System**: Vanilla CSS with Glassmorphism tokens

### Infrastructure & API
- **Cloud Hosting**: Azure App Service (Linux)
- **Managed Database**: Azure Database for MySQL
- **External API**: Google Maps JavaScript API (Places, Geolocation)

---

## 🚀 배포 정보

본 프로젝트는 **Microsoft Azure** 클라우드 환경에 최적화되어 배포되었습니다.
- **Production Profile**: `application-prod.yml`을 통한 환경 변수 관리 및 성능 최적화(Caching) 적용.
- **CI/CD**: GitHub Actions 연동을 통한 지속적 배포 파이프라인 구축.

---

## 👨‍💻 문제 해결 및 개선 사항

- **성능 최적화**: 저사양 클라우드 티어(B1)에서의 성능 확보를 위해 Thymeleaf 캐싱 및 서버 스레드 튜닝 적용.
- **보안 강화**: 하드코딩된 민감 정보를 모두 제거하고 환경 변수(`Environment Variables`)와 프로필 시스템으로 전환.
- **사용자 경험**: 하드코딩된 로컬 주소(localhost)를 상대 경로로 전환하여 클라우드 환경에서의 리소스 로딩 지연 해결.

---

© 2025 OWL Project Team. All rights reserved.
