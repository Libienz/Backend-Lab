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
</div>
</details>