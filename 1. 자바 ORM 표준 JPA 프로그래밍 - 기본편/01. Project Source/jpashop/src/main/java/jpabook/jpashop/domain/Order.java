package jpabook.jpashop.domain;

import javax.persistence.Entity;
import java.time.LocalDateTime;

@Entity
public class Order {

    private Long id;
    private Long memberId;
    private LocalDateTime orderDate;
    private OrderStatus orderStatus;
}
