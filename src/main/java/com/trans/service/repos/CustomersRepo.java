package com.trans.service.repos;


import com.trans.service.entities.Customers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomersRepo extends JpaRepository<Customers, Long> {
}
