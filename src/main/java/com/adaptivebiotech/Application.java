package com.adaptivebiotech;

import com.adaptivebiotech.entities.MyData;
import com.adaptivebiotech.entities.MyDataToo;
import com.adaptivebiotech.repos.MyDataRepository;
import com.adaptivebiotech.repos.MyDataTooRepository;
import io.micronaut.runtime.Micronaut;
import jakarta.inject.Inject;

import javax.transaction.Transactional;

public class Application {

    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }

    @Inject
    private MyDataRepository repository;

    @Transactional
    MyData makeData(String theData) {
        MyData data = new MyData();

        data.setData(theData);

        return repository.save(data);
    }

    @Inject
    private MyDataTooRepository repository2;

    @Transactional
    MyDataToo makeDataToo(String theData) {
        MyDataToo data = new MyDataToo();

        data.setData(theData);

        return repository2.save(data);
    }


}
