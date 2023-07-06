import javafx.util.Pair;

import java.util.List;

public class Algo {
    public static float borderlineFactor = 0.7f;
    public static Pair<Point,Float> optimalDistancePosOfInsertionToRoute(Route route, ClientNode c){

        Point bestcur = null;
        float minDis = Float.MAX_VALUE;

        Point cur = route.depot;
        Point next = route.nexts.get(route.depot);
        do {
            float newDis = Route.insertionDistance(c,cur,next);
            if(newDis < minDis){
                minDis = newDis;
                bestcur = cur;
            }
        }while (cur!=route.depot);

        return new Pair<Point,Float>(bestcur,minDis);
    }

    public static DepotNode BorderLineTest(ClientNode client, List<DepotNode> depots){
        if(depots.size() == 0)
            return null;
        if(depots.size() == 1)
            return depots.get(0);

        depots.sort((a,b)->{
            return Float.compare(Point.distance(client,a), Point.distance(client,b));
        });

        DepotNode first = depots.get(0);
        DepotNode second = depots.get(1);
        float f = Point.distance(first,client)/Point.distance(first,second);
        if(f <= borderlineFactor)
            return first;

        return null;
    }

    public static void LocalOptimalBorderlineInsertion(Solution solution){
        for (ClientNode bclient:solution.borderlineClientsToInsert) {
            Route bestRout = null;
            Point bestPoint = null;
            float bestDis = Float.MAX_VALUE;
            for (Route route:solution.Routs.values()) {
                Pair<Point,Float>  res = optimalDistancePosOfInsertionToRoute(route,bclient);
                if(bestDis > res.getValue()){
                    bestDis = res.getValue();
                    bestRout = route;
                    bestPoint = res.getKey();
                }
            }
            if(bestRout!=null){
                bestRout.insert(bestPoint,bclient);
            }
        }
        solution.borderlineClientsToInsert.clear();
    }

}
