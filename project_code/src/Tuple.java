public class Tuple<X, Y> {
    public final X x;
    public final Y y;
    public Tuple(X x, Y y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {

        // If the object is compared with itself then return true
        if (o == this) {
            return true;
        }

        /* Check if o is an instance of Complex or not
          "null instanceof [type]" also returns false */
        if (!(o instanceof Tuple)) {
            return false;
        }

        // typecast o to Complex so that we can compare data members
        Tuple c = (Tuple) o;

        // Compare the data members and return accordingly
        return this.x.equals(c.x) && this.y.equals(c.y);
    }
}
