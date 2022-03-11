package com.adaptivebiotech;

import com.adaptivebiotech.entities.MyData;
import com.adaptivebiotech.entities.MyDataToo;
import com.adaptivebiotech.repos.MyDataRepository;
import com.adaptivebiotech.repos.MyDataTooRepository;
import com.adaptivebiotech.utils.SecurityContext;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.Test;

import jakarta.inject.Inject;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@MicronautTest
class PrePersistProblemTest {

    @Inject
    private SecurityContext securityContext;

    @Inject
    private MyDataRepository repository;

    @Inject
    private MyDataTooRepository repository2;

    @Inject
    private Application application;

    @Test
    void testMakeData() throws Exception {
        MyData data1 = securityContext.with("bensc",
                () -> {
                    MyData myData = application.makeData("Hello, World!");
                    repository.flushAndClear();
                    return myData;
                });

        MyData data2 = repository.findById(data1.getId()).orElseThrow();

        assertEquals(data1.getData(), data2.getData());
        assertNotNull(data2.getCreated());
        assertEquals("bensc", data2.getCreatedBy());
    }

    @Test
    void testMakeDataToo() throws Exception {
        MyDataToo dataToo1 = securityContext.with("bensc",
                () -> {
                    MyDataToo myDataToo = application.makeDataToo("Hello, World!");
                    repository2.flushAndClear();
                    return myDataToo;
                });

        MyDataToo dataToo2 = repository2.findById(dataToo1.getId()).orElseThrow();

        assertEquals(dataToo1.getData(), dataToo2.getData());
        assertNotNull(dataToo2.getCreated());
        assertEquals("bensc", dataToo2.getCreatedBy());
    }

}
