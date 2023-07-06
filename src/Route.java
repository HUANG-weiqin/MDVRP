
import java.util.Hashtable;
import java.util.Map;

public class Route {
    public DepotNode depot;
    public float distance = 0;
    public Map<Point,Point> nexts;
    public Map<Point,Point> prevs;

    public Route(DepotNode depot) {
        this.depot = depot;
        nexts = new Hashtable<>();
        prevs = new Hashtable<>();
        nexts.put(depot,depot);
        prevs.put(depot,depot);
    }

    public Route(Route route) {
        depot = route.depot;
        nexts = new Hashtable<>(route.nexts);
        prevs = new Hashtable<>(route.prevs);
        distance = route.distance;
    }

    public void insert(Point cur,ClientNode newcomer){
        Point next = nexts.get(cur);
        nexts.put(newcomer,next);
        prevs.put(next,newcomer);
        nexts.put(cur,newcomer);
        prevs.put(newcomer,cur);
        distance += insertionDistance(newcomer,cur,next);
    }

    public void remove(Point cur){
        Point next = nexts.get(cur);
        Point prev = prevs.get(cur);
        nexts.put(prev,next);
        prevs.put(next,prev);
        distance -= insertionDistance(cur,prev,next);
    }

    public static float insertionDistance(Point newcomer,Point cur,Point next){
        return Point.distance(cur,newcomer) + Point.distance(newcomer,next) - Point.distance(cur,next);
    }

    @Override
    public String toString() {
        String res = "";
        res += "Route-Depot: "+ depot.id + '\n';
        res += "Route-Size: "+ (nexts.size() - 1) + '\n';
        res += "Route-Distance: "+ distance + '\n';
        return res ;
    }
}
