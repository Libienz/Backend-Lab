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

### 정렬

```java

    /**
     * 1. 나이 내림 차순(desc)
     * 2. 회원 이름 올림차순(asc)
     * 단 2에서 회원 이름이 없으면 마지막에 출력(nulls last)
     */
    @Test
    public void sort() throws Exception {
        em.persist(new Member(null, 100));
        em.persist(new Member("member5", 100));
        em.persist(new Member("member6", 100));

        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(100))
                .orderBy(member.age.desc(), member.username.asc().nullsLast())
                .fetch();

        Member member5 = result.get(0);
        Member member6 = result.get(1);
        Member memberNull = result.get(2);

        assertThat(member5.getUsername()).isEqualTo("member5");
        assertThat(member6.getUsername()).isEqualTo("member6");
        assertThat(memberNull.getUsername()).isNull();

    }

```

### 페이징

- 조회 건수 제한


```java
    @Test
    public void paging1() throws Exception {
        List<Member> result = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetch();
        assertThat(result.size()).isEqualTo(2);
    }
```

- 전체 조회 수가 필요한 경우

```java


    @Test
    public void paging2() throws Exception {
        QueryResults<Member> queryResults = queryFactory
                .selectFrom(member)
                .orderBy(member.username.desc())
                .offset(1)
                .limit(2)
                .fetchResults();
        assertThat(queryResults.getTotal()).isEqualTo(4);
        assertThat(queryResults.getLimit()).isEqualTo(2);
        assertThat(queryResults.getOffset()).isEqualTo(1);
        assertThat(queryResults.getResults().size()).isEqualTo(2);

    }


```

### 집합


```java
    @Test
    public void aggregation() throws Exception {
        List<Tuple> result = queryFactory
                .select(
                        member.count(),
                        member.age.sum(),
                        member.age.avg(),
                        member.age.max(),
                        member.age.min())
                .from(member)
                .fetch();

        Tuple tuple = result.get(0);
        assertThat(tuple.get(member.count())).isEqualTo(4);
        assertThat(tuple.get(member.age.sum())).isEqualTo(100);
        assertThat(tuple.get(member.age.avg())).isEqualTo(25);
        assertThat(tuple.get(member.age.max())).isEqualTo(40);
        assertThat(tuple.get(member.age.min())).isEqualTo(10);

    }


```


### GroupBy

```java


    /**
     * 팀의 이름과 각 팀의 평균 연령을 구해라
     * @throws Exception
     */
    @Test
    public void group() throws Exception {
        List<Tuple> result = queryFactory
                .select(team.name, member.age.avg())
                .from(member)
                .join(member.team, team)
                .groupBy(team.name)
                .fetch();

        Tuple teamA = result.get(0);
        Tuple teamB = result.get(1);

        assertThat(teamA.get(team.name)).isEqualTo("teamA");
        assertThat(teamA.get(member.age.avg())).isEqualTo(15);

        assertThat(teamB.get(team.name)).isEqualTo("teamB");
        assertThat(teamB.get(member.age.avg())).isEqualTo(35);


    }

```
- 그룹화된 결과를 제한하려면 having을 추가하자


```java

        …
        .groupBy(item.price)
        .having(item.price.gt(1000))
        …

```

### 조인 - 기본 조인
- 기본 조인: 조인의 기본 문법은 첫 번쟤 파라미터에 조인 대상을 지정하고, 두 번째 파라미터에 별칭으로 사용할 Q 타입을 지정하면 된다.

```java

    @Test
    public void join() throws Exception {
        List<Member> result = queryFactory
                .selectFrom(member)
                .join(member.team, team)
                .where(team.name.eq("teamA"))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("member1", "member2");
    }
```

### 세타 조인
- 연관관계가 없는 필드로 조인

```java


    @Test
    public void theta_join() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Member> result = queryFactory
                .select(member)
                .from(member, team)
                .where(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }



```
- from 절에 여러 엔티티를 선택함으로서 세타 조인이 가능하다
- 외부 조인이 불가능하다. 다음에 설명할 조인 on을 사용하면 외부 조인 가능

### 조인 ON절

- ON절을 활용하면
  - 조인 대상을 필터링 할 수 있고
  - 연관관계가 없는 엔티티의 외부 조인이 가능하다.

1. 조인 대상 필터링

```java


    /**
     * 회원과 팀을 조인, 팀 이름이 teamA인 팀만 조인, 회원은 모두 조회
     * JPQL: select m, t from Member m left join m.team t on t.name = 'teamA'
     * @throws Exception
     */
    @Test
    public void join_on_filtering() throws Exception {

        List<Tuple> teamA = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(member.team, team).on(team.name.eq("teamA"))
                .fetch();
    }

```

2. 연관관계 없는 엔티티 외부 조인

```java

    /**
     * 연관관계 없는 엔티티 외부 조인
     * @throws Exception
     */
    @Test
    public void join_on_no_relation() throws Exception {
        em.persist(new Member("teamA"));
        em.persist(new Member("teamB"));

        List<Tuple> result = queryFactory
                .select(member, team)
                .from(member)
                .leftJoin(team).on(member.username.eq(team.name))
                .fetch();

        assertThat(result)
                .extracting("username")
                .containsExactly("teamA", "teamB");
    }

```


### 페치 조인

```java


    @Test
    public void fetchJoin() throws Exception {
        em.flush();
        em.clear();

        Member member1 = queryFactory
                .selectFrom(member)
                .join(member.team, team).fetchJoin()
                .where(member.username.eq("member1"))
                .fetchOne();
    }

```

### 서브 쿼리

