package com.restaurant.orderservice.entity;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.cassandra.core.mapping.PrimaryKey;
import org.springframework.data.cassandra.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.cassandra.core.mapping.CassandraType;
import org.springframework.data.cassandra.core.mapping.CassandraType.Name;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("orders")
public class Order {

    @PrimaryKey
    private String id;

    private Long customerId;

    @CassandraType(type = Name.LIST, typeArguments = Name.TEXT)
    private List<String> itemsJson; // Chaque String = JSON d'un OrderItem

    private double total;

    private LocalDateTime orderDate;

    private OrderStatus status;

}
