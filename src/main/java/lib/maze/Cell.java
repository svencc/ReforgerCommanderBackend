package lib.maze;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum Cell {

    EMPTY("[ ]"),
    BLOCKED("[X]"),
    START("[S]"),
    GOAL("[G]"),
    PATH("[*]");

    @NonNull
    private final String symbol;

    public static Cell fromSymbol(String symbol) {
        for (Cell cell : Cell.values()) {
            if (cell.symbol.equals(symbol)) {
                return cell;
            }
        }
        throw new IllegalArgumentException("No cell with symbol " + symbol + " found");
    }

    @Override
    public String toString() {
        return symbol;
    }

}
