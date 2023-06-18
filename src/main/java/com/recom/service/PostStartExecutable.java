package com.recom.service;

import lombok.NonNull;

public interface PostStartExecutable {

    @NonNull
    String identifyPostStartRunner();

    void executePostStartRunner();

}
