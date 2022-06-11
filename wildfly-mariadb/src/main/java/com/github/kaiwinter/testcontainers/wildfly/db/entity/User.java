package com.github.kaiwinter.testcontainers.wildfly.db.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Data;

@Entity
@Table(name = "user")
@Data
public class User {

   @Id
   @Column(name = "id", unique = true, nullable = false)
   @GeneratedValue(strategy = GenerationType.IDENTITY)
   private int id;

   @Column(name = "username", nullable = false)
   private String username;

   @Column(name = "login_count", nullable = false)
   private int loginCount;

}
