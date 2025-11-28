# 🥙 AI가 분석하는 건강 식단 배틀, 푸파

<br>

# 🗂️ 코드 구조도

```
src/
├── main/
│   ├── java/
│   │   └── com/ssafy/foofa/
│   │               ├── core            # 공통 유틸리티 및 설정
│   │               │   ├── annotation    # 커스텀 어노테이션
│   │               │   ├── auth          # JWT 관리 및 인증
│   │               │   ├── config        # Spring, 웹 관련 설정
│   │               │   ├── serialization # 직렬화
│   │               │   └── exception     # 커스텀 예외
│   │               ├── battle          # 배틀 관련 모듈
│   │               │   ├── application   # 애플리케이션 서비스
│   │               │   ├── domain        # 도메인 엔티티 및 리포지토리
│   │               │   ├── infra         # 외부 API 연동
│   │               │   └── presentation  # API (Controller) 및 DTO
│   │               ├── identity        # 사용자 및 로그인 관련 모듈
│   │               │   ├── application
│   │               │   ├── domain      
│   │               │   ├── infra         # 외부 API 연동
│   │               │   └── presentation
│   │               ├── chat            # 채팅 관련 모듈
│   │               └── FoofaApplication.kt
│   └── resources                       # 리소스 파일 (설정, 스크립트 등)
│       ├── application.yaml              # 개발 환경 설정
│       ├── application-prod.yaml         # 운영 환경 설정
└── test/                               # 테스트 관련 코드
    ├── java                              # 테스트 코드
    └── resources                         # 테스트용 리소스
```

<br>

# 🤝 그라운드 룰 & 컨벤션
- [브랜치 전략 및 커밋 컨벤션](https://github.com/codrin2/foofa-BE/wiki/%EB%B8%8C%EB%9E%9C%EC%B9%98-%EC%A0%84%EB%9E%B5-%EB%B0%8F-%EC%BB%A4%EB%B0%8B-%EC%BB%A8%EB%B2%A4%EC%85%98)
- [코드 컨벤션](https://github.com/codrin2/foofa-BE/wiki/%EC%BD%94%EB%93%9C-%EC%BB%A8%EB%B2%A4%EC%85%98)
