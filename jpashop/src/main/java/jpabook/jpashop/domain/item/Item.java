package jpabook.jpashop.domain.item;

import jakarta.persistence.*;
import jpabook.jpashop.domain.Category;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE) //상속관계 전략 필요 여기에선 부모 엔티티와 자식 엔티티들을 모두 동일한 테이블에 저장한다. (모든 속성 때려넣고 각 컬럼은 구분자로 구분)
@DiscriminatorColumn(name = "dtype") //부모와 자식을 한테이블에 넣기 위한 구분자
@Getter
@Setter
public abstract class Item {

    @Id
    @GeneratedValue
    @Column(name = "item_id")
    private Long id;

    private String name;
    private int price;
    private int stock;

    @ManyToMany(mappedBy = "items")
    private List<Category> categories = new ArrayList<Category>();
}
