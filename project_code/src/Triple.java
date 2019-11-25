public class Triple<X, Y, Z> {

    private final X x;
    private final Y y;
    private final Z z;

    public Triple(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Y getY() {
        return y;
    }
    public X getX() {
        return x;
    }
    public Z getZ() { return z; }


    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Triple)) {
            return false;
        }
        Triple c = (Triple) o;
        return this.x.equals(c.getX()) && this.y.equals(c.getY()) && this.z.equals(c.getZ());
    }



    //TODO: check heres
    @Override
    public int hashCode() {
        return x.hashCode() * (y.hashCode() + 256)* z.hashCode();
    }


}