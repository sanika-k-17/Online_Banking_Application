package com.project.online_banking_application;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;
    private final TransactionRepository transactionRepository;
    private final PasswordEncoder passwordEncoder;

    public CustomerService(
            CustomerRepository customerRepository,
            TransactionRepository transactionRepository,
            PasswordEncoder passwordEncoder) {

        this.customerRepository = customerRepository;
        this.transactionRepository = transactionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // =========================
    // REGISTER CUSTOMER
    // =========================

    public Customer registerCustomer(Customer customer) {

        // Check email
        if (customerRepository
                .findByEmail(customer.getEmail())
                .isPresent()) {

            throw new IllegalArgumentException(
                    "Email is already registered."
            );
        }

        // Check account number
        if (customerRepository
                .existsByAccountNumber(customer.getAccountNumber())) {

            throw new IllegalArgumentException(
                    "Account number is already registered."
            );
        }

        // Check account number is not empty
        if (customer.getAccountNumber() == null ||
                customer.getAccountNumber().trim().isEmpty()) {

            throw new IllegalArgumentException(
                    "Account number is required."
            );
        }

        // Remove extra spaces
        customer.setAccountNumber(
                customer.getAccountNumber().trim()
        );

        // Encode password
        customer.setPassword(
                passwordEncoder.encode(
                        customer.getPassword()
                )
        );

        // Initial balance
        if (customer.getBalance() < 0) {
            customer.setBalance(0);
        }

        return customerRepository.save(customer);
    }

    // =========================
    // FIND CUSTOMER BY EMAIL
    // =========================

    public Customer findByEmail(String email) {

        return customerRepository
                .findByEmail(email)
                .orElse(null);
    }

    // =========================
    // GET CUSTOMER BY USERNAME
    // Username = Email
    // =========================

    public Customer getCustomerByUsername(String username) {

        return customerRepository
                .findByEmail(username)
                .orElse(null);
    }

    // =========================
    // FIND BY ACCOUNT NUMBER
    // =========================

    public Customer findByAccountNumber(
            String accountNumber) {

        return customerRepository
                .findByAccountNumber(accountNumber)
                .orElse(null);
    }

    // =========================
    // DEPOSIT
    // =========================

    @Transactional
    public void deposit(
            String username,
            double amount) {

        if (amount <= 0) {

            throw new IllegalArgumentException(
                    "Deposit amount must be greater than 0."
            );
        }

        Customer customer =
                getCustomerByUsername(username);

        if (customer == null) {

            throw new IllegalArgumentException(
                    "Customer not found."
            );
        }

        customer.setBalance(
                customer.getBalance() + amount
        );

        customerRepository.save(customer);

        Transaction transaction =
                new Transaction(
                        username,
                        "Deposit",
                        amount,
                        "Amount deposited successfully"
                );

        transactionRepository.save(transaction);
    }

    // =========================
    // WITHDRAW
    // =========================

    @Transactional
    public void withdraw(
            String username,
            double amount) {

        if (amount <= 0) {

            throw new IllegalArgumentException(
                    "Withdrawal amount must be greater than 0."
            );
        }

        Customer customer =
                getCustomerByUsername(username);

        if (customer == null) {

            throw new IllegalArgumentException(
                    "Customer not found."
            );
        }

        if (customer.getBalance() < amount) {

            throw new IllegalArgumentException(
                    "Insufficient balance."
            );
        }

        customer.setBalance(
                customer.getBalance() - amount
        );

        customerRepository.save(customer);

        Transaction transaction =
                new Transaction(
                        username,
                        "Withdraw",
                        amount,
                        "Amount withdrawn successfully"
                );

        transactionRepository.save(transaction);
    }

    // =========================
    // GET OTHER CUSTOMERS
    // =========================

    public List<Customer> getAllCustomersExcept(
            String username) {

        return customerRepository.findAll()
                .stream()
                .filter(customer ->
                        !customer.getEmail().equals(username)
                )
                .toList();
    }

    // =========================
    // TRANSFER
    // =========================

    @Transactional
    public void transfer(
            String senderUsername,
            String receiverUsername,
            double amount) {

        if (amount <= 0) {

            throw new IllegalArgumentException(
                    "Transfer amount must be greater than 0."
            );
        }

        if (senderUsername.equals(receiverUsername)) {

            throw new IllegalArgumentException(
                    "You cannot transfer money to yourself."
            );
        }

        Customer sender =
                getCustomerByUsername(senderUsername);

        Customer receiver =
                getCustomerByUsername(receiverUsername);

        if (sender == null) {

            throw new IllegalArgumentException(
                    "Sender account not found."
            );
        }

        if (receiver == null) {

            throw new IllegalArgumentException(
                    "Receiver account not found."
            );
        }

        if (sender.getBalance() < amount) {

            throw new IllegalArgumentException(
                    "Insufficient balance."
            );
        }

        sender.setBalance(
                sender.getBalance() - amount
        );

        receiver.setBalance(
                receiver.getBalance() + amount
        );

        customerRepository.save(sender);
        customerRepository.save(receiver);

        Transaction senderTransaction =
                new Transaction(
                        senderUsername,
                        "Transfer",
                        amount,
                        "Money transferred to "
                                + receiverUsername
                );

        Transaction receiverTransaction =
                new Transaction(
                        receiverUsername,
                        "Transfer",
                        amount,
                        "Money received from "
                                + senderUsername
                );

        transactionRepository.save(senderTransaction);
        transactionRepository.save(receiverTransaction);
    }

    // =========================
    // TRANSACTION HISTORY
    // =========================

    public List<Transaction> getTransactions(
            String username) {

        return transactionRepository
                .findByUsernameOrderByTransactionDateDesc(
                        username
                );
    }
}