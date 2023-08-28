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

### 주문 조회 V4
- JPA에서 DTO로 바로 조회

```java

public List<OrderSimpleQueryDto> findOrderDtos() {
      return em.createQuery(
      "select new jpabook.jpashop.repository.OrderSimpleQueryDto(o.id, m.name, o.orderDate, o.orderStatus, d.address)" +
      " from Order o" +
      " join o.member m" +
      " join o.delivery d", OrderSimpleQueryDto.class
      ).getResultList();
}
```
- 일반적인 SQL을 사용할 때 처럼 원하는 값을 선택해서 조회
- new 명령어를 사용해서 JPQL의 결과를 DTO로 즉시 변환!
- SELECT절에서 원하는 데이터를 직접 선택하므로 DB -> 애플리케이션 네트웍 용량 최적화 (생각보다 미비)
- 리포지토리 재사용성 떨어짐, API 스펙에 맞춘 코드가 리포지토리에 들어가는 꼴
- 엔티티를 DTO로 변환하거나(V3) DTO로 바로 조회하는(V4) 두가지 방법은 각각 장단점이 있다.
  - V3: 리포지토리 코드 재사용성 굳. repository가 엔티티만을 조회할 수 있도록 일관성을 준다.
  - V4: 조회성능 미비하게 끌어올린다. (엔티티를 통째로 받아오지 않고 join한 테이블에서 내가 원하는 데이터만 냠냠 가능)

- 잠깐 잠깐 넘어가기 전에 join과 fetch join의 차이점 다시 짚고 가자!
- https://cobbybb.tistory.com/18

</div>
</details>

<details>
<summary>Section 04: API 개발 고급 - 컬렉션 조회 최적화 </summary>
<div markdown="1">

- 주문내역에서 추가로 주문한 상품 정보(OrderItem)를 추가로 조회하자
- Order 기준으로 컬렉션인 OrderItem과 Item이 필요하다.
- 앞의 예제에서는 toOne관계만 있었다. 이번에는 컬렉션인 일대다 관계를 조회하고 최적화 하는 방법을 알아보자

### 주문 조회 V1: 엔티티 직접 노출
```java
    @GetMapping("/api/v1/orders")
    public List<Order> ordersV1() {
        List<Order> all = orderRepository.findAllByString(new OrderSearch());
        // PROXY TOUCH
        for (Order order : all) {
            order.getMember().getName();
            order.getDelivery().getAddress();
            List<OrderItem> orderItems = order.getOrderItems();
            orderItems.stream().forEach(o -> o.getItem().getName());

        }
        return all;
    }
```
- 문제점 
  - 엔티티를 직접 노출 한다.
  - 양방향 연관관계면 무한 루프에 걸리지 않게 한곳에 @JsonIgnore를 추가해야 한다.
  - 프록시를 터치하여 초기화 해야 한다.

### 주문 조회 V2: 엔티티를 DTO로 변환
```java
    @GetMapping("/api/v2/orders")
    public List<OrderDto> ordersV2() {
        List<Order> orders = orderRepository.findAllByString(new OrderSearch());
        List<OrderDto> collect = orders.stream()
                .map(o -> new OrderDto(o))
                .collect((Collectors.toList()));

        return collect;
    }

    @Getter
    static class OrderDto {
        private Long orderId;
        private String name;
        private LocalDateTime orderDate;
        private Address address;
        private List<OrderItemDto> orderItems;

        public OrderDto(Order order) {
            orderId = order.getId();
            name = order.getMember().getName();
            orderDate = order.getOrderDate();
            address = order.getDelivery().getAddress();
            orderItems = order.getOrderItems().stream()
                    .map(orderItem -> new OrderItemDto(orderItem))
                    .collect(Collectors.toList());

        }
    }

    static class OrderItemDto {
        private String itemName;
        private int orderPrice;
        private int count;

        public OrderItemDto(OrderItem orderItem) {
            itemName = orderItem.getItem().getName();
            orderPrice = orderItem.getOrderPrice();
            count = orderItem.getCount();
        }
    }

```
- 문제점 
  - 지연 로딩으로 너무 많은 SQL이 실행된다.
  - SQL 실행 수
    - order 1번 (order 조회 수 N)
    - member, address N번
    - orderItem N번
    - item N번 (orderItem 조회 수 N)


### 주문 조회 V3: 엔티티를 DTO로 변환 - 페치 조인 최적화
```java
    public List<Order> findAllWithItem() {
        return em.createQuery(
                        "select o from Order o" +
                                " join fetch o.member m" +
                                " join fetch o.delivery d" +
                                " join fetch o.orderItems oi" +
                                " join fetch oi.item i", Order.class)
                .getResultList();
    }
```
- 페치 조인으로 SQL이 1번만 실행됨
- distinct를 사용한 이유는 1대다 조인이 있음으로 데이터베이스 row가 증가하기 때문
  - 그 결과 같은 order엔티티의 조회 수도 증가하게 된다. 
  - JPA의 distinct는 SQL에 distinct를 추가하고, 더해서 같은 엔티티가 조회되면, 애플리케이션에서 중복을 걸러준다.
  - 이 예에서 order가 컬렉션 페이 조인 때문에 중복 조회 되는 것을 막아준다.
- 단점 - 페이징 불가능!
  - 컬렉션 페치 조인을 사용하면 페이징이 불가능하다. 하이버네이트는 경고 로그를 남기면서 모든 데이터를 DB에서 읽어오고, 메모리에서 페이징 해버린다
  - 즉 페이징 sql이 작성되지 않는 것
    - Why! 
      - DB에서의 테이블은 distinct를 멕여도 모든 col이 똑같지 않다면 중복을 제거하지 않는다. 
      - 즉 페이징을 하고자 할 때 distinct가 먹지 않은 상태로 들어가기에 싱크가 맞지 않기에 DB에 요청할 수 없는 것 

  - 메모리에 퍼올린다음 애플리케이션레벨에서 페이징이 들어가는 것인데 매우매우 위험하다 (메모리 터질수도)
- 참고 : 컬렉션 페치 조인은 1개만 사용할 수 있다. 컬렉션 둘 이상에 페치 조인을 사용하면 안된다. 데이터가 부정합하게 조회될 수 있다. 

</div>
</details>