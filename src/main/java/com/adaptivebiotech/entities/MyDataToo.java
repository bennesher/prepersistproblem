package com.adaptivebiotech.entities;

import com.adaptivebiotech.utils.SecurityContext;
import io.micronaut.data.annotation.DateCreated;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
public class MyDataToo {
    private Long id;

    @DateCreated
    @Column(nullable = false, updatable = false)
    private Timestamp created;

    @Column(nullable = false, updatable = false)
    private String createdBy;

    @Column
    private String data;

    public void setId(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    @PrePersist
    public void setCreatedBy() {
        this.createdBy = SecurityContext.getInstance().getUser();
        System.out.printf("Created by: %s\n", createdBy);
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
