package com.adaptivebiotech.repos;

import com.adaptivebiotech.entities.MyData;
import com.adaptivebiotech.utils.SecurityContext;
import io.micronaut.data.annotation.Repository;
// import io.micronaut.data.annotation.event.PrePersist;
import io.micronaut.data.repository.CrudRepository;
import jakarta.inject.Inject;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.PrePersist;

@Repository
public abstract class MyDataRepository implements CrudRepository<MyData, Long> {

    @Inject
    private SecurityContext context;

    @PersistenceContext
    private EntityManager em;

    @PrePersist
    public void setCreator(MyData data) {
        data.setCreatedBy(context.getUser());
        System.out.printf("Created by %s\n", data.getCreatedBy());
    }

    public void flushAndClear() {
        em.flush();
        em.clear();
    }
}