```java 
    /**
     * 나이가 가장 많은 회원 조회
     * @throws Exception
     */
    @Test
    public void subQuery() throws Exception {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.eq(select(memberSub.age.max())
                        .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(40);

    }


    /**
     * 나이가 평균 이상인 회원 조회
     * @throws Exception
     */
    @Test
    public void subQueryGoe() throws Exception {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.goe(select(memberSub.age.avg())
                        .from(memberSub)
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(30, 40);

    }



    @Test
    public void subQueryIn() throws Exception {
        QMember memberSub = new QMember("memberSub");
        List<Member> result = queryFactory
                .selectFrom(member)
                .where(member.age.in(
                        select(memberSub.age)
                                .from(memberSub)
                                .where(memberSub.age.gt(10))
                ))
                .fetch();

        assertThat(result).extracting("age")
                .containsExactly(20, 30, 40);

    }

    @Test
    public void selectSubQuery() throws Exception {

        QMember memberSub = new QMember("memberSub");
        List<Tuple> result = queryFactory
                .select(member.username,
                        select(memberSub.age.avg())
                                .from(memberSub))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

```
- from절의 서브 쿼리 한계
  - JPA JPQL 서브쿼리의 한계점으로 form 절의 서브쿼리는 지원하지 않는다.
  - 당연히 Querydsl도 지원하지 않는다.
  - 하이버네이트 구현체를 사용하면 select 절의 서브쿼리는 지웒나다.
  - querydsl도 하이버네이트 구현체를 사용하면 select절의 서브쿼리를 지원한다.
- from절의 서브 쿼리 해결방안
  - 서브쿼리를 join으로 변경한다. (가능한 상황도 있고 불가능한 상황도 있다)
  - 애플리케이션에서 쿼리를 2번 분리해서 실행한다
  - nativeSQL을 사용한다.
  - 그런데 DB에게 어디까지 일을 시켜야 될 지 한번 고민해봐라
  - 복잡한 서브쿼리가 필요한 경우 그냥 애플리케이션에서 처리하는 것이 더 효율적일 수 있기 때문이다

### Case문

```java

    @Test
    public void basicCase() throws Exception {

        List<String> result = queryFactory
                .select(member.age
                        .when(10).then("열살")
                        .when(20).then("스무살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String s : result) {
            System.out.println("s = " + s);
        }
    }

    @Test
    public void complexCase() throws Exception {

        List<String> res = queryFactory
                .select(new CaseBuilder()
                        .when(member.age.between(0, 20)).then("0~20살")
                        .when(member.age.between(21, 30)).then("21~30살")
                        .otherwise("기타"))
                .from(member)
                .fetch();
        for (String re : res) {
            System.out.println("re = " + re);
        }
    }
```

### 상수, 문자 더하기

```java

    @Test
    public void constant() throws Exception {
        List<Tuple> result = queryFactory
                .select(member.username, Expressions.constant("A"))
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            System.out.println("tuple = " + tuple);
        }
    }

    @Test
    public void concat() throws Exception {
        List<String> result = queryFactory
                .select(member.username.concat("_").concat(member.age.stringValue()))
                .from(member)
                .where(member.username.eq("member1"))
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
```


</div>
</details>


<details>
<summary>Section 04 중급 문법 </summary></summary>
<div markdown="1">



</div>
</details>



<details>
<summary>Section 05 순수 JPA와 Querydsl </summary></summary>
<div markdown="1">

### 프로젝션 결과 반환 - 기본

- 프로젝션 대상이 하나
- 프로젝션 대상이 하나면 타입을 명확하게 지정할 수 있음

```java

    @Test
    public void simpleProjection() throws Exception {
        List<String> result = queryFactory
                .select(member.username)
                .from(member)
                .fetch();

        for (String s : result) {
            System.out.println("s = " + s);
        }
    }
```

- 프로젝션 대상이 둘 이상 
- 튜플 조회

```java


    @Test
    public void tupleProjection() throws Exception {
        List<Tuple> result = queryFactory
                .select(member.username, member.age)
                .from(member)
                .fetch();

        for (Tuple tuple : result) {
            String username = tuple.get(member.username);
            Integer age = tuple.get(member.age);
            System.out.println("username = " + username);
            System.out.println("age = " + age);
        }
    }


```

- 프로젝션과 결과 반환 - DTO 조회 (순수 JPA)
- 순수 JPA에서 DTO로 조회할 때는 new 명령어를 사용해야 함
- DTO의 package이름을 다 적어줘야해서 지저분함
- 생성자 방식만 지원함

```java


    @Test
    public void findDto() throws Exception {
        List<MemberDto> result = em.createQuery("select new study.querydsl.dto.MemberDto(m.username, m.age) from Member m", MemberDto.class)
                .getResultList();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

```

- Querydsl 빈 생성
- 결과를 DTO 반환할 때 사용
- 3가지 방법이 있다.
- 첫번째 프로퍼티 접근 by Setter

```java
    @Test
    public void findDtoBySetter() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.bean(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

```
- 필드 직접 접근

```java

    List<MemberDto> result = queryFactory
        .select(Projections.fields(MemberDto.class,member.username,member.age))
        .from(member)
        .fetch();
```
- 생성자 사용

```java

    @Test
    public void findDtoByConstructor() throws Exception {
        List<MemberDto> result = queryFactory
                .select(Projections.constructor(MemberDto.class,
                        member.username,
                        member.age))
                .from(member)
                .fetch();

        for (MemberDto memberDto : result) {
            System.out.println("memberDto = " + memberDto);
        }
    }

```
</div>
</details>
