## 실전 스프링 데이터 JPA

<details>
<summary>Section 02 예제 도메인 모델</summary></summary>
<div markdown="1">

### 예제 도메인 모델
- ![img.png](img.png)
- ![img_1.png](img_1.png)

</div>
</details>


<details>
<summary>Section 03 공통 인터페이스 기능</summary></summary>
<div markdown="1">

### 순수 jpa 기반 리포지토리를 살펴보자
- 회원 기본 CRUD

```java
package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Member;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public class MemberJpaRepository {

    @PersistenceContext
    private EntityManager em;

    public Member save(Member member) {
        em.persist(member);
        return member;
    }

    public void delete(Member member) {
        em.remove(member);
    }

    public List<Member> findAll() {
        return em.createQuery("select m from Member m", Member.class)
                .getResultList();
    }

    public Optional<Member> findById(Long id) {
        Member member = em.find(Member.class, id);
        return Optional.ofNullable(member);
    }

    public long count() {
        return em.createQuery("select count(m) from Member m", Long.class).getSingleResult();
    }
    public Member find(Long id) {
        return em.find(Member.class, id);
    }
}

```

- Team 기본 CRUD

```java
package study.datajpa.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import study.datajpa.entity.Team;

import java.util.List;
import java.util.Optional;

@Repository
public class TeamRepository {

    @PersistenceContext
    private EntityManager em;

    public Team save(Team team) {
        em.persist(team);
        return team;
    }

    public void delete(Team team) {
        em.remove(team);
    }

    public List<Team> findAll() {
        return em.createQuery("select t from Team t", Team.class)
                .getResultList();
    }

    public Optional<Team> findById(Long id) {
        Team team = em.find(Team.class, id);
        return Optional.ofNullable(team);
    }

    public long count() {
        return em.createQuery("select count(t) from Team t", Long.class)
                .getSingleResult();
    }

}

```
- CRUD가 반복적으로 진행되고 있는 것을 알 수 있다.
- 제네릭을 사용하면 재사용성을 늘릴 수 있을지도!?
- Spring 데이터 jpa에서는 공통 인터페이스를 통해서 boilerplate한 코드를 삭제할 수 있도록 도와준다.

### Spring Data JPA 공통 인터페이스 

- Spring Data JPA를 사용하면 인터페이스를 사용하는 것만으로 CRUD 공통 인터페이스를 사용가능 하다. 어떻게 그것이 가능할까!?
- 실제로 인터페이스를 사용할 때 class를 찍어보자
  - memberRepository.getClass() class com.sun.proxy.$ProxyXXX
- 스프링 jpa가 구현체를 생성하고 프록시로 제공하는 것을 확인할 수 있다

### Spring Data JPA 공통 인터페이스 적용
- 공통 인터페이스를 적용해보자

```java
    @Test
    public void basicCRUD() throws Exception {

        //given
        Member member1 = new Member("member1");
        Member member2 = new Member("member2");
        memberRepository.save(member1);
        memberRepository.save(member2);
        //단건 조회 검증
        Member findMember1 = memberRepository.findById(member1.getId()).get();
        Member findMember2 = memberRepository.findById(member2.getId()).get();
        assertThat(findMember1).isEqualTo(member1);
        assertThat(findMember2).isEqualTo(member2);

        //리스트 조회 검증
        List<Member> all = memberRepository.findAll();
        assertThat(all.size()).isEqualTo(2);

        //count 검증
        long count = memberRepository.count();
        assertThat(count).isEqualTo(2);

        //삭제 검증
        memberRepository.delete(member1);
        memberRepository.delete(member2);
        long deletedCount = memberRepository.count();
        assertThat(deletedCount).isEqualTo(0);

    }

```
- Spring Data JPA로 똑같이 적용을 해보아도 이미 구현되어 있는 기능들이라 별 다른 수정 없이 사용 가능한 것을 확인할 수 있다.

### 공통 인터페이스 분석
- ![img_2.png](img_2.png)


</div>
</details>


<details>
<summary>Section 04 Query Method 기능</summary></summary>
<div markdown="1">

