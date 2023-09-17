package lib.clipboard.concavehull;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class Point {

    private final Double x;
    private final Double y;

    public String toString() {
        return "(" + x + " " + y + ")";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Point) {
            if (x.equals(((Point) obj).getX()) && y.equals(((Point) obj).getY())) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int yHash = y.hashCode();
        final int xHash = x.hashCode();

        return yHash ^ xHash;
    }
}