

<details>
<summary>01. 영속성 컨텍스트 </summary>
<div markdown="1">

### 영속성 컨텍스트
- 엔티티를 영구 저장하는 환경

### 엔티티의 생명주기
- 비영속
  - (new/transient) 영속성 컨텍스트와 전혀 관계가 없는 새로운 상태
- 영속
  - 영속성 컨텍스트에 관리되는 상태
- 준영속
  - 영속성 컨텍스트에 저장되었다가 분리된 상태
- 삭제
  - 삭제된 상태 
  - em.remove(member) 객체 자체를 삭제

![img.png](imgs/img.png)

### 영속성 컨텍스트를 사용하여 얻는 이점
- 강의에서도 얘기했지만 항상 어떤 것과 어떤 것을 매핑하는 중간계층의 컨텍스트를 사용할 경우 크게 두 가지의 이점을 얻을 수 있다
1. buffered 작업
2. 캐시 이용
- 영속성 컨텍스트 역시 RDB와 객체 엔티티간의 매핑으로써 다음의 이점을 얻을 수 있다
  - 1차 캐시 
    - em.persist로 객체를 영속화 할 경우 em.find할 때 1차 캐시에서 조회할 수 있다.
    - DB connection과 반복되는 쿼리 작업을 을 줄일 수 있다.
    - 만약 em.find를 한경우 cache miss가 나면 DB에서 꺼내와서 1차 캐시에 객체를 담아 둔다.
  - 동일성 보장
    - Member a = em.find(Member.class, 1L);
    - Member b = em.find(Member.class, 1L);
    - a == b가 성립한다. 
    - 어려운 말로 1차 캐시로 반복 가능한 읽기 등급의 트랜잭션 격리 수준을 데이터베이스가 아닌 애플리케이션 차원에서 제공한다고 표현할 수 있다
  - 쓰기 지연
    - 트랜잭션을 지원하는 쓰기 지연
    - em.persist는 객체를 영속화 할 뿐 sql을 실행시키지 않는다.
    - 커밋하는 순간 데이터가 영구화 된다.
    - flush: 데이터베이스에 sql 저장소에 있는 쿼리들을 실행시켜 영속성 컨텍스트와 DB의 내용을 동기화
      - 다만 flush 후 커밋하지 않으면 트랜잭션 단위 roll back이 가능한 상태이다.
    ![img.png](img.png)
  - 변경 감지
    - 이전 실전편에서 데이터를 수정할 경우 memberA.setUsername("h");와 같이 수정하고 따로 persist 할 필요가 없는 것을 보았었다
    - 이와 같은 과정이 가능한 것은 영속성 컨텍스트에 엔티티와 스냅샷을 비교하는 과정이 있기 때문 
    ![img_1.png](img_1.png)
  - 지연 로딩
- 
</div>
</details>


<details>
<summary>02. 엔티티 매핑 </summary>
<div markdown="1">

### @Entitiy
- @Entity가 붙은 클래스는 jpa가 관리, 엔티티라 한다.
- 테이블과 객체를 매핑할 클래스에 @Entity를 붙여주면 된다.
- 어노테이션을 사용하기 위해서 기본 생성자 필수!(접근 제어는 public 혹은 protected)
- final 클래스, enum, interface, inner 클래스는 사용 불가
- 저장할 필드에 final 사용 X

### 데이터베이스 스키마 자동 생성
- DDL을 애플리케이션 실행 시점에 자동 생성
- propertiy 파일에 DDL 행동의 속성을 설정할 수 있는데 create, create-drop, update, validate, none과 같은 속성들이 있다 
- 운영 장비에는 절대 create, create-drop, update를 사용하지 말자 (전부 지워버리는 수가 있다)
- 개발 초기 단계는 create 또는 update
- 많은 개발자들이 모여서 사용하는 테스트 서버는 update 또는 validate
- 스테이징과 운영 서버는 validate 또는 none을 사용하자

