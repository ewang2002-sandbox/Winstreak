package xyz.achsdiscord.classes;

import java.util.Objects;

public class TuplePair<X, Y> {
    public final X left;
    public final Y right;

    public TuplePair(X x, Y y) {
        this.left = x;
        this.right = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TuplePair<?, ?> tuplePair = (TuplePair<?, ?>) o;
        return Objects.equals(left, tuplePair.left) &&
                Objects.equals(right, tuplePair.right);
    }

    @Override
    public int hashCode() {
        return Objects.hash(left, right);
    }
}
