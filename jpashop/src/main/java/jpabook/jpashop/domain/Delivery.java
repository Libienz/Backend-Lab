package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Delivery {

    @Id
    @GeneratedValue
    @Column(name = "delivery_id")
    private Long id;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY)
//    @JoinColumn(name = "delivery_id") //연관관계의 주인 : 외래키를 가지고 있다 1:1관계에선 어느쪽에 FK를 둘 것인지 고민이 되는데 자주 조회하는 쪽을 두는 것이 좋다.
    private Order order;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING) //Enum 타입에서 해당 어노테이션 필요 ORDINAL은 정수로 구분, ORDINAL절대 쓰면 안됨 순서에서 밀리거나 장애 발생 가능성 높음
    private DeliveryStatus status; //READY, COMP

}
