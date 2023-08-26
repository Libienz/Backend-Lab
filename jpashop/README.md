## 실전 스프링 부트와 JPA 활용2 - API 개발과 성능 최적화
<details>
<summary>Section 01: API 개발 기본 </summary>
<div markdown="1">

### 회원 등록 API V1
- 회원 등록 API를 다음과 같이 만들어보자
```java
    @PostMapping("api/v1/members") 
    public CreateMemberResponse saveMemberV1(@RequestBody @Valid Member member) {
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    } 
```
- 엔티티를 RequestBody에 직접 매핑했다
  - 문제점 
    - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다. (프로젝션 얼마나 할지!)
    - 엔티티에 API 검증을 위한 로직이 들어간다.(@NotEmpty 등등)
    - 실무에서는 회원 엔티티를 위한 API가 다양하게 만들어지는데, 한 엔티티에 각각의 API를 위한 모든 요구사항을 담기는 어렵다.
    - 엔티티가 변경되면 API 스펙이 변한다.
  - 결론
    - API 요청 스펙에 맞추어 별도의 DTO를 파라미터로 받자!
    - 실무에서는 엔티티를 API 스펙에 노출하지 말자!

### 회원 등록 API V2
- 회원 등록 API를 다음과 같이 만들어보자
```java
    @PostMapping("/api/v2/members")
    public CreateMemberResponse saveMemberV2(@RequestBody @Valid CreateMemberRequest request) {
        Member member = new Member();
        member.setName(request.getName());
        Long id = memberService.join(member);
        return new CreateMemberResponse(id);
    } 
```
- CreateMemberRequest를 Member 엔티티 대신에 RequestBody와 매핑한다.
- 엔티티와 프레젠테이션 계층을 위한 로직을 분리할 수 있다.
- 에닡티와 API 스펙을 명확하게 분리할 수 잇다.
- 엔티티가 변해도 API 스펙이 변하지 않는다.

### 회원 수정 API
- 회원 수정 api를 다음과 같이 만들어보자 
```java
/**
 * 수정 API
 */
@PutMapping("/api/v2/members/{id}")
public UpdateMemberResponse updateMemberV2(
        @PathVariable("id") Long id,
        @RequestBody @Valid UpdateMemberRequest request) {
      memberService.update(id, request.getName());
      Member findMember = memberService.findOne(id);
      return new UpdateMemberResponse(findMember.getId(), findMember.getName());
      } 
```
- 회원 수정도 DTO를 요청 파라미터에 매핑한 것을 확인할 수 있다.
- 다음으로 변경 감지를 이용한 회원 수정 서비스 코드를 살펴보자 
```java
public class MemberService {
  private final MemberRepository memberRepository;
  /**
   * 회원 수정
   */
  @Transactional
  public void update(Long id, String name) {
    Member member = memberRepository.findOne(id);
    member.setName(name);
  }
} 
```
- 명시적인 영속화나 update 쿼리 없이 변경 감지 (Dirty checking)을 통하여 데이터를 수정하고 있다
- 수정은 변경감지를 이용하라고 했다!

### 회원 조회 API V1
- 회원조회 API V1을 살펴보자
```java
 @GetMapping("/api/v1/members")
 public List<Member> membersV1() {
    return memberService.findMembers();
 }
```
- 위와 같이 naive하게 하면 다음의 문제점들이 있따.
  - 엔티티에 프레젠테이션 계층을 위한 로직이 추가된다.
  - 기본적으로 엔티티의 모든 값이 노출된다.
  - 응답 스펙을 맞추기 위한 로직이 추가된다(@JsonIgnore, 별도의 뷰 로직 등등)
  - 실무에서는 같은 엔티티에 대해 API가 용도에 따라 다양하게 만들어지는데 하나의 엔티티는 이 모든 것을 감당할 수 없다.
  - 컬렉션을 직접 반환하면 향후 API 스펙을 변경하기 어렵다.

### 회원 조회 V2: 응답 값으로 엔티티가 아닌 별도의 DTO 사용
- 회원 조회 V2를 살펴보자 
```java
/**
 * 조회 V2: 응답 값으로 엔티티가 아닌 별도의 DTO를 반환한다.
 */
@GetMapping("/api/v2/members")
public Result membersV2() {
      List<Member> findMembers = memberService.findMembers();
      //엔티티 -> DTO 변환
      List<MemberDto> collect = findMembers.stream()
          .map(m -> new MemberDto(m.getName()))
          .collect(Collectors.toList());
      return new Result(collect);
}
@Data
@AllArgsConstructor
static class Result<T> {
  private T data;
}
@Data
@AllArgsConstructor
static class MemberDto {
  private String name;
}
```
- 엔티티를 DTO로 변환해서 반환한다. 
- 엔티티가 변해도 API 스펙이 변경되지 않는다.
- 추가로 Result 클래스로 컬렉션을 감싸서 향후 필요한 필드를 추가할 수 있다.(count라던지 이런 것들 무조건 추가된다! 유지 보수를 위해서는 감싸라)


</div>
</details>

<details>
<summary>Section 02: API 개발 고급 - 준비 </summary>
<div markdown="1">

