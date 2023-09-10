package lib.gecom;

import lombok.NonNull;

import java.util.*;

public class GeAgent {

    @NonNull
    private final List<GeAction> possibleActions = new ArrayList<>();
    @NonNull
    private final Map<GeSubgoal, Integer> subgoals = new HashMap<>();
    @NonNull
    private final GePlanner planner;
    @NonNull
    private final Queue<GeAction> plan = new LinkedList<>();
    @NonNull
    private final Stack<GeAction> planStack = new Stack<>();

//    private GeAction currentAction;
//    private GeSubgoal currentGoal;

    public GeAgent(final List<GeAction> possibleActions) {
        if (possibleActions.isEmpty()) {
            this.possibleActions.addAll(possibleActions);
        }

        planner = new GePlanner();
    }

    public void addSubgoal(@NonNull final GeSubgoal subgoal) {
        subgoals.put(subgoal, 0);
    }

    /*
    // Agents seem to implement these methods (Unity; maybe this will be useful for us later)
    public void Start()
    {
        // Hier erfolgt die anfängliche Einrichtung des Agenten.
        // Das kann das Initialisieren von Variablen oder das Einrichten von Komponenten beinhalten.
    }

    public void Update()
    {
        // Hier werden Aktualisierungen für den Agenten in jedem Frame durchgeführt.
        // Dies kann das Bewegen des Agenten, das Prüfen von Kollisionen oder andere laufende Operationen beinhalten.
    }
     */
}
