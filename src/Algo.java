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
    public static float epsilon = 0.00001f;

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

    public static boolean RouteMoveOneStepToLocalOptimal(Route route){

        for (ClientNode cur: route.getClientsByOrder()) {
            float removeScore = Route.insertionDistance(cur,route.getPrev(cur),route.getNext(cur));
            Route tmp = new Route(route);
            tmp.remove(cur);
            PointEvaluationRes res =  RoutTraveler(tmp,(ClientNode) cur,
                    (toInsert,a,b)->{
                        return Route.insertionDistance(toInsert,a,b) - removeScore;
                    }
            ).get(0);
            if(res.score < -epsilon){
                route.remove(cur);
                route.insert(res.point,(ClientNode) cur);
                return true;
            }
        }

        return false;
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

    public static float RouteMoveOneStepToDiversification(Route route,List<ClientNode> clientNodes,float limit){

        for (ClientNode cur: clientNodes) {
            float removeScore = Route.insertionDistance(cur,route.getPrev(cur),route.getNext(cur));
            PointEvaluationRes res =  RoutTraveler(route,(ClientNode) cur,
                    (toInsert,a,b)->{
                        float t = Route.insertionDistance(toInsert,a,b) - removeScore;
                        if(t<=epsilon)
                            return Float.MAX_VALUE;
                        return t;
                    }
            ).get(0);
            if(res.score < limit){
                route.remove(cur);
                route.insert(res.point,(ClientNode) cur);
                clientNodes.remove(cur);
                return res.score;
            }
        }
        return Float.MAX_VALUE;
    }

    public static int RoutDiff(Route r1,Route r2){
        int res = 0;
        Point cur = r1.depot;
        do {
            if(r1.getNext(cur) != r2.getNext(cur)){
                res+=1;
            }
            cur = r1.getNext(cur);
        }while (cur!=r1.depot);
        return res;
    }

    public static int SolutionDiff(Solution r1,Solution r2){
        int res = 0;
        for (DepotNode depot:r1.Routs.keySet()) {
            res += RoutDiff(r1.Routs.get(depot),r2.Routs.get(depot));
        }
        return res;
    }

    public static void DiversificationToLimit(Route route,float limit,float step,float factor){
        List<ClientNode> clientNodes = route.getClientsByOrder();
        Collections.shuffle(clientNodes);
        Route ori = new  Route(route);
        while (limit - step >= step){
            float cost = RouteMoveOneStepToDiversification(route,clientNodes,step);
            if(cost>limit){
                step*=factor;
                continue;
            }
            limit-=cost;
        }

        System.out.println(RoutDiff(ori,route));
    }

}
