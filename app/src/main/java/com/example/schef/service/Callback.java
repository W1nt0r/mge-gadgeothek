package com.example.schef.service;

/**
 * Created by Schef on 12.10.2017.
 */


public interface Callback<T> {
    void onCompletion(T input);
    void onError(String message);
}