package org.example.domain.repository;

import org.example.domain.SignUpForm;
import org.example.domain.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
