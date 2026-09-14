package com.project.online_banking_application;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RegistrationFlowTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void registerPostSavesCustomerAndRedirectsToLogin() throws Exception {
        String suffix = String.valueOf(System.nanoTime());
        String username = "testuser" + suffix;

        mockMvc.perform(post("/register")
                        .param("fullName", "Test User")
                        .param("email", username + "@example.com")
                        .param("mobile", "9999999999")
                        .param("username", username)
                        .param("password", "password123"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/login"));

        assertTrue(customerRepository.findByUsername(username).isPresent());
    }
}
