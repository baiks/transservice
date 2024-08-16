package com.trans.service.repos;
import com.trans.service.entities.Accounts;
import com.trans.service.entities.Customers;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountsRepo extends JpaRepository<Accounts, Long> {

    Optional<Accounts> findByAccountNumber(String account);

    @Cacheable("account_mem2#")
    Optional<Accounts> findById(long id);
    @Cacheable("account_mem1#")
    Optional<Accounts> findByCustomer(Customers customers);
}
