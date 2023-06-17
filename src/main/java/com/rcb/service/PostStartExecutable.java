package com.rcb.service;

import lombok.NonNull;

public interface PostStartExecutable {

    @NonNull
    String identifyPostStartRunner();

    void executePostStartRunner();

}
