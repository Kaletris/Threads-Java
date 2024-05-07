package com.example.demo;

import jakarta.persistence.*;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;

@Entity
@Table(name = "my_user")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String passwordSalt;
    private String passwordHash;
    private boolean isAdmin = false;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean checkPassword(String password) throws NoSuchAlgorithmException {
        String inputHash = hashPassword(password);
        return inputHash.equals(passwordHash);
    }

    protected User(){}

    public User(String name, String password) throws NoSuchAlgorithmException{
        this.name = name;
        SecureRandom sr = new SecureRandom();
        byte[] salt = new byte[16];
        sr.nextBytes(salt);
        passwordSalt = HexFormat.of().formatHex(salt);
        passwordHash = hashPassword(password);
    }

    private String hashPassword(String password) throws NoSuchAlgorithmException {
        String passwordAndSalt = password + passwordSalt;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(passwordAndSalt.getBytes(StandardCharsets.UTF_8));
        return HexFormat.of().formatHex(hash);
    }
}