package lib.goap;

import lombok.NonNull;
import org.springframework.lang.Nullable;

public class GoapState {

    public Integer importance = 0;
    public String effect;
    public Object value;

    /**
     * @param importance the importance of the state being reached Only necessary if
     *                   the state is used to define a worldState. Has no effect in
     *                   Actions being taken. Do NOT set this to Integer.MaxValue since
     *                   this causes the goal to be removed from the HashSet by the
     *                   Planner.
     * @param effect     the effect the state has.
     * @param value      the value of the effect. Since "Object" is being used this is
     *                   NOT type safe!
     */
    public GoapState(
            @Nullable final Integer importance,
            @NonNull final String effect,
            @NonNull final Object value
    ) {
        if (importance == null || importance < 0) {
            this.importance = 0;
        } else {
            this.importance = importance;
        }

        this.effect = effect;
        this.value = value;
    }

}
