package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
public class test {
    @Id
    @GeneratedValue
    private Long id;
    private String name;
}