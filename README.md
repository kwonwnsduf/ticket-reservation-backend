# Ticket Reservation Backend (Spring Boot)

좌석/티켓 예약 시스템 백엔드 프로젝트 (학습 + 포트폴리오 목적)  
Day1 목표: **프로젝트 실행 환경 세팅 + 회원(Member) 도메인 최소 기능(회원가입/로그인) 완성**

---

## Tech Stack
- Java 17
- Spring Boot 3.x
- Spring Web (REST API)
- Spring Data JPA (ORM)
- H2 Database (in-memory, 개발용)
- Lombok
- Gradle

---

## Project Structure (Layered Architecture)
- `presentation` : Controller (HTTP 요청/응답)
- `application` : Service (비즈니스 로직)
- `domain` : Entity/Repository (도메인 모델, 영속성)
- `global` : 공통 응답 DTO, 예외 처리

예)
- `com.example.ticket.presentation.member` : MemberController
- `com.example.ticket.application.member` : MemberService
- `com.example.ticket.domain.member` : Member, MemberRepository
- `com.example.ticket.global` : ApiResponse, ApiException, GlobalExceptionHandler

---

# Day1 (완료)
## 1. Gradle 프로젝트 실행 & IDE 세팅
- IntelliJ에서 프로젝트 오픈 시 **Gradle 프로젝트로 인식되도록** 설정
- JDK 설정(17/21 후보 중 프로젝트에 맞는 버전 사용)
- Gradle 동기화 오류/Toolchain 관련 문제를 확인하고 SDK 설정으로 해결
- 서버 실행 확인: Spring Boot 배너 및 Tomcat 기동 로그 확인

---

## 2. H2 + JPA 환경 세팅 (application.properties)
개발 편의를 위해 H2(in-memory) + JPA 설정을 추가했다.

주요 설정:
- H2 in-memory DB 사용
- JPA ddl-auto=create (실행 시 테이블 자동 생성)
- SQL 로그 출력(show-sql, format_sql)
- H2 콘솔 활성화 (`/h2-console`)

> 실수로 properties 내용이 줄바꿈 없이 붙어서 설정이 적용되지 않을 수 있으므로  
> **각 설정은 반드시 한 줄씩 분리해서 작성**했다.

---

## 3. H2 Console 접속 이슈 해결 기록
H2 콘솔에서 접속 실패가 발생했는데, 원인은 **JDBC URL 불일치**였다.

- Spring 설정: `jdbc:h2:mem:testdb`
- H2 콘솔 입력: `jdbc:h2:mem:shop` (오류)

해결:
- H2 콘솔 JDBC URL을 `jdbc:h2:mem:testdb`로 맞춰 접속 성공

---

## 4. Member 도메인 구현 (Entity/Repository)
### Member Entity
- `@Entity`, `@Table(name="members")`로 테이블 매핑
- `email`에 DB 레벨 unique 제약조건 적용 (`unique(email)`)
- JPA 기본 생성자 필요 → `@NoArgsConstructor(access = PROTECTED)`
- 객체 생성 안전성/가독성 위해 `@Builder` 적용

필드:
- id (PK, auto increment)
- email (unique, not null)
- password (Day1은 평문 테스트용 / Day2 이후 암호화 예정)
- name (not null)

서버 실행 로그에서 Hibernate가 `members` 테이블을 생성하는 것까지 확인함.

### MemberRepository
- `JpaRepository<Member, Long>` 상속으로 기본 CRUD 자동 제공
- 이메일 중복 체크: `existsByEmail(String email)`
- 이메일로 조회: `findByEmail(String email)` (Optional 사용)

---

## 5. 공통 API 응답/예외 처리(Global)
### ApiResponse<T>
응답 형태를 통일하기 위해 공통 응답 DTO를 만들었다.

- success: 성공 여부
- data: 성공 데이터
- message: 실패 메시지

정적 팩토리 메서드 사용:
- `ApiResponse.ok(data)` : 성공 응답 생성
- `ApiResponse.fail(message)` : 실패 응답 생성

### ApiException + GlobalExceptionHandler
- 서비스 로직에서 에러 발생 시 `ApiException(status, message)`를 던지고
- `@RestControllerAdvice`에서 전역 처리하여 응답을 통일했다.

추가로 Validation 예외(`MethodArgumentNotValidException`)도 처리하여
요청값 검증 실패 시 400 + 메시지를 내려줄 수 있게 구성했다.

---

## 6. Member API 구현 (Controller)
### MemberController
Base path:
- `/api/members`

Endpoints:
- `POST /api/members/signup` : 회원가입
- `POST /api/members/login` : 로그인

요청/응답 DTO를 컨트롤러 내부 static class로 구성했고,
요청값 검증을 위해 `@Email`, `@NotBlank` 어노테이션을 사용했다.
(Validation 동작을 위해 필요 시 `@Valid` 적용)

---

## 7. Postman 테스트 및 디버깅 기록
Postman으로 회원가입 요청을 보냈을 때 404가 발생했는데,
원인은 실제 매핑 경로에 **오타(sginup)**가 있었기 때문이었다.

해결:
- 서버 로그에서 매핑 목록을 확인하여 `{POST [/api/members/sginup]}`를 발견
- 매핑 경로를 `signup`으로 수정하여 정상 동작 확인

최종적으로 회원가입 요청 성공 및 응답(JSON) 확인 완료.

---

## 8. 확인 완료
- 서버 기동 성공
- Member API (회원가입/로그인) 동작 확인
- H2 콘솔 접속 성공
- Hibernate 테이블 생성 로그 확인 (members)

---

## Day 2
### 전역 예외 처리 및 Validation
- @Valid를 이용한 요청 값 검증
- DTO에 @Email, @NotBlank 적용
- GlobalExceptionHandler(@RestControllerAdvice) 구현
- MethodArgumentNotValidException 전역 처리
- ErrorResponse를 통한 에러 응답 구조 분리
- ResponseEntity를 사용한 HTTP Status 제어


---

## Day3 - JPA 연동 & 예매 도메인 구현

### ✅ 목표
- Spring Data JPA 연동
- Event / Seat / Reservation 도메인(Entity) 설계
- Repository / Service / Controller 구현
- 예매 생성 로직(중복 예매 방지) 추가
- 전역 예외 처리(ErrorCode, ApiException)와 연동

### ✅ 구현 내용
- **Entity**
    - Event (공연)
    - Seat (좌석) - (event_id, seat_no) 유니크 제약
    - Reservation (예약) - (event_id, seat_id) 유니크 제약
- **Repository**
    - EventRepository, SeatRepository, ReservationRepository
    - ReservationRepository: `findByEventIdAndSeatId()`로 중복 예매 체크
- **Service**
    - EventService / SeatService / ReservationService
    - ReservationService에서 이미 예약된 좌석이면 `ALREADY_RESERVED` 예외 발생
- **Controller**
    - /api/events (공연 CRUD)
    - /api/events/{eventId}/seats (좌석 생성/조회)
    - /api/events/{eventId}/reservations (예매 생성)

### ✅ 테스트
- Postman으로 공연 생성 → 좌석 생성 → 예매 생성 흐름 확인
- 동일 좌석 재예매 시 409(CONFLICT) 에러 확인


----
### Day 4
- Reservation 도메인 설계
- 좌석 예매 생성 API 구현
- PESSIMISTIC_WRITE 락을 이용한 동시성 제어
- 이미 예매된 좌석에 대한 예외 처리


---
# Day5 – Reservation HOLD / CONFIRM / CANCEL

Day5에서는 기존 즉시 예약 방식(Day4)을 확장하여  
**결제 전 좌석 임시 점유(HOLD)** 개념을 도입한 예매 흐름을 구현했습니다.

---

## 핵심 개념

### Reservation Status
- `HOLD` : 결제 전 임시 예매 상태
- `CONFIRMED` : 결제 완료 후 확정 상태
- `CANCELED` : 임시 예매 취소 상태

### Seat Status
- `AVAILABLE` → `HELD` → `SOLD`
- HOLD 상태에서만 CONFIRM 가능

---

## Day5 주요 기능

### 1️⃣ 임시 예매 생성 (HOLD)
좌석을 임시로 점유하고 Reservation을 생성합니다.

- **POST** `/api/reservations`
- **Body**
```json
{
  "seatId": 10
}


---
## Day 6 - 예매 확정 및 취소 처리

- 예매 상태(PENDING / CONFIRMED / CANCELLED) 도입
- 결제 실패 시 트랜잭션 롤백 처리
- JPA Dirty Checking을 이용한 상태 변경
- 예매 확정/취소 API 구현
----

Day 7 - 예매 취소 및 트랜잭션 처리

- 예매 취소 시 예약/좌석 상태 동기화
- @Transactional을 통한 원자성 보장
- 중간 실패 시 전체 롤백 처리
- 엔티티 내부에 비즈니스 로직 캡슐화

---

## Day8 - 예매 취소 및 만료 처리

### 구현 내용
- 예매 취소 API 구현
- HOLD 상태 예약 자동 만료 처리
- Reservation / Seat 상태 전이 정리
- 스케줄러 기반 데이터 정합성 유지

### 핵심 포인트
- HOLD 상태는 영구 상태가 아님
- 만료 책임은 Reservation 도메인
- 좌석 상태 변경은 Reservation 흐름에 종속

### 배운 점
- 실무에서 타임아웃 처리는 필수
- 도메인 책임 분리가 중요
- 스케줄러 + 트랜잭션 조합 이해



