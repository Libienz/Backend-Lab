package jpabook.jpashop.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Embeddable: 객체를 값으로써 데이터베이스에 내장 가능하도록 하는 Annotation
 *               중간 테이블을 생성하지 않고 엔티티와 함께 영속화 됨으로 엔티티와 함께 로드되거나 삭제됨 (값으로써 내장 시켜버리는 것)
 *               만약 해당 어노테이션이 붙지 않은 객체를 엔티티의 속성으로 매핑할 때에는 별도의 엔티티로 간주되며 별도의 테이블로 매핑된다.
 *               값이라는 것은 immutable. Setter를 제공하지 않는 것이 원칙
 */
@Embeddable
@Getter
@AllArgsConstructor
public class Address {

    //jpa 스펙 상 리플렉션이나 프록시 같은 기술을 사용하기 위해 필요한 기본 생성자
    protected Address() {
    }

    private String city;
    private String street;
    private String zipcode;
}
