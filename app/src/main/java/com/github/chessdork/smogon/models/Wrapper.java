package com.github.chessdork.smogon.models;

public class Wrapper<T> {
    String status;
    T result;

    public String getStatus() {
        return status;
    }

    public T getResult() {
        return result;
    }
}
