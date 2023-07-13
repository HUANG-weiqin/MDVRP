
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Route {
    public static ResolutionCache<Route,Double> routeScoresCache = new ResolutionCache<>(200000);
    public DepotNode depot;
    public double distance = 0;
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
        if(!nexts.containsKey(cur))
            return;
        Point next = nexts.get(cur);
        Point prev = prevs.get(cur);
        nexts.put(prev,next);
        prevs.put(next,prev);
        nexts.remove(cur);
        prevs.remove(cur);
        distance -= insertionDistance(cur,prev,next);
    }

    public static double insertionDistance(Point newcomer,Point cur,Point next){
        return Point.distance(cur,newcomer) + Point.distance(newcomer,next) - Point.distance(cur,next);
    }

    public Point getNext(Point p){
        return nexts.get(p);
    }

    public Point getPrev(Point p){
        return prevs.get(p);
    }

    public List<ClientNode> getClientsByOrder(){
        List<ClientNode> res = new ArrayList<>();
        Point cur = getNext(depot);
        while (cur != depot) {
            res.add((ClientNode) cur);
            cur = getNext(cur);
        }
        return res;
    };

    public List<Point> getPointsByOrder(){
        List<Point> res = new ArrayList<>(nexts.size());
        res.add(depot);
        Point cur = getNext(depot);
        while (cur != depot) {
            res.add((ClientNode) cur);
            cur = getNext(cur);
        }
        return res;
    };

    public int size(){
        return nexts.size();
    }

    public double getSubCircleDistance(ClientNode from,ClientNode to){
        if(from == to){
            return 2*Point.distance(from,depot);
        }
        double res = Point.distance(from,depot) + Point.distance(to,depot);
        Point cur = from;
        Point next = getNext(from);
        while (cur!=to){
            res += Point.distance(cur,next);
            cur = next;
            next = getNext(next);
        }
        return res;
    }

    public int getSubRoutDemand(ClientNode from,ClientNode to){
        int res = from.demand;
        ClientNode cur = from;
        while (cur!=to){
            res += cur.demand;
            cur = (ClientNode) getNext(cur);
        }
        return res;
    }

    @Override
    public int hashCode() {
        int dis = (int)(distance);
        return (dis*dis*10000 * 303147* nexts.size() * nexts.hashCode());
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Route){
            Route other = (Route) obj;
            boolean a =  nexts.equals(other.nexts);
            if(a)
                return true;
            return a;
        }
        return false;
    }

    public double evaluate(List<Integer> cars){

        if(routeScoresCache.containsKey(this))
            return routeScoresCache.get(this);
        double res = Algo.evaluateRoute(this,cars);
        routeScoresCache.put(this,res);
        return res;
    }
}