### 스프링 데이터 JPA가 제공하는 마법 같은 기능
- 메서드 이름으로 쿼리 생성
- 메서드 이름으로 JPA Named Query 호출
- @Query 어노테이션을 사용해서 리파지토리 인터페이스에 쿼리 직접 정의

### 메서드 이름으로 쿼리 생성
- 메서드 이름을 분석해서 JPQL 쿼리가 작성되고 실행된다.
- 이름과 나이를 기준으로 회원을 조회하는 다음의 순수 JPA 리포지토리 코드를 보자

```java

    public List<Member> findByUsernameAndAgeGreaterThan(String username, int age) {
        return em.createQuery("select m from Member m where m.username = :username and m.age > :age")
                .setParameter("username", username)
                .setParameter("age", age)
                .getResultList();
    }
```
- 쿼리를 작성하고 실행시키도록 코드를 짠 것을 볼 수 있다 
- 다음으로 스프링 데이터 jpa에서 같은 기능을 구현한 코드를 보자

```java
   List<Member> findByUsernameAndAgeGreaterThan(String username, int age);

```
- 끝이다.. 이름만 규약에 맞게 원형을 인터페이스에 정의하면 스프링 데이터 jpa가 메서드 이름에 맞는 쿼리를 작성하고 실행하도록 해주는 것
- 스프링 데이터 jpa는 공통 인터페이스를 구현해주는 장점도 있지만 이것처럼 특정 도메인 종속적인 기능도 얼마든지 이용할 수 있는 것이다.

### 쿼리 메서드 필터 조건
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#jpa.query-methods.query-creation

### 스프링 데이터가 제공하는 쿼리 메서드 기능
- 조회: find...By, read...By, query...By, get...By 
- https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repositories.query-methods.query-creation
- 예) findHelloBy처럼 ...에 식별하기 위한 내용이 들어가도 된다. 
- COUNT: count...By 반환타입 long
- EXISTS: exists…By 반환타입 boolean
- 삭제: delete…By, remove…By 반환타입 long
- DISTINCT: findDistinct, findMemberDistinctBy
- LIMIT: findFirst3, findFirst, findTop, findTop3

### JPA NamedQuery
- JPA의 NamedQuery를 스프링 데이터 JPA에서 호출 할 수 있음
- 먼저 순수 JPA의 Named query 사용 모습
- 엔티티에 Named 쿼리 작성
```java
@NamedQuery(
        name="Member.findByUsername",
        query="select m from Member m where m.username = :username"
)
public class Member {
  ...
}
```
- JPA를 직접 사용해서 Named 쿼리 호출
```java

    public List<Member> findByUsername(String username) {
                return em.createNamedQuery("Member.findByUsername", Member.class)
                        .setParameter("username", username)
                        .getResultList();
    }
```
- Named 쿼리를 이용해 쿼리에 이름을 부여, 재사용성을 높였다. 
- 또한 Named 쿼리가 정적 쿼리라는 특성을 이용 컴파일 타임에 쿼리의 정합성을 체크할 수 있도록 했다 (중요한 장점)
- 이렇게 정의된 named 쿼리는 스프링 데이터 jpa에서 특정 메서드에서 실행될 쿼리로 설정할 수 있다
- 스프링 데이터 jpa로 named 쿼리 호출
```java
@Query(name = "Member.findByUsername")
List<Member> findByUsername(@Param("username") String username);

```
- @Query를 통해 named 쿼리를 지정해주는 모습이다
- 사실 @Query어노테이션이 없어도 named 쿼리가 실행된다. 그 이유는 스프링 데이터 jpa에서는 findByUsername이라는 메서드를 실행할 때 먼저 Member.findByUsername이라는 named 쿼리를 찾아보기 때문
- namedQuery가 존재하지 않는다면 메서드 이름으로 쿼리가 생성될 것이기 때문에 사실 @Query가 필요 없는 것이다.
- 여쨋든 named 쿼리를 이용하면 쿼리에 이름을 부여해 재사용성을 높임과 동시에 정적 컴파일이 가능하다는 큰 장점이 있고 스프링 데이터 jpa에서도 사용할 수 있는 것을 확인했다
- 그럼에도 불구하고 실무에선 namedQuery를 직접 등록해서 사용하는 일은 드물다.
- 쿼리를 엔티티단에서 정의해야 하는 것도 그렇고 관심사의 분리가 명확하지 않은 것 때문이다.
- 그렇다면 주로 사용되는 방법은 무엇이냐?
- 바로 다음에 공부할 @Query를 사용해서 리파지토리 메서드에 쿼리를 직접 정의하는 것은 namedQuery의 장점을 모두 가지면서 NamedQuery의 단점이 없기에 자주 사용된다.

