package com.adaptivebiotech.utils;

import jakarta.annotation.PostConstruct;
import jakarta.inject.Singleton;

import java.util.concurrent.Callable;

@Singleton
public class SecurityContext {
    private final ThreadLocal<String> currentUser = new ThreadLocal<>();

    private static SecurityContext instance;

    @PostConstruct
    public void initInstance() {
        SecurityContext.instance = this;
    }

    public static SecurityContext getInstance() {
        return instance;
    }

    public <T> T with(String username, Callable<T> task) throws Exception {
        String prev = currentUser.get();
        try {
            currentUser.set(username);
            return task.call();
        } finally {
            currentUser.set(prev);
        }
    }

    public String getUser() {
        return currentUser.get();
    }
}
