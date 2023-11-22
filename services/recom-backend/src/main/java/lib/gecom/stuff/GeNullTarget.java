package lib.gecom.stuff;

import lib.gecom.GeTargetable;
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