### @Query, 리포지토리 메서드에 쿼리 정의하기

```java

public interface MemberRepository extends JpaRepository<Member, Long> {
  @Query("select m from Member m where m.username= :username and m.age = :age")
  List<Member> findUser(@Param("username") String username, @Param("age") int age);
}

```
- 실행할 메서드에 정적 쿼리를 직접 작성함으로 이름 없는 Named 쿼리를 적용하는 것과 같음
- 정적 쿼리이기에 Named 쿼리처럼 어플리케이션 실행시점에 문법 오류를 발견할 수 있음
- 실무에서는 메서드 이름으로 쿼리 생성 기능은 파라미터가 증가하면 메서드 이름이 매우 지저분해지기에 리포지토리 메서드에 쿼리를 직접 정의하는 해당방법을 가장 많이 사용한다.

### @Query, 값, DTO 조회하기 

### 단순히 값 하나를 조회

```java
  @Query("select m.username from Member m")
  List<String> findUsernameList();
```
- JPA 값 타입(@Embedded)도 이 방식으로 조회 가능하다.

### DTO로 직접 조회

```java
@Query("select new study.datajpa.dto.MemberDto(m.id, m.username, t.name) " + "from Member m join m.team t")
List<MemberDto> findMemberDto();
```

- 주의! DTO로 직접 조회 하려면 JPA의 new 명령어를 사용해야 한다. 그리고 다음과 같이 생성자가 맞는 DTO가 필요하다.

### 파라미터 바인딩
- 위치 기반과 이름 기반중 이름 기반을 사용하자 (가독성과 유지 보수를 위해)

```java
import org.springframework.data.repository.query.Param
public interface MemberRepository extends JpaRepository<Member, Long> {
   @Query("select m from Member m where m.username = :name")
   Member findMembers(@Param("name") String username);
}
```

### 반환 타입

- 스프링 데이터 JPA는 유연한 반환 타입을 지원한다.
```java

List<Member> findListByUsername(String username);
Member findMemberByUsername(String username);
Optional<Member> findOptionalByUsername(String username);
```
- 스프링 데이터 JPA 공식 문서: https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#repository-query-return-types
- 조회 결과가 많거나 없으면?
  - 컬렉션 
    - 결과 없음: 빈 컬렉션 반환
  - 단건 조회
    - 결과 없음: null 반환
    - 결과가 2건 이상: NonUniqueResultException 예외 발생
- 참고 : 단건으로 지정한 메서드를 호출하면 스프링 데이터 JPA는 내부에서 JPQL의 메서드를 호출한다. 
  - 이 메서드를 초출했을 때 조회 결과가 없으면 NoResultException 예외가 발생하는데 개발자 입장에서 다루기가 상당히 불편
  - 스프링 데이터 JPA는 단건을 조회할 때 이 예외를 한번 감싸서 null을 반환하도록 구현되어 있다.

### 순수 JPA 페이징과 정렬
- JPA에서 페이징을 어떻게 할 것인가?
- 다음 조건으로 페이징과 정렬을 사용하는 예제 코드를 보자
- 검색 조건: 나이가 10살
- 정렬 조건: 이름으로 내림차순
- 페이징 조건: 첫 번째 페이지, 페이지당 보여줄 데이터는 3건

```java

    public List<Member> findByPage(int age, int offset, int limit) {
        return em.createQuery("select m from Member m where m.age =: age order by m.username desc")
                .setParameter("age", age)
                .setFirstResult(offset)
                .setMaxResults(limit)
                .getResultList();
    }

    public long totalCount(int age) {
        return em.createQuery("select count(m) from Member m where m.age =: age", Long.class)
                .setParameter("age", age)
                .getSingleResult();
    }
```

