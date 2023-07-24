package jpabook.jpashop.domain.item;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@DiscriminatorValue("M")
@Setter
public class Movie extends Item {
    private String director;
    private String actor;

}