### 샘플 데이터 입력
```java
package jpabook.jpashop;

import jakarta.annotation.PostConstruct;
import jakarta.persistence.EntityManager;
import jpabook.jpashop.domain.*;
import jpabook.jpashop.domain.item.Book;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;


@Component
@RequiredArgsConstructor
public class InitDb {

    private final InitService initService;

    @PostConstruct
    public void init() {
        initService.dbInit1();
    }
    @Component
    @Transactional
    @RequiredArgsConstructor
    static class InitService {
        private final EntityManager em;
        public void dbInit1() {
            Member member = createMember("userA", "서울", "1", "1111");
            em.persist(member);

            Book book1 = createBook("JPA1 BOOK", 10000, 100);
            em.persist(book1);

            Book book2 = createBook("JPA2 BOOK", 20000, 100);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 10000, 1);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 20000, 2);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }

        public void dbInit2() {
            Member member = createMember("userB", "부산", "2", "2222");
            em.persist(member);

            Book book1 = createBook("SPRING1 BOOK", 20000, 200);
            em.persist(book1);

            Book book2 = createBook("SPRING2 BOOK", 40000, 300);
            em.persist(book2);

            OrderItem orderItem1 = OrderItem.createOrderItem(book1, 20000, 3);
            OrderItem orderItem2 = OrderItem.createOrderItem(book2, 40000, 4);

            Delivery delivery = createDelivery(member);
            Order order = Order.createOrder(member, delivery, orderItem1, orderItem2);
            em.persist(order);
        }
        private static Book createBook(String name, int price, int stockQuantity) {
            Book book1 = new Book();
            book1.setName(name);
            book1.setPrice(price);
            book1.setStockQuantity(stockQuantity);
            return book1;
        }
        private Member createMember(String name, String city, String street, String zipcode) {
            Member member = new Member();
            member.setName(name);
            member.setAddress(new Address(city, street, zipcode));
            return member;
        }
        private static Delivery createDelivery(Member member) {
            Delivery delivery = new Delivery();
            delivery.setAddress(member.getAddress());
            return delivery;
        }
    }


}

```

</div>
</details>


<details>
<summary>Section 03: API 개발 고급 - 지연 로딩과 조회 성능 최적화</summary>
<div markdown="1">

### 주문 조회 V1
- 간단한 주문 조회를 살펴보자
```java
 @GetMapping("/api/v1/simple-orders")
 public List<Order> ordersV1() {
 List<Order> all = orderRepository.findAllByString(new OrderSearch());
   for (Order order : all) {
     order.getMember().getName(); //Lazy 강제 초기화
     order.getDelivery().getAddress(); //Lazy 강제 초기환
   }
   return all;
 }
```
- 문제점들
  - 엔티티를 직접 노출하는 것은 좋지 않다.
  - order -> member 와 order -> address는 지연 로딩! 따라서 실제 엔티티 대신에 프록시가 존재
  - jackson 라이브러리는 기본적으로 이 프록시 객체를 json으로 어떻게 생성해야 하는 지 모름 -> 예외 발생
  - Hibernate5Module을 스프링 빈으로 등록하면 프록시 무제를 해결할 수는 있음
  - 양방향 연관관계에서 한곳을 @JsonIgnore처리하지 않으면 양쪽을 서로 호출하면서 무한 루프가 걸릴 수 있다

- 주의! 
  - 지연로딩을 피하기 위해 즉시 로딩으로 설정하면 안된다. 
  - 즉시 로딩으로 설정하면 성능 튜닝을 어렵게 한다
  - 연관관계가 필요 없는 경우에도 항상 조회하기에 성능 문제가 발생할 수 있는 것. 
  - 항상 지연 로딩을 기본으로 하고, 성능 최적화가 필요한 경우에는 페치 조인을 사용하시오

### 주문 조회 V2
- 엔티티를 DTO로 변환!

```java
/**
 * V2. 엔티티를 조회해서 DTO로 변환(fetch join 사용X)
 * - 단점: 지연로딩으로 쿼리 N번 호출
 */
@GetMapping("/api/v2/simple-orders")
public List<SimpleOrderDto> ordersV2() {
  List<Order> orders = orderRepository.findAll();
  List<SimpleOrderDto> result = orders.stream()
    .map(o -> new SimpleOrderDto(o))
    .collect(toList());
  return result;
}
```
- 엔티티를 DTO로 변환하는 일반적인 방법이다.
- 여기에서 List를 한 꺼풀 더 씌우면 더 좋은 설계 (해당 예제에선 생략)
- 문제점
  - 쿼리가 총 1 + N + N번 실행된다.
  - Order 조회 1번 (order 조회 결과 row 수가 N이 된다.)
  - order -> member 지연 로딩 조회 N번
  - order -> delivery 지연 로딩 조회 N번
  - 예) order의 결과가 4개면 최악의 경우 1 + 4 + 4번 실행된다 (최악의 경우)
    - 지연로딩은 영속성 컨텍스트에서 조회함으로 이미 조회된 경우 쿼리를 생략한다
    - 따라서 위에서 최악의 경우라고 명시한 것!

### 주문 조회 V3
- 페치 조인 최적화

```java
public List<Order> findAllWithMemberDelivery() {
      return em.createQuery(
      "select o from Order o" +
      " join fetch o.member m" +
      " join fetch o.delivery d", Order.class
      ).getResultList();
}
```
- 엔티티를 페치 조인을 사용해서 쿼리 1번에 조회
- 페치 조인으로 order -> member, order -> delivery는 이미 조회된 상태임으로 지연로딩 X

</div>
</details>