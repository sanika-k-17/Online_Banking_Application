package com.project.online_banking_application;


import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TransactionRepository
        extends JpaRepository<Transaction, Long> {

    List<Transaction> findByUsernameOrderByTransactionDateDesc(String username);

}
