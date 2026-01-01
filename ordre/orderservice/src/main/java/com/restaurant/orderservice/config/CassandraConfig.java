package com.restaurant.orderservice.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.cassandra.config.CqlSessionFactoryBean;
import org.springframework.data.cassandra.repository.config.EnableCassandraRepositories;

@Configuration
@EnableCassandraRepositories(basePackages = "com.restaurant.orderservice.repository")
public class CassandraConfig {

    @Bean
    public CqlSessionFactoryBean session() {
        CqlSessionFactoryBean session = new CqlSessionFactoryBean();
        session.setContactPoints("127.0.0.1");
        session.setPort(9042);
        session.setKeyspaceName("orders");
        session.setLocalDatacenter("datacenter1");
        return session;
    }
}

