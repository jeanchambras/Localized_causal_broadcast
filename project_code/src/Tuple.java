public class Tuple<X, Y> {
    public final X x;
    public final Y y;

    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Tuple)) {
            return false;
        }
        Tuple c = (Tuple) o;
        return this.x.equals(c.x) && this.y.equals(c.y);
    }

    @Override
    public int hashCode() {
        return x.hashCode() * (y.hashCode() + 256);
    }


}