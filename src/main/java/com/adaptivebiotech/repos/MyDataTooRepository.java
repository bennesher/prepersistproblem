package com.adaptivebiotech.repos;

import com.adaptivebiotech.entities.MyDataToo;
import io.micronaut.data.annotation.Repository;
import io.micronaut.data.repository.CrudRepository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public abstract class MyDataTooRepository implements CrudRepository<MyDataToo, Long> {

    @PersistenceContext
    private EntityManager em;

    public void flushAndClear() {
        em.flush();
        em.clear();
    }
}