### 스프링 데이터 JPA 페이징과 정렬
- 원래 페이징과 정렬은 고대의 선배들이 진행하던 규약같은 것이 있었는데 상당히 복잡하고 DB마다 그 형태가 다르다.
- 스프링 데이터 JPA는 이러한 페이징과 정렬을 표준화 하여 구현을 쉽게 한다. 다음을 살펴보자
- 페이징과 정렬 파라미터
  - org.springframework.data.domain.Sort : 정렬 기능
  - org.springframework.data.domain.Pageable : 페이징 기능 (내부에 Sort 포함)
  - Sort와 Pageable 인터페이스 두개로 표준화 시킨 것이다.
- 특별한 반환 타입
  - org.springframework.data.domain.Page : 추가 count 쿼리 결과를 포함하는 페이징
  - org.springframework.data.domain.Slice : 추가 count 쿼리 없이 다음 페이지만 확인 가능(내부적 으로 limit + 1조회)
  - List (자바 컬렉션): 추가 count 쿼리 없이 결과만 반환

### 페이징과 정렬을 사용하는 예제 코드
- 검색 조건: 나이가 10살
- 정렬 조건: 이름으로 내림차 순
- 페이징 조건: 첫 번째 페이지, 페이지당 보여줄 데이터는 3건

#### Page 사용 예제 정의 코드 
```java
public interface MemberRepository extends Repository<Member, Long> {
   Page<Member> findByAge(int age, Pageable pageable);
}
```

#### Page 사용 예제 실행 코드

```java 
@Test
public void page() throws Exception {
     //given
     memberRepository.save(new Member("member1", 10));
     memberRepository.save(new Member("member2", 10));
     memberRepository.save(new Member("member3", 10));
     memberRepository.save(new Member("member4", 10));
     memberRepository.save(new Member("member5", 10));
     //when
     PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "username"));
     Page<Member> page = memberRepository.findByAge(10, pageRequest);
     //then
     List<Member> content = page.getContent(); //조회된 데이터
     assertThat(content.size()).isEqualTo(3); //조회된 데이터 수
     assertThat(page.getTotalElements()).isEqualTo(5); //전체 데이터 수
     assertThat(page.getNumber()).isEqualTo(0); //페이지 번호
     assertThat(page.getTotalPages()).isEqualTo(2); //전체 페이지 번호
     assertThat(page.isFirst()).isTrue(); //첫번째 항목인가?
     assertThat(page.hasNext()).isTrue(); //다음 페이지가 있는가?
}
```

- 두 번째 파라미터로 받은 Pageable은 인터페이스다. 따라서 실제 사용할 때는 해당 인터페이스를 구현한 PageRequest 객체를 사용한다.
- PageRequest 생성자의 첫 번째 파라미터에는 현재 페이지를, 두 번째 파라미터에는 조회할 데이터 수를 입력한다.
- 여기에 추가로 정렬 정보도 파라미터로 사용할 수 있다. 참고로 페이지는 0부터 시작한다.
- 참고 countQuery는 join과 함께 사용될 필요가 없다 괜히 필요없는 장신구를 단 countQuery가 나갈 수 있다는 뜻
- count쿼리는 그래서 다음과 같이 분리 할 수 있다.

```java
@Query(value = "select m from Member m", countQuery = "select count(m.username) from Member m")
Page<Member> findMemberAllCountBy(Pageable pageable); 
```

- 페이지를 유지하면서 엔티티를 DTO로 변환하기
```java
Page<Member> page = memberRepository.findByAge(10, pageRequest);
Page<MemberDto> dtoPage = page.map(m -> new MemberDto());
```

### 벌크성 수정 쿼리
- JPA를 사용한 벌크성 수정 쿼리

```java
public int bulkAgePlus(int age) {
   int resultCount = em.createQuery(
   "update Member m set m.age = m.age + 1" +
   "where m.age >= :age")
   .setParameter("age", age)
   .executeUpdate();
   return resultCount;
}
```

- 스프링 데이터 JPA를 사용한 벌크성 수정 쿼리

```java
@Modifying
@Query("update Member m set m.age = m.age + 1 where m.age >= :age")
int bulkAgePlus(@Param("age") int age);
```

