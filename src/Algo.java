import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@FunctionalInterface
interface evalFunc {
    float eval(Point a, Point b,Point c);
}
public class Algo {
    public static float borderlineFactor = 0.7f;

    public static List<PointEvaluationRes>  RoutTraveler(Route route,ClientNode client,evalFunc evaluator){
        List<PointEvaluationRes> res = new ArrayList<>();
        Point cur = route.depot;
        Point next = route.getNext(cur);
        do {
            float dis = evaluator.eval(client,cur,next);
            res.add(new PointEvaluationRes(cur,dis));
            cur = next;
            next = route.getNext(next);
        }while (cur!=route.depot);

        Collections.sort(res);
        return  res;
    }


    public static PointEvaluationRes optimalDistancePosOfInsertionToRoute(Route route, ClientNode client){
        return RoutTraveler(route,client,(a,b,c)->Route.insertionDistance(a,b,c)).get(0);
    }

    public static boolean RouteMoveOneStepToLocalOptmal(Route route){
        Point cur = route.getNext(route.depot);
        float removeScore = Route.insertionDistance(cur,route.depot,route.getNext(cur));
        while (cur != route.depot) {
            PointEvaluationRes res =  RoutTraveler(route,(ClientNode) cur,
                    (toInsert,a,b)->{
                        return Route.insertionDistance(toInsert,a,b) - removeScore;
                    }
                    ).get(0);
            if(res.score < 0){
                System.out.println(res.score);
                route.remove(cur);
                route.insert(res.point,(ClientNode) cur);
                return true;
            }
            cur = route.getNext(cur);
        }
        return false;
    }

    public static void SolutionToLocalOptimal(Solution solution){
        for (Route route:solution.Routs.values()) {
            while (RouteMoveOneStepToLocalOptmal(route));
        }
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
                PointEvaluationRes  res = optimalDistancePosOfInsertionToRoute(route,bclient);
                if(bestDis > res.score){
                    bestDis = res.score;
                    bestRout = route;
                    bestPoint = res.point;
                }
            }
            if(bestRout!=null){
                bestRout.insert(bestPoint,bclient);
            }
        }
        solution.borderlineClientsToInsert.clear();
    }

}
