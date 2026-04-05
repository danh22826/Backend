package com.example.demo;

import jakarta.persistence.*;

@Entity
public class Test {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}