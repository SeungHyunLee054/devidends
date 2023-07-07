# Zerobase Practice Project 03 Dividends
## 사용한 기능
### Tool
1. IntelliJ
### database
1. H2 Database
### plugins
1. Spring boot
2. Gradle
3. Junit5
4. JPA
5. Mockito
6. Lombok
7. Security
8. Redis
9. jsoup
10. jjwt
11. collections4
## 제공하는 기능(API)
### 회사(Company) 관련 기능
1. 회사 이름으로 해당 회사의 메타 정보와 배당금 정보를 반환
2. 회사명 자동 완성
3. 모든 회사 목록 반환
4. 새로운 회사 정보 추가
5. ticker 에 해당하는 회사 정보 삭제

### 권한(auth) 관련 기능
1. 회원가입
2. 로그인

### 로그(log) 관련 기능
1. logback을 이용한 로그 기록 저장
2. 모든 로그를 저장하는 파일과 error만을 저장하는 파일을 나눔
3. 일정 용량이상이 되면 자동으로 압축

### 배당금 저장 기능
1. 일정 주기 마다 저장된 회사 목록 조회 후 회사마다 배당금 정보를 새로 스크래핑 후 새로운 배당금 정보 저장

### 스크래핑 기능
1. yahoo 포털사이트에서 정보를 스크래핑(다른 사이트에서도 가능)

### 캐싱 기능
1. 레디스 서버를 통해 자주 사용하는 데이터를 캐싱 / 삭제

-----
### 배운점
* reids server를 활용하여 자주 사용하는 데이터를 캐싱는 방법
* 스크래핑을 하여 원하는 정보를 사이트에서 가져오는 방법
* security를 이용해서 권한을 사용하는 방법

-----
### 어려웠던 점
* TDD를 만드는 것. security때문에 테스트 케이스를 만드는 것이 어려웠다. 몇몇은 실패...
* security를 활용하는 것을 완벽하게 이해하지 못한 것 같다.
* windows 환경에서 redis server 구축이 어려웠다.
