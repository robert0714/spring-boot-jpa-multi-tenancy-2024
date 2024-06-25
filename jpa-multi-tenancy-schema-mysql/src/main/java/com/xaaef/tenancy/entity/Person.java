package com.xaaef.tenancy.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Entity
@Table(name = "pms_person")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Person implements java.io.Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private Integer age;

    @ElementCollection
    private Set<Integer> grade;

    @ElementCollection
    private Set<String> hobby;

}
