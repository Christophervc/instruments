package com.aplication.rest.instruments.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<UUID, User> {

    Optional<User> findByEmail(String email);

    boolean existByEmail(String email);

    boolean existByDni(String dni);
}