### 필드와 컬럼 매핑
### @Column
  - name: 필드와 매핑할 테이블의 컬럼 이름
  - insertable, updatable: 등록, 변경 가능 여부
  - nullable: 컬럼에 유니크 제약조건을 걸 때 사용
  - columnDefinition: 데이터베이스 컬럼 정보를 직접 줄 수 있다. 
    - ex) varchar(100) default ‘EMPTY'
  - length: 문자 길이 제약 조건
  - precision, scale: bigDecimal 타입에서 사용 floating number의 정밀도와 scale 지정
### @Enumerated
  - 자바 enum 타입을 매핑할 때 사용
  - 두가지 ORDINAL과 STRING 이쓴ㄴ데 항상 STRING을 사용하도록 하자 
### @Temporal
  - 날짜 타입을 매핑할 떄 사용
  - but 충분히 높은 버젼의 hibernate을 사용하고 있다면 LocalDate, LocalDateTime을 @Temporal없이 사용 가능
### @Lob
  - 데이터베이스 BLOB, CLOB 타입과 매핑
  - 지정할 수 있는 속성 없음
  - 매핑하는 필드 타입이 문자면 CLOB 매핑, 나머지는 BLOB 매핑
### @Transient
  - 필드 매핑 X
  - 데이터베이스에 저장 X

### 기본 키 매핑 어노테이션

### @Id
- PK임을 알림
### @GeneratedValue
- 키를 자동 생성 하도록 설정
- Strategies
  - IDENTITY: 데이터베이스에 위임
    - DB에 넣을 때 null로 주고 DB에서 키를 알아서 생성하도록 위임한다
    - 다만 persist 시점에 영속성 컨텍스트에서 관리하기 위해 SQL을 flush시점이 아니더라도 persist 시점에 바로 푸쉬한다.  
    - 버퍼의 장점을 누리지 못하지만 생각보다 cardinal한 성능저하는 일어나지 않음
  - SEQUENCE: 데이터베이스 시퀀스 오브젝트 사용
    - 오라클에서 많이 사용
    - jpa가 시퀀스 값을 db로 부터 가져와서 메모리에 저장한다. 
    - 데이터 베이스에 call next value 쿼리를 보내어 시퀀스 오브젝트의 값을 가져온다
    - 시퀀스 값은 db에서 미리 정의 되어 있는 것이 IDENTITY와의 차이점 (가져온 후에 영속성 컨텍스트에 저장하는 것도 차이점)
    - 시퀀스 제네레이터는 커스텀 가능
      - 특히 주의 깊게 볼 속성은 allocationSize
      - allocationSize = 50(default)으로 하면 DB에서 시퀀스를 가져올 때 50개를 가져와서 로컬 메모리에 저장할 수 있다
      - 1번 할때마다 네트워크를 타면 부담스러우니 allocationSize이용하면 성능 최적화를 이룰 수 있다
      - 이론적으로는 사이즈가 크면 클수록 좋지만 웹서버를 내리는 시점에 id 값의 구멍이 생길 수 있다 (굳이 구멍 생겨도 문제는 없지만)
  - TABLE: 키 생성용 테이블 사용, 모든 DB에서 사용
    - 테이블을 직접사용하다 보니 락도 걸릴 수 있고 성능이 떨어질 수 있음
    - 잘 사용되는 매핑 전략은 아님
  - AUTO: 방언에 따라 자동 지정, default
- 결론은 뭘쓰냐!? 
  - 기본 키는 null이면 안되고 유일해야 하고 변하면 안된다.(서비스의 요소를 pk로 끌어오지 말자)
  - 권장: Long형 + 대체키 + 키 생성전략 사용(AUTO_INCREMENT나 SEQUENCE 전략 사용)
- 

</div>
</details>



<details>
<summary>03. 연관관계 매핑 </summary>
<div markdown="1">



</div>
</details>