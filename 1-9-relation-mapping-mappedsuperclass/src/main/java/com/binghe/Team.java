package com.binghe;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
public class Team extends BaseEntity {

    @Id @GeneratedValue
    private Long id;

    private String name;
}
