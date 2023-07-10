
public class Point {
    public float x,y;
    public int id;

    public Point(int id, float x, float y) {
        this.x = x;
        this.y = y;
        this.id = id;
    }

    static float distance(Point a,Point b){
        float xx = a.x-b.x;
        float yy = a.y-b.y;
        return  (float)Math.sqrt(xx*xx+yy*yy);
    }

    @Override
    public int hashCode() {
        return id;
    }
}
