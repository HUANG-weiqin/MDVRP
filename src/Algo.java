import shortestPath.Graph;
import shortestPath.Path;

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

    public static int getRandomInt(int lower,int upper){
        return lower +  (int) (Math.random() * upper);
    }

    public static List<PointEvaluationRes>  RoutTraveler(Route route,ClientNode client,evalFunc evaluator){
        List<PointEvaluationRes> res = new ArrayList<>();
        Point cur = route.depot;
        Point next = route.getNext(cur);
        do {
            float dis = evaluator.eval(client,cur,next);
            res.add(new PointEvaluationRes(route,cur,dis));
            cur = next;
            next = route.getNext(next);
        }while (cur!=route.depot);

        Collections.sort(res);
        return  res;
    }


    public static List<PointEvaluationRes> getAllPossiblePosOfInsertionToRoute(Route route, ClientNode client, float shift){
        return RoutTraveler(route,client,(a,b,c)-> {return Route.insertionDistance(a,b,c) + shift; });
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
                PointEvaluationRes  res = getAllPossiblePosOfInsertionToRoute(route,bclient,0).get(0);
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

    public static List<PointEvaluationRes> getAllPossibleDiversificationPos(Route route,ClientNode clientNode){
        float removeScore = Route.insertionDistance(clientNode,route.getPrev(clientNode),route.getNext(clientNode));
        return RoutTraveler(route,clientNode,
                (toInsert,a,b)->{
                    float t = Route.insertionDistance(toInsert,a,b) - removeScore;
                    if(t<=epsilon)
                        return Float.MAX_VALUE;
                    return t;
                });
    }

    public static float RouteMoveOneStepToDiversification(Route route,List<ClientNode> clientNodes,float limit){

        for (ClientNode cur: clientNodes) {
            List<PointEvaluationRes> allPos = getAllPossibleDiversificationPos(route,cur);
            for (PointEvaluationRes pos:allPos) {
                if(pos.score > limit  ||  getRandomInt(0,100) < 2)
                    continue;
                route.remove(cur);
                route.insert(pos.point,(ClientNode) cur);
                clientNodes.remove(cur);
                return pos.score;
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
    }

    public static Graph buildCostGraphFromRouteWithCapacity(Route route,List<Integer> capacities){
        Graph costGraph = new Graph(route.size());

        List<ClientNode> clientRoute= route.getClientsByOrder();
        for (int capacity:capacities) {
            for(int i=0;i<clientRoute.size();++i){
                ClientNode from = clientRoute.get(i);
                for(int j=i;j<clientRoute.size();++j){
                    ClientNode to = clientRoute.get(j);
                    if(route.getSubRoutDemand(from,to) > capacity)
                        break;
                    costGraph.setEdge(i,j+1,route.getSubCircleDistance(from,to));
                }
            }
        }
        return costGraph;
    }

    public static Path getRouteOptimalSubPath(Route route,List<Integer> capacities){
        Graph costGraph = buildCostGraphFromRouteWithCapacity(route,capacities);
        int depotNodeIdx = route.size()-1;
        return costGraph.getShortestPath(depotNodeIdx);
    }

    public static float evaluateSolution(Solution solution){
        float res = 0;
        for (Route route:solution.Routs.values()) {
            Path path = getRouteOptimalSubPath(route,solution.nodesManager.carCapacity);
            res += path.distance;
        }
        return res;
    }

    public static ArrayList<Route> getAllPossibleRouteAfterInsertion(Route route,ClientNode client){
        ArrayList<Route> routes = new ArrayList<>();
        for (Point cur:route.nexts.keySet()) {
            Route newer = new Route(route);
            newer.insert(cur,client);
            routes.add(newer);
        }
        return routes;
    }

    public static List<PointEvaluationRes> getAllPossiblePosOfInsertionToRouteByRealDistance(Route route,ClientNode client,List<Integer> carCapacities,float shift){
        List<PointEvaluationRes> res = new ArrayList<>();
        for (Route r:getAllPossibleRouteAfterInsertion(route,client)){
            Path newer = getRouteOptimalSubPath(r,carCapacities);
            float realDis =  newer.distance;
            PointEvaluationRes peval = new PointEvaluationRes(route,r.getPrev(client),realDis + shift);
            res.add(peval);
        }
        return res;
    }

    public static List<PointEvaluationRes> getGoodPositionsToInsertGloballyByRealDistance(Solution solution,ClientNode client){
        List<PointEvaluationRes> res = new ArrayList<>();
        for (Route route: solution.Routs.values()) {
            List<PointEvaluationRes> tmp = getAllPossiblePosOfInsertionToRouteByRealDistance(route,client,solution.nodesManager.carCapacity,0);
            res.add( Collections.min(tmp) );
        }
        Collections.sort(res);
        return res;
    }

    public static List<PointEvaluationRes> getGoodPositionsToInsertGlobally(Solution solution,ClientNode client){
        List<PointEvaluationRes> res = new ArrayList<>();
        for (Route route: solution.Routs.values()) {
            List<PointEvaluationRes> tmp = getAllPossiblePosOfInsertionToRoute(route,client,0);
            res.add( tmp.get(0) );
        }
        Collections.sort(res);
        return res;
    }

    public static float solutionMoveOneStepToLocalOptimal(Solution solution){
        float res = 0;
        for(Route route:solution.Routs.values()) {
            for (ClientNode client:route.getClientsByOrder()) {
                float removeScore = Route.insertionDistance(client,route.getPrev(client),route.getNext(client));
                route.remove(client);
                List<PointEvaluationRes> tmp = getGoodPositionsToInsertGlobally(solution,client);
                PointEvaluationRes best = tmp.get(0);
                best.route.insert(best.point,client);
                res += best.score - removeScore;
            }
        }
        System.out.println("Solution reduce:" + res);
        return res;
    }

    public static float solutionMoveOneStepToLocalOptimalByRealDistance(Solution solution){
        float res = 0;
        for(Route route:solution.Routs.values()) {
            for (ClientNode client:route.getClientsByOrder()) {
                float s1 = getRouteOptimalSubPath(route,solution.nodesManager.carCapacity).distance;
                Point recoverPoint = route.getPrev(client);
                route.remove(client);
                float s2 = getRouteOptimalSubPath(route,solution.nodesManager.carCapacity).distance;
                List<PointEvaluationRes> tmp = getGoodPositionsToInsertGloballyByRealDistance(solution,client);
                PointEvaluationRes best = tmp.get(0);
                float origScore = getRouteOptimalSubPath(best.route,solution.nodesManager.carCapacity).distance;
                float delta = best.score - origScore + s2 - s1;
                if(delta<-epsilon){
                    best.route.insert(best.point,client);
                    res += delta;
                }
                else {
                    route.insert(recoverPoint,client);
                }
            }
        }
        return res;
    }

    public static void solutionToLocalOptimal(Solution solution){
        while (solutionMoveOneStepToLocalOptimal(solution)<0);
    }

    public static void solutionToLocalOptimalByRealScore(Solution solution){
        while (solutionMoveOneStepToLocalOptimalByRealDistance(solution)<0);
    }

}
