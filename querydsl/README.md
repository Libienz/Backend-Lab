<details>
<summary>Section 01 Proj Config </summary></summary>
<div markdown="1">

### build.gradle
```groovy
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.0.0'
	id 'io.spring.dependency-management' version '1.1.0'
}

group = 'study'
version = '0.0.1-SNAPSHOT'
sourceCompatibility = '17'

configurations {
	compileOnly {
		extendsFrom annotationProcessor
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.h2database:h2'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'

	// Querydsl 추가
	implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
	annotationProcessor "com.querydsl:querydsl-apt:${dependencyManagement.importedProperties['querydsl.version']}:jakarta"
	annotationProcessor "jakarta.annotation:jakarta.annotation-api"
	annotationProcessor "jakarta.persistence:jakarta.persistence-api"

}

tasks.named('test') {
	useJUnitPlatform()
}
```
</div>
</details>


<details>
<summary>Section 02 domain model</summary></summary>
<div markdown="1">


![img.png](img.png)

</div>
</details>


<details>
<summary>Section 03 Querydsl 기본 문법</summary></summary>
<div markdown="1">

### JPQL 사용 예시
```java
    @Test
    public void startJPQL() throws Exception {
        Member findMember = em.createQuery("select m from Member m where m.username = :username", Member.class)
                .setParameter("username", "member1")
                .getSingleResult();
        assertThat(findMember.getUsername()).isEqualTo("member1");
     }
```

### Querydsl 사용 예시

```java
     @Test
     public void startQuerydsl() throws Exception {

         JPAQueryFactory queryFactory = new JPAQueryFactory(em);
         QMember m = new QMember("m");

         Member findMember = queryFactory
                 .select(m)
                 .from(m)
                 .where(m.username.eq("member1"))
                 .fetchOne();

         assertThat(findMember.getUsername()).isEqualTo("member1");

      }
```
- JPQL 실행 시점 오류, Querydsl 컴파일 오류
- JPQL 파라미터 바인딩 직접, Querydsl 파라미터 바인딩 자동처리
- JPAQueryFactory를 필드로 제공하면 동시성 문제는 어떻게 될까? 
  - 동시성 문제는 JPAQueryFactory를 생성할 때 제공하는 EntityManager에 달려있다.
  - 스프링 프레임워크는 여러 쓰레드에서 동시에 같은 EntityManager에 접근해도, 트랜잭션 마다 별도의 영속성 컨텍스트를 제공하기 때문에, 동시성 문제는 걱정하지 않아도 된다.

### 기본 Q-Type 활용
- Qtype이란 쿼리를 빌드하기 위한 타입
- Q클래스 인스턴스를 사용하는 2가지 방법

```java
QMember qMember = new QMember("m"); //별칭 직접 지정
Qmember qMember = Qmember.member; //기본 인스턴스 사용
```

### 검색 조건 쿼리
- Querydsl은 JPQL이 제공하는 모든 검색 조건을 제공한다.

```java
member.username.eq("member1") // username = 'member1'
member.username.ne("member1") //username != 'member1'
member.username.eq("member1").not() // username != 'member1'
member.username.isNotNull() //이름이 is not null
member.age.in(10, 20) // age in (10,20)
member.age.notIn(10, 20) // age not in (10, 20)
member.age.between(10,30) //between 10, 30
member.age.goe(30) // age >= 30
member.age.gt(30) // age > 30
member.age.loe(30) // age <= 30
member.age.lt(30) // age < 30
member.username.like("member%") //like 검색
member.username.contains("member") // like ‘%member%’ 검색
member.username.startsWith("member") //like ‘member%’ 검색
...
```

### AND 조건을 파라미터로 처리

```java 

    @Test
    public void searchAndParam() throws Exception {
        Member findMember = queryFactory
                .select(member)
                .from(member)
                .where(
                        member.username.eq("member1"),
                        member.age.eq(10)
                )
                .fetchOne();
        assertThat(findMember.getUsername()).isEqualTo("member1");
    }

```
- where()에 파라미터로 검색조건을 추가하면 AND조건이 추가됨
- 이 경우 null 값은 무시 -> 메서드 추출을 활용해서 동적 쿼리를 깔끔하게 만들 수 있음

### 결과 조회
- fetch(): 리스트 조회, 데이터 없으면 빈 리스트 반환
- fetchOne(): 단 건 조회
  - 결과가 없으면 null
  - 결과가 둘 이상이면 NonUniqueResultException
- fetchFirst(): limit(1).fetchOne()
- fetchResults(): 페이징 정보 포함, totalCount 쿼리 추가 실행
- fetchCount(): count 쿼리로 변경해서 수 조회

```java

    @Test
    public void resultFetch() throws Exception {
        //리스트 조회
        List<Member> fetch = queryFactory
                .select(member)
                .from(member)
                .fetch();
        //단건 조회
        Member fetchOne = queryFactory
                .selectFrom(member)
                .fetchOne();
        //limit(1) 조회
        Member fetchFirst = queryFactory
                .selectFrom(member)
                .fetchFirst();

        //fetchResults.getTotal() 제공 등등 페이징 관련 제공
        QueryResults<Member> fetchResults = queryFactory
                .selectFrom(member)
                .fetchResults();
        fetchResults.getTotal();
        fetchResults.getLimit();
        fetchResults.getOffset();

        //countQuery
        long total = queryFactory
                .selectFrom(member)
                .fetchCount();

    }

```
</div>
</details>