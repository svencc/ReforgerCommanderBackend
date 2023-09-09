package lib.maze;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class MazeLocation {

    private final int row;
    private final int column;

    @Builder
    public MazeLocation(final int row, final int column) {
        this.row = row;
        this.column = column;
    }

}
