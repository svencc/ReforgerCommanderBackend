package lib.gecom;

import lombok.*;

import java.io.Serializable;

@Getter
@Builder
@EqualsAndHashCode
@RequiredArgsConstructor
public class GeWorldState implements Serializable {

    @NonNull
    public final String key;
    @NonNull
    public final Integer value;

}
