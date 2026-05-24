package com.ptithcm.frontend.repository;

public interface RepositoryCallback<T> {
    void onSuccess(T result);

    void onError(String message);
}
