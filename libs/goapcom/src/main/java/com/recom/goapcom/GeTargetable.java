package com.recom.goapcom;

import lombok.NonNull;

import java.util.Optional;

public interface GeTargetable {

    @NonNull
    Optional<Object> getTargetPosition();

}
