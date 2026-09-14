package com.project.online_banking_application;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class HomeController {

    private final CustomerService customerService;

    public HomeController(CustomerService customerService) {
        this.customerService = customerService;
    }

    // =========================
    // HOME
    // =========================

    @GetMapping("/")
    public String home() {

        return "home";
    }

    // =========================
    // LOGIN PAGE
    // =========================

    @GetMapping("/login")
    public String login() {

        return "login";
    }

    // =========================
    // REGISTER PAGE
    // =========================

    @GetMapping("/register")
    public String register() {

        return "register";
    }

    // =========================
    // REGISTER CUSTOMER
    // =========================

    @PostMapping("/register")
    public String registerCustomer(
            @ModelAttribute Customer customer,
            Model model) {

        try {

            customerService.registerCustomer(customer);

            return "redirect:/login?registered";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "register";
        }
    }

    // =========================
    // DASHBOARD
    // =========================

    @GetMapping("/dashboard")
    public String dashboard(
            Model model,
            Principal principal) {

        Customer customer =
                customerService.getCustomerByUsername(
                        principal.getName()
                );

        model.addAttribute(
                "customer",
                customer
        );

        return "dashboard";
    }

    // =========================
    // PROFILE
    // =========================

    @GetMapping("/profile")
    public String profile(
            Model model,
            Principal principal) {

        Customer customer =
                customerService.getCustomerByUsername(
                        principal.getName()
                );

        model.addAttribute(
                "customer",
                customer
        );

        return "profile";
    }

    // =========================
    // DEPOSIT PAGE
    // =========================

    @GetMapping("/deposit")
    public String depositPage() {

        return "deposit";
    }

    // =========================
    // DEPOSIT
    // =========================

    @PostMapping("/deposit")
    public String deposit(
            @RequestParam double amount,
            Principal principal,
            Model model) {

        try {

            customerService.deposit(
                    principal.getName(),
                    amount
            );

            model.addAttribute(
                    "message",
                    "Deposit Successful!"
            );

            return "logout";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "deposit";
        }
    }

    // =========================
    // WITHDRAW PAGE
    // =========================

    @GetMapping("/withdraw")
    public String withdrawPage() {

        return "withdraw";
    }

    // =========================
    // WITHDRAW
    // =========================

    @PostMapping("/withdraw")
    public String withdraw(
            @RequestParam double amount,
            Principal principal,
            Model model) {

        try {

            customerService.withdraw(
                    principal.getName(),
                    amount
            );

            model.addAttribute(
                    "message",
                    "Withdrawal Successful!"
            );

            return "logout";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            return "withdraw";
        }
    }

    // =========================
    // TRANSFER PAGE
    // =========================

    @GetMapping("/transfer")
    public String transferPage(
            Model model,
            Principal principal) {

        model.addAttribute(
                "customers",
                customerService.getAllCustomersExcept(
                        principal.getName()
                )
        );

        return "Transfar";
    }

    // =========================
    // TRANSFER
    // =========================

    @PostMapping("/transfer")
    public String transfer(
            @RequestParam String receiverUsername,
            @RequestParam double amount,
            Principal principal,
            Model model) {

        try {

            customerService.transfer(
                    principal.getName(),
                    receiverUsername,
                    amount
            );

            model.addAttribute(
                    "message",
                    "Money Transfer Successful!"
            );

            return "logout";

        } catch (IllegalArgumentException e) {

            model.addAttribute(
                    "error",
                    e.getMessage()
            );

            model.addAttribute(
                    "customers",
                    customerService.getAllCustomersExcept(
                            principal.getName()
                    )
            );

            return "Transfar";
        }
    }

    // =========================
    // TRANSACTIONS
    // =========================

    @GetMapping("/transactions")
    public String transactions(
            Model model,
            Principal principal) {

        model.addAttribute(
                "transactions",
                customerService.getTransactions(
                        principal.getName()
                )
        );

        return "transactions";
    }
}