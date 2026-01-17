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

```
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


---

## Day9 – 예매 만료 처리 (Timeout)

### 구현 내용
- HOLD 상태 예매에 만료 시간(expiredAt) 추가
- 일정 시간 내 결제되지 않으면 자동 취소
- 좌석 자동 해제 처리
- @Scheduled 기반 배치 처리

### 핵심 설계
- 상태 전이 로직을 Reservation 엔티티에 위임
- Service는 흐름 제어만 담당
- 서버 주도 정합성 유지

### 기술 포인트
- JPA Pessimistic Lock
- Domain-driven 상태 관리
- Scheduler 기반 자동 처리

----

## Day10 - 예매 만료 처리 (Reservation Expiration)

### 구현 내용
- HOLD 상태 예매에 시간 제한 도입
- 5분 초과 시 자동 취소
- 좌석 상태 자동 복구
- Spring Scheduler 기반 만료 처리

### 핵심 포인트
- 스케줄링을 통한 백그라운드 정리 작업
- 도메인 중심 설계 (만료 규칙 엔티티에 위치)
- 실무 예매 시스템 필수 기능 구현


---

# Day11 - Reservation Expire Scheduler & State Flow

## 1. 구현 목표
- HOLD 상태 예약 5분 후 자동 만료 처리
- 좌석 상태 자동 복구

## 2. 핵심 기능
- @Scheduled 기반 만료 처리
- Reservation 상태 전이 정리
- 도메인 메서드 중심 리팩토링

## 3. 상태 흐름
AVAILABLE → HOLD → CONFIRMED  
HOLD → EXPIRED  
CONFIRMED → CANCELLED

## 4. 배운 점
- 스케줄러를 통한 비즈니스 규칙 자동화
- 서비스 로직을 도메인으로 이동시키는 이유


---
# Day12 – Optimistic Lock 적용

## 1. 개요

Day12에서는 좌석 선점(HOLD) 로직의 동시성 제어 방식을  
기존 **DB 비관적 락(Pessimistic Lock)** 에서  
**JPA Optimistic Lock(@Version)** 기반 구조로 전환하였다.

이를 통해:
- DB 락 대기(blocking)를 제거하고
- 커밋 시점에 충돌을 감지하며
- 충돌 발생 시 사용자에게 **409 Conflict**로 명확히 응답하는
  확장성 있는 구조를 구현하였다.

---

## 2. 적용 배경

기존 구조는 다음과 같이 비관적 락을 사용하였다.

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
Optional<Seat> findByIdWithLock(...)


```
---
# Day13 – Redis 기반 좌석 HOLD 관리 (고급 설계)

## 🎯 목표

- 좌석 HOLD 상태를 **DB가 아닌 Redis**로 관리
- TTL(Time To Live)을 활용하여 **자동 만료 처리**
- DB에는 **확정(CONFIRMED) 예약만 저장**
- 스케줄러 기반 만료 로직 제거 → **Redis TTL로 대체**
- **결제(pay)가 확정 책임을 단일로 담당**하도록 구조 정리

---

## 🧩 설계 개요

| 구분 | 저장 위치 | 설명 |
|------|-----------|------|
| 좌석 AVAILABLE / OCCUPIED | RDB (Seat 테이블) | 최종 확정 상태만 관리 |
| 좌석 HOLD | Redis | 임시 선점, TTL 자동 만료 |
| 예약(CONFIRMED) | RDB (Reservation 테이블) | 결제 성공 시에만 생성 |
| 결제(Payment) | RDB (Payment 테이블) | Reservation 확정 이후 기록 |

> **핵심 원칙**  
> 임시 상태(HOLD)는 Redis, 영구 상태(CONFIRMED/OCCUPIED)는 DB로 분리하여  
> 성능, 확장성, 동시성 안전성을 동시에 확보

---
# Day14 – JWT 기반 인증/인가 구현 (Spring Security)

## 📌 개요
Day14에서는 Spring Security와 JWT(JSON Web Token)를 이용해 **Stateless 인증/인가 구조**를 구현하였다.  
로그인 시 JWT Access Token을 발급하고, 이후 모든 보호 API는 **Bearer Token 기반 인증**을 통해 접근하도록 구성하였다.

---

## 🎯 

- Spring Security + JWT 인증 구조 설계
- 로그인 시 Access Token 발급
- JwtFilter를 통한 요청 필터링 및 인증 처리
- SecurityContext에 사용자 정보 저장
- Role 기반 인가(Authorization) 구조 준비

---
# Day15 – 테스트 & 문서화 (Service Layer 중심)

## 1. Day15 목표

Day15의 목표는 **지금까지 구현한 핵심 비즈니스 로직을 테스트 코드로 검증**하는 것이다.  
특히 예매 흐름과 결제 흐름이라는 서비스의 핵심 로직을 **성공/실패/예외 시나리오까지 포함하여 검증**하는 데 집중하였다.

검증 대상:
- ReservationService (좌석 HOLD, 소유자 검증)
- PaymentService (결제, 좌석 확정, 보상 트랜잭션)

> 단순히 “동작하는 코드”를 넘어서,  
> **“신뢰할 수 있는 코드”로 만드는 것을 목표**로 한다.

---

## 2. 테스트 전략

### 2-1. 단위 테스트(Unit Test) + Mockito

- DB, Redis, 외부 결제 모듈(PG), Repository는 **모두 mock 처리**
- **Service 로직만 단독으로 검증**
- 빠르고, 실패 원인을 명확히 파악할 수 있는 구조

사용 기술:
- JUnit5
- Mockito
- assertThrows, assertEquals, verify

---



# Day16 – Docker 기반 로컬 배포 환경 구축

## 🎯 목표
Spring Boot 애플리케이션을 로컬 개발 환경(IntelliJ 실행)이 아닌  
**Docker 기반의 독립된 실행 환경에서 배포 형태로 실행**한다.

- Spring Boot 애플리케이션 JAR 빌드
- Docker 이미지로 패키징
- MySQL을 별도 컨테이너로 분리
- docker-compose로 애플리케이션 + DB 동시 실행
- JWT 인증이 포함된 API 호출까지 검증

---

## 🏗️ 시스템 구성

Client (curl / browser)
|
v
Spring Boot App (Docker Container)
|
v
MySQL (Docker Container)


- 애플리케이션과 데이터베이스를 **완전히 분리된 컨테이너**로 실행
- 실제 운영 환경과 유사한 구조를 로컬에서 재현

---

## 🧱 사용 기술

- Java 17
- Spring Boot 3.x
- Spring Security + JWT
- JPA (Hibernate)
- MySQL 8.0
- Docker
- Docker Compose

##배운점
배포의 핵심은 실행 환경 분리임을 이해

Docker 네트워크 환경에서의 DB 연결 방식 이해

JWT 인증이 포함된 상태에서 컨테이너 환경 테스트 경험

배포 환경에서 발생하는 403 / 500 오류를 직접 경험하며
이후 장애 분석(Day17)을 위한 기반 확보

---
Day17 — 장애 시나리오 대응 & 보상 트랜잭션
🎯 목표

실제 서비스 운영 환경에서 발생할 수 있는 결제–예약 불일치 문제를 가정하고,
트랜잭션 경계 설계와 **보상 트랜잭션(결제 취소)**을 통해 시스템 안정성을 강화한다.
1. Day17에서 다룬 핵심 문제
   ❌ 기존 구조의 한계

결제(PG)는 성공했지만,
좌석 확정 또는 예약 저장 과정에서 동시성 충돌 / DB 예외가 발생할 수 있음

이 경우 돈은 빠졌는데 예약은 실패하는 상태가 발생 가능

2. 주요 장애 시나리오
   (1) 결제 성공 → 좌석 확정 실패

외부 결제(PG)는 성공

좌석 occupy() 또는 예약 저장 중 OptimisticLockException 발생

(2) 결제 성공 → DB 제약 위반

중복 결제 / 유니크 제약 위반

결제는 승인됐지만 예약은 생성되지 않음

(3) 외부 결제 성공 이후 내부 예외

내부 비즈니스 예외(ApiException) 또는 알 수 없는 런타임 예외 발생

3. 해결 전략 (Day17 핵심 설계)
✅ 1) 결제와 DB 확정 로직 분리

외부 PG 결제는 트랜잭션 밖에서 수행

좌석 OCCUPIED / 예약 CONFIRMED / 결제 SUCCESS만 하나의 트랜잭션으로 묶음
✅ 2) 보상 트랜잭션(결제 취소) 도입

PG 결제 성공 이후 DB 확정 실패 시

PaymentGateway.cancel(txId)를 호출해 결제 취소(환불) 시도

✅ 3) Payment 상태 모델 확장

결제 과정을 상태로 명확히 표현하여 운영/장애 추적 가능하도록 설계

5. 테스트 시나리오
   ✔ 테스트 케이스 1: 정상 결제 성공

PG 결제 성공

좌석/예약 확정 성공

cancel 호출 없음

✔ 테스트 케이스 2: 결제 성공 → 좌석 확정 실패

PG 결제 성공

DB 확정 중 Optimistic Lock 예외

PaymentGateway.cancel(txId) 호출 검증

✔ 테스트 케이스 3: 결제 성공 → DB 제약 위반

PG 결제 성공

예약 저장 중 DataIntegrityViolationException

cancel 호출 검증

6. Day17에서 얻은 점

외부 시스템(PG)과 내부 트랜잭션을 분리하는 설계 경험

보상 트랜잭션을 통한 장애 대응 구조 이해

“결제는 성공했지만 예약은 실패한 상태”를 안전하게 처리하는 방법 학습

실무 서비스에 가까운 결제/예약 안정성 설계 경험