- 벌크성 수정, 삭제 쿼리는 @Modifying 어노테이션을 사용하자
  - 사용하지 않으면 예외가 터진다. 
- 벌크성 쿼리를 실행하고 나서는 꼭 영속성 컨텍스트를 초기화하자
  - 벌크성 쿼리를 실행하고 나서 영속성 컨텍스트를 초기화 하지 않으면 영속성 컨텍스트에 과거 값이 남아서 문제가 될 수 있다.
  - @Modifying(clearAutomatically)을 통해 영속성 컨텍스트를 초기화 하자

### EntityGraph
- 연관된 엔티티들ㅇ르 SQL 한번에 조회하는 방법
- member -> team은 지연로딩 관계이다. 따라서 N+1문제가 있다
- 연관된 엔티티를 한번에 조회하려면 페치 조인이 필요하다
```java
@Query("select m from Member m left join fetch m.team")
List<Member> findMemberFetchJoin();
```
- 스프링 데이터 JPA는 JPA가 제공하는 엔티티 그래프 기능을 편리하게 사용하게 도와준다. 
- 이기능을 사용하면 JPQL없이 페치 조인을 사용할 수 있다. 

```java
//공통 메서드 오버라이드
@Override
@EntityGraph(attributePaths = {"team"})
List<Member> findAll();
//JPQL + 엔티티 그래프
@EntityGraph(attributePaths = {"team"})
@Query("select m from Member m")
List<Member> findMemberEntityGraph();
//메서드 이름으로 쿼리에서 특히 편리하다.
@EntityGraph(attributePaths = {"team"})
List<Member> findByUsername(String username)
```
- 엔티티 그래프는 사실상 페치 조인의 간편 버전

### JPA Hint & Lock
- JPA 쿼리 힌트: SQL 힌트가 아님 JPA 구현체 하이버네이트에게 제공하는 힌트
- 주로 변경 감지를 위한 스냅샷 관리를 막기 위한 용도로 사용된다.

```java
@QueryHints(value = @QueryHint(name = "org.hibernate.readOnly", value =
"true"))
Member findReadOnlyByUsername(String username);
```
- Lock: JPA가 제공하는 Lock 사용법 

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
List<Member> findByUsername(String name);
```
</div>
</details>


<details>
<summary>Section 05 확장 기능</summary></summary>
<div markdown="1">

### 사용자 정의 리포지토리 구현
- 스프링 데이터 JPA 리포지토리는 인터페이스만 정의하고 구현체는 스프링이 자동 생성
- 스프링 데이터 JPA가 제공하는 인터페이스를 직접 구현하면 구현해야 하는 기능이 너무 많음
- 다양한 이유로 인터페이스의 메서드를 직접 구현하고 싶다면?
  - JPA 직접 사용
  - 스프링 JDBC Template 사용
  - MyBatis 사용 등등
- 사용자 정의 인터페이스를 만들고

```java
  public interface MemberRepositoryCustom {
      List<Member> findMemberCustom();
  } 
