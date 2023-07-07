package shortestPath;
import java.util.ArrayList;
import java.util.List;

public class Path implements Comparable<Path>{
    public List<Integer> path;
    public float distance;

    Path(int endPoint){
        this.path = new ArrayList<>();
        path.add(endPoint);
        distance=0;
    }

    Path(Path p){
        this.path = new ArrayList<>(p.path);
        this.distance = p.distance;
    }

    @Override
    public int compareTo(Path o) {
        return Float.compare(distance,o.distance);
    }

    public int getEndPoint(){
        return path.get(path.size()-1);
    }

    public void addNextPoint(int next,float cost){
        path.add(next);
        distance+=cost;
    }
}
