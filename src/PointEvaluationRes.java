

public class PointEvaluationRes implements Comparable<PointEvaluationRes> {
    public Point point;
    public double score;

    public Route route;
    public PointEvaluationRes(Route route, Point point, double score){
        this.route = route;
        this.point = point;
        this.score = score;
    }

    @Override
    public int compareTo(PointEvaluationRes o) {
        return Double.compare(score, o.score);
    }
}
