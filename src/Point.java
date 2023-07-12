
public class Point {
    public double x;
    public double y;
    public int id;

    public Point(int id, double x, double y) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    static double distance(Point a,Point b){
        double xx = a.x-b.x;
        double yy = a.y-b.y;
        return  (double) Math.sqrt(xx*xx+yy*yy);
    }

    @Override
    public int hashCode() {
        return (id+1)*(id+1);
    }
}
