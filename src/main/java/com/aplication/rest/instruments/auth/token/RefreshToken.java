package com.aplication.rest.instruments.auth.token;

import com.aplication.rest.instruments.user.User;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "refresh_tokens")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    // long random string we'll give to our frontend
    @Column(nullable = false, unique = true)
    private String token;

    // Using Instant for absolute timestamp (UTC)
    @Column(nullable = false)
    private Instant expiryDate;

    // one-to-one relationship: a user has a active Refresh Token
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}