```
- 사용자 정의 인터페이스 구현 클래스를 만든 다음
```java
@RequiredArgsConstructor
public class MemberRepositoryImpl implements MemberRepositoryCustom {
  private final EntityManager em;
  @Override
  public List<Member> findMemberCustom() {
    return em.createQuery("select m from Member m")
            .getResultList();
  }
}
```
- 사용자 정의 인터페이스를 상속하고
```java
public interface MemberRepository
        extends JpaRepository<Member, Long>, MemberRepositoryCustom {
}
```
- 사용자 정의 메서드를 호출하자!
```java
List<Member> result = memberRepository.findMemberCustom(); 
```

- 사용자 정의 구현 클래스 규칙
  - 리포지토리 인터페이스 이름 + Impl
  - 스프링 데이터 JPA가 인식해서 스프링 빈으로 등록


### Auditing
- 엔티티를 생성, 변경할 때 변경한 사람과 시간을 추적하고 싶으면?
  - 등록일
  - 수정일
  - 등록자
  - 수정자

- 순수 JPA 사용
```java
package study.datajpa.entity;
@MappedSuperclass
@Getter
public class JpaBaseEntity {
 @Column(updatable = false)
 private LocalDateTime createdDate;
 private LocalDateTime updatedDate;
 @PrePersist
 public void prePersist() {
 LocalDateTime now = LocalDateTime.now();
 createdDate = now;
 updatedDate = now;
 }
 @PreUpdate
 public void preUpdate() {
 updatedDate = LocalDateTime.now();
 }
}
//public class Member extends JpaBaseEntity {}
```
- 스프링 데이터 JPA 사용
  - 설정
    - @EnableJpaAuditing 스프링 부트 설정 클래스에 적용
    - @EntityListeners(AuditingEntityListener.class) -> 엔티티에 적용
  - 사용 어노테이션
    - @CreatedDate
    - @LastModifiedDate
    - @CreatedBy
    - @LastModifiedBy

### Web 확장 - 도메인 클래스 컨버터
- HTTP 파라미터로 넘어온 엔티티의 아이디로 엔티티 객체를 찾아서 바인딩
```java
@RestController
@RequiredArgsConstructor
public class MemberController {
  private final MemberRepository memberRepository;
  @GetMapping("/members/{id}")
  public String findMember(@PathVariable("id") Member member) {
    return member.getUsername();
  }
} 
```
- HTTP 요청은 회원 id를 받지만 도메인 클래스 컨버터가 중간에 동작해서 회원 엔티티 객체를 받아온다.
- 도메인 클래스 컨버터도 Repository를 사용해서 엔티티를 찾는다.
- 주의: 도메인 클래스 컨버터로 엔티티를 파라미터로 받으면, 이 엔티티는 단순 조회용으로만 사용해야 한다.
  - 트랜잭션이 없는 범위에서 엔티티를 조회했음으로 엔티티를 변경해도 DB에 반영되지 않는다.

### Web 확장 - 페이징과 정렬
- 스프링 데이터가 제공하는 페이징과 정렬 기능을 스프링 MVC에서 편리하게 사용할 수 있다.
- 페이징과 정렬 예제
```java
 @GetMapping("/members")
public Page<Member> list(Pageable pageable) {
        Page<Member> page = memberRepository.findAll(pageable);
        return page;
}
```
- 파라미터로 pageable을 받을 수 있다. 
-  /members?page=0&size=3&sort=id,desc&sort=username,desc
  - 위와 같이 요청을 날리면 PageRequest 객체가 생성되어 리퍼지토리 메서드에 넘어가게 된다
  - page: 현재 페이지. 0부터 시작한다.
  - size: 한 페이지에 노출할 데이터 건수
  - sort: 정렬 조건 정의
- 기본값
- 글로벌 설정: 스프링 부트 
```properties
spring.data.web.pageable.default-page-size=20 /# 기본 페이지 사이즈/
spring.data.web.pageable.max-page-size=2000 /# 최대 페이지 사이즈/
```
- 개별설정

```java
@RequestMapping(value = "/members_page", method = RequestMethod.GET)
public String list(@PageableDefault(size = 12, sort = "username", direction = Sort.Direction.DESC) Pageable pageable) {
        ...
}
 
