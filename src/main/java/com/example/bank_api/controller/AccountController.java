package com.example.bank_api.controller;

import com.example.bank_api.model.Account;
import com.example.bank_api.model.Pix;
import com.example.bank_api.model.Transaction;
import com.example.bank_api.repository.AccountRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
public class AccountController {
    private final Logger log = LoggerFactory.getLogger(getClass());

    @Autowired
    private AccountRepository repository;

    @GetMapping
    public String get() {
        String name = "Projeto Bank";
        String group = "Thomas Rogrigues e Thomaz Bartol";
        return "Projeto: " + name + "\nIntegrantes: " + group;
    }

    @PostMapping("/accounts")
    @ResponseStatus(code = HttpStatus.CREATED)
    public Account create(@RequestBody Account account) {
        validateAccount(account);
        log.info("Cadastrando conta " + account.getName());
        repository.save(account);
        return account;
    }

    @GetMapping("/accounts")
    public List<Account> index() {
        return repository.findAll();
    }

    @GetMapping("/accounts/{id}")
    public ResponseEntity<Account> getId(@PathVariable Long id) {
        log.info("Buscando conta com id: " + id);
        return ResponseEntity.ok(getAccount(id));
    }

    @GetMapping("/accounts/by-cpf/{cpf}")
    public ResponseEntity<Account> getCpf(@PathVariable Long cpf) {
        log.info("Buscando conta com cpf: " + cpf);
        return ResponseEntity.ok(getAccountCpf(cpf));
    }

    @PatchMapping("/accounts/{id}/deactivate")
    public ResponseEntity<Account> patchActive(@PathVariable Long id) {
        log.info("Desativando conta com id: " + id);
        Account account = getAccount(id);
        account.setActive("n");
        repository.save(account);
        return ResponseEntity.ok(account);
    }

    @PatchMapping("/accounts/deposit")
    public ResponseEntity<Account> patchDeposit(@RequestBody Transaction transaction) {
        log.info("Fazendo depósito na conta com id: " + transaction.getId());
        Account updatedAccount = updateBalance(transaction, "deposit");
        repository.save(updatedAccount);
        return ResponseEntity.ok(updatedAccount);
    }

    @PatchMapping("/accounts/withdraw")
    public ResponseEntity<Account> patchWithdraw(@RequestBody Transaction transaction) {
        log.info("Fazendo saque na conta com id: " + transaction.getId());
        Account updatedAccount = updateBalance(transaction, "withdraw");
        repository.save(updatedAccount);
        return ResponseEntity.ok(updatedAccount);
    }

    @PatchMapping("/accounts/pix")
    public ResponseEntity<List<Account>> patchPix(@RequestBody Pix pix) {
        log.info("Fazendo pix da conta " + pix.getOriginId() + " para a conta " + pix.getDestinyId());
        List<Account> updatedAccounts = doPix(pix);
        for (Account account : updatedAccounts) {
            repository.save(account);
        }
        return ResponseEntity.ok(updatedAccounts);
    }

    private List<Account> doPix(Pix pix) {
        if (pix.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Valor da transação deve ser maior que zero");
        }
        Float newBalanceO = getAccount(pix.getOriginId()).getBalance() - pix.getAmount();
        Float newBalanceD = getAccount(pix.getDestinyId()).getBalance() + pix.getAmount();
        getAccount(pix.getOriginId()).setBalance(newBalanceO);
        getAccount(pix.getDestinyId()).setBalance(newBalanceD);
        List<Account> updatedAccounts = new ArrayList<>();
        updatedAccounts.add(getAccount(pix.getOriginId()));
        updatedAccounts.add(getAccount(pix.getDestinyId()));

        return updatedAccounts;
    }

    private Account updateBalance(Transaction transaction, String type) {
        if (transaction.getAmount() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O Valor da transação deve ser maior que zero");
        }
        if (type.equals("deposit")) {
            Float newBalance = getAccount(transaction.getId()).getBalance() + transaction.getAmount();
            getAccount(transaction.getId()).setBalance(newBalance);
        } else {
            Float newBalance = getAccount(transaction.getId()).getBalance() - transaction.getAmount();
            getAccount(transaction.getId()).setBalance(newBalance);
        }
        return getAccount(transaction.getId());
    }

    private Account getAccount(Long id){
        return repository.findById(id)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")
                );
    }

    private Account getAccountCpf(Long cpf){
        return repository.findByCpf(cpf)
                .orElseThrow(
                        () -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Conta não encontrada")
                );
    }

    private void validateAccount(Account account) {
        if(account.getName() == null || account.getCpf() == null){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados incompletos (Nome e CPF são obrigatórios)");
        }
        if (account.getBalance() < 0){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados Inválidos (Saldo inicial não pode ser negativo)");
        }
        List<String> validTypes = new ArrayList<>();
        validTypes.add("corrente");
        validTypes.add("poupança");
        validTypes.add("salário");
        if (!validTypes.contains(account.getType())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Dados Inválidos (Tipo deve estar entre corrente, poupança ou salário)");
        }
        if (account.getOpeningDate().isAfter(LocalDate.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Data Inválida (A data não pode ser no futuro)");
        }
    }
}
