package com.example.lap_ecommerce.user.repository;

import com.example.lap_ecommerce.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}