```

- 접두사
  - 페이징 정보가 둘 이상이면 접두사로 구분
  - @Qualifier에 접두사 명 추가할 것
  - ex) /members?member_page=0&order_page=1

```java
public String list(
@Qualifier("member") Pageable memberPageable,
@Qualifier("order") Pageable orderPageable, ...
```

- Page 내용을 DTO로 변환하기
  - 엔티티를 API로 노출하면 다양한 문제가 발생한다. 그래서 엔티티를 꼭 DTO로 변환해서 반환해야 한다.
  - Page는 map()을 지원해서 내부 데이터를 다른 것으로 변경할 수 잇따.

```java
@GetMapping("/members")
public Page<MemberDto> list(Pageable pageable) {
   Page<Member> page = memberRepository.findAll(pageable);
   Page<MemberDto> pageDto = page.map(MemberDto::new);
   return pageDto;
}
//최적화
@GetMapping("/members")
public Page<MemberDto> list(Pageable pageable) {
    return memberRepository.findAll(pageable).map(MemberDto::new);
}
```

- Page를 1부터 시작하기
  - 스프링 데이터는 Page를 0부터 시작한다.
  - 만약 1부터 시작하고 싶다면?
    - 직접 클래스를 만들어서 처리하든가
    -  spring.data.web.pageable.one-indexed-parameters 를 true 로 설정해라
      - 그런데 이 방법은 web에서 page파라미터를 -1 처리할 뿐임으로 응답값인 Page에 모두 0 페이지 인덱스를 사용하는 한계가 있다.



</div>
</details>


<details>
<summary>Section 06 스프링 데이터 JPA 분석</summary></summary>
<div markdown="1">


</div>
</details>

### 스프링 데이터 JPA 분석
```java

//SimpleJpaRepository
@Repository
@Transactional(readOnly = true)
public class SimpleJpaRepository<T, ID> ...{
@Transactional
public <S extends T> S save(S entity) {
        if (entityInformation.isNew(entity)) {
          em.persist(entity);
          return entity;
        } else {
            return em.merge(entity);
        }
    }
    ...
}

```
- @Repository 적용: JPA 예외를 스프링이 추상화한 예외로 변환
- @Transactional 트랜잭션 적용 (구현체에 정의되어 있다)
  - JPA의 모든 변경은 트랜잭션 안에서 동작
  - 스프링 데이터 JPA는 변경(등록, 수정, 삭제)메서드를 트랜잭션 처리
  - 서비스 계층에서 트랜잭션을 시작하지 않으면 리파지토리에서 트랜잭션 시작
  - 서비스 계층에서 트랜잭션을 시작하면 리파지토리는 해당 트랜잭션을 전파 받아서 사용
  - 그래서 스프링 데이터 JPA를 사용할 때 트랜잭션이 없어도 데이터 등록, 변경이 가능했던 것
- @Transavtional(readOnly = true)
  - 데이터를 단순히 조회만 하고 변경하지 않는 트랜잭션에서 readOnly = true 옵션을 사용하면 플러시를 생략한다.
  - 약간의 성능향상을 얻을 수 있음

### save()메서드의 동작
- 새로운 엔티티면 저장(persist)
- 새로운 엔티티가 아니면 병합(merge)
  - 머지는 데이터를 통째로 갈아끼운다.
  - 업데이트는 부분변경을 하게되는 경우가 많은데 쓰지 않은 필드가 전부 null이 될 위험성이 있음
  - jpa에서 변경감지를 권장하는 이유이다.
- 새로운 엔티티임을 어떻게 판단하는가?
  - 식별자가 객체일 때 null로 판단.
  - 식별자가 primitive 타입일 때 0으로 판단
  - Persistable 인터페이스를 구현해서 판단 로직 변경 가능
  - @GeneratedValue를 사용하지 않고 id를 초기화하면 save메서드에선 이미 존재하는 놈이라고 판단하고 DB에 쿼리를 날려 객체를 통째로 바꿔끼울 준비를 한다
  - DB에서 조회가 안되고 나서야 새로운 객체임을 다시 뒤늦게 알아차리고 객체를 영속화 한다. (불필요한 쿼리 발생)

```java
package study.datajpa.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Item implements Persistable<String> {

    @Id
    private String id;

    @CreatedDate
    private LocalDateTime createdDate;

    @Override
    public boolean isNew() {
        return createdDate == null;
    }
}

```

- 참고
  - JPA 식별자 생성 전략이 @GeneratedValue면 save 호출 시점에 식별자가 없음으로 새로운 엔티티로 인식, 정상 동작
  - 그런데 JPA 식별자 생성 전략이 @Id만 사용해서 직접 할당이면 이미 식별자 값이 있는 상태로 save() 호출 
  - 따라서 이 경우 merge() 호출
  - merge()는 우선 DB를 호출해서 값을 확인하고 DB에 값이 없으면 새로운 엔티티로 인지함으로 매우 비효율적
  - 따라서 Persistable를 사용해서 새로운 엔티티 확인 여부를 직접 구현하는 게 효과적이다.
  - 참고로 등록시간(@CreatedDate)을 조합해서 사용하면 이 필드로 새로운 엔티티 여부를 편리하게 확인할 수 있다 

<details>
<summary>Section 07</summary></summary>
<div markdown="1">


</div>
</details>
