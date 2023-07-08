

public class PointEvaluationRes implements Comparable<PointEvaluationRes> {
    public Point point;
    public float score;

    public Route route;
    public PointEvaluationRes(Route route,Point point, float score){
        this.route = route;
        this.point = point;
        this.score = score;
    }

    @Override
    public int compareTo(PointEvaluationRes o) {
        return Float.compare(score, o.score);
    }
}
