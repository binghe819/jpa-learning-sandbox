package com.binghe;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity
public class Parent {

    @Id @GeneratedValue
    private Long id;

    private String name;

//    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL) // CasCade 테스트
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Child> childs = new ArrayList<>();

    public void addChild(Child child) {
        this.childs.add(child);
        child.setParent(this);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Child> getChilds() {
        return childs;
    }
}
