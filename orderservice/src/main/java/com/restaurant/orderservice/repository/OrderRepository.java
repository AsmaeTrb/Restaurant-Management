package com.restaurant.orderservice.repository;
import com.restaurant.orderservice.entity.Order;
import org.springframework.data.cassandra.repository.CassandraRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends CassandraRepository<Order, String> {
    // CRUD auto : save, findById, findAll, deleteById
}

