package com.recom.goapcom.stuff;

import com.recom.goapcom.GeTargetable;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.util.Optional;

@NoArgsConstructor
public class GeNullTarget implements GeTargetable {
    
    @NonNull
    @Override
    public Optional<Object> getTargetPosition() {
        return Optional.empty();
    }

}
