package com.example.bank_api.repository;

import com.example.bank_api.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {
    Optional<Account> findByCpf(Long cpf);
}
