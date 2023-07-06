

public class PointEvaluationRes implements Comparable<PointEvaluationRes> {
    public Point point;
    float score;

    public PointEvaluationRes(Point point, float score){
        this.point = point;
        this.score = score;
    }

    @Override
    public int compareTo(PointEvaluationRes o) {
        return Float.compare(score, o.score);
    }
}
