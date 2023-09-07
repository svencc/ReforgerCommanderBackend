package lib.goap.state;

import lombok.Data;
import lombok.NonNull;

@Data
public class GoapState implements Stateful, Important {

    @NonNull
    private final Integer importance;
    @NonNull
    private final String effect;
    @NonNull
    private final Object value;

    /**
     * @param importance the importance of the state being reached Only necessary if
     *                   the state is used to define a worldState. Has no effect in
     *                   Actions being taken. Do NOT set this to Integer.MaxValue since
     *                   this causes the goal to be removed from the HashSet by the
     *                   Planner.
     * @param effect   the effect the state has.
     * @param value      the value of the effect. Since "Object" is being used this is
     *                   NOT type safe!
     */
    public GoapState(
            @NonNull final Integer importance,
            @NonNull final String effect,
            @NonNull final Object value
    ) {
        if (importance < 0) {
            this.importance = 0;
        } else {
            this.importance = importance;
        }
        this.effect = effect;
        this.value = value;
    }

    public GoapState(
            @NonNull final String effect,
            @NonNull final Object value
    ) {
        this.importance = 0;
        this.effect = effect;
        this.value = value;
    }

}
