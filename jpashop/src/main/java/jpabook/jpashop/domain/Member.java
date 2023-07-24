package jpabook.jpashop.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String name;

    @Embedded //내장 타입
    private Address address;

    @OneToMany(mappedBy = "member") //Member와 order는 일대다 관계, mapped by : 나는 이 연관관계에 주인이 아니라 종속적 컬럼이다. 외래키를 가지고 있지 않다.
    private List<Order> orders = new ArrayList<>();


}
