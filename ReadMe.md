## 개요
매장 테이블 예약 서비스 구현

Use : Spring, Jpa, Mysql, openApi(https://www.vworld.kr/dev/v4dv_geocoderguide2_s001.do)

목표 : 사용자가 매장을 방문할 때 미리 예약 서비스로 매장과 소비자에게 편의를 제공하는 서버를 구축한다.

기간 : 2023/06/13 ~ 2023/06/19

## Member api
- [x] 회원 가입
- [x] 로그인 토큰 발행 (JWT, filter 사용)

## Reservation api
### 공통
- [x] 상점 검색
- [x] 상점 상세 정보 확인
- [x] 상점 예약 목록 보기
- [x] review 조회
### user
- [x] 예약 요청
- [x] review 작성
### partner
- [x] 상점 등록
- [x] 예약 승인, 거절
### kiosk
- [x] 예약자 체크인 처리
- [x] 예약자 체크인 처리 후 서버에 알림
