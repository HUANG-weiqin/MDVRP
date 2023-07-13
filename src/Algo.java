import shortestPath.Graph;
import shortestPath.Path;

import java.util.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

@FunctionalInterface
interface evalFunc {
    double eval(Point a, Point b, Point c);
}

public class Algo {
    public static double borderlineFactor = 0.3;
    public static double epsilon = 0.00001;

    public static int getRandomInt(int lower, int upper) {
        return lower + (int) (Math.random() * upper);
    }

    public static List<PointEvaluationRes> RoutTraveler(Route route, ClientNode client, evalFunc evaluator) {
        List<PointEvaluationRes> res = new ArrayList<>();
        Point cur = route.depot;
        Point next = route.getNext(cur);
        do {
            double dis = evaluator.eval(client, cur, next);
            res.add(new PointEvaluationRes(route, cur, dis));
            cur = next;
            next = route.getNext(next);
        } while (cur != route.depot);

        Collections.sort(res);
        return res;
    }


    public static List<PointEvaluationRes> getAllPossiblePosOfInsertionToRoute(Route route, ClientNode client, double shift) {
        return RoutTraveler(route, client, (a, b, c) -> {
            return Route.insertionDistance(a, b, c) + shift;
        });
    }

    public static boolean RouteMoveOneStepToLocalOptimal(Route route) {

        for (ClientNode cur : route.getClientsByOrder()) {
            double removeScore = Route.insertionDistance(cur, route.getPrev(cur), route.getNext(cur));
            Route tmp = new Route(route);
            tmp.remove(cur);
            PointEvaluationRes res = RoutTraveler(tmp, cur,
                    (toInsert, a, b) -> {
                        return Route.insertionDistance(toInsert, a, b) - removeScore;
                    }
            ).get(0);
            if (res.score < -epsilon) {
                route.remove(cur);
                route.insert(res.point, cur);
                return true;
            }
        }

        return false;
    }


    public static DepotNode BorderLineTest(ClientNode client, List<DepotNode> depots) {
        if (depots.size() == 0)
            return null;
        if (depots.size() == 1)
            return depots.get(0);

        depots.sort((a, b) -> {
            return Double.compare(Point.distance(client, a), Point.distance(client, b));
        });
        DepotNode first = depots.get(0);
        DepotNode second = depots.get(1);
        double f = Point.distance(first, client) / Point.distance(first, second);
        if (f <= borderlineFactor)
            return first;

        return null;
    }

    public static void LocalOptimalBorderlineInsertion(Solution solution) {
        for (ClientNode bclient : solution.borderlineClientsToInsert) {
            Route bestRout = null;
            Point bestPoint = null;
            double bestDis = Double.MAX_VALUE;
            for (Route route : solution.Routs.values()) {
                PointEvaluationRes res = getAllPossiblePosOfInsertionToRoute(route, bclient, 0).get(0);
                if (bestDis > res.score) {
                    bestDis = res.score;
                    bestRout = route;
                    bestPoint = res.point;
                }
            }
            if (bestRout != null) {
                bestRout.insert(bestPoint, bclient);
            }
        }
        solution.borderlineClientsToInsert.clear();
    }

    public static List<PointEvaluationRes> getAllPossibleDiversificationPos(Route route, ClientNode clientNode) {
        double removeScore = Route.insertionDistance(clientNode, route.getPrev(clientNode), route.getNext(clientNode));
        return RoutTraveler(route, clientNode,
                (toInsert, a, b) -> {
                    double t = Route.insertionDistance(toInsert, a, b) - removeScore;
                    if (t <= epsilon)
                        return Double.MAX_VALUE;
                    return t;
                });
    }

    public static double RouteMoveOneStepToDiversification(Route route, List<ClientNode> clientNodes, double limit) {

        for (ClientNode cur : clientNodes) {
            List<PointEvaluationRes> allPos = getAllPossibleDiversificationPos(route, cur);
            for (PointEvaluationRes pos : allPos) {
                if (pos.score > limit || getRandomInt(0, 100) < 2)
                    continue;
                route.remove(cur);
                route.insert(pos.point, cur);
                clientNodes.remove(cur);
                return pos.score;
            }
        }
        return Double.MAX_VALUE;
    }

    public static int RoutDiff(Route r1, Route r2) {
        int res = 0;
        Point cur = r1.depot;
        do {
            if (r1.getNext(cur) != r2.getNext(cur)) {
                res += 1;
            }
            cur = r1.getNext(cur);
        } while (cur != r1.depot);
        return res;
    }

    public static int SolutionDiff(Solution r1, Solution r2) {
        int res = 0;
        for (DepotNode depot : r1.Routs.keySet()) {
            res += RoutDiff(r1.Routs.get(depot), r2.Routs.get(depot));
        }
        return res;
    }

    public static void DiversificationToLimit(Route route, double limit, double step, double factor) {
        List<ClientNode> clientNodes = route.getClientsByOrder();
        Collections.shuffle(clientNodes);
        Route ori = new Route(route);
        while (limit - step >= step) {
            double cost = RouteMoveOneStepToDiversification(route, clientNodes, step);
            if (cost > limit) {
                step *= factor;
                continue;
            }
            limit -= cost;
        }
    }

    public static Graph buildCostGraphFromRouteWithCapacity(Route route, List<Integer> capacities) {
        Graph costGraph = new Graph(route.size());

        List<ClientNode> clientRoute = route.getClientsByOrder();
        for (int capacity : capacities) {
            for (int i = 0; i < clientRoute.size(); ++i) {
                ClientNode from = clientRoute.get(i);
                for (int j = i; j < clientRoute.size(); ++j) {
                    ClientNode to = clientRoute.get(j);
                    if (route.getSubRoutDemand(from, to) > capacity)
                        break;
                    costGraph.setEdge(i, j + 1, route.getSubCircleDistance(from, to));
                }
            }
        }
        return costGraph;
    }

    public static Path getRouteOptimalSubPath(Route route, List<Integer> capacities) {
        Graph costGraph = buildCostGraphFromRouteWithCapacity(route, capacities);
        int depotNodeIdx = route.size() - 1;
        return costGraph.getShortestPath(depotNodeIdx);
    }

    public static double evaluateRoute(Route route, List<Integer> carCapacity) {
        Path path = getRouteOptimalSubPath(route, carCapacity);
        return path.distance;
    }

    public static double evaluateSolution(Solution solution) {
        double res = 0;
        for (Route route : solution.Routs.values()) {
            res += evaluateRoute(route, solution.nodesManager.carCapacity);
        }
        return res;
    }

    public static ArrayList<Route> getAllPossibleRouteAfterInsertion(Route route, ClientNode client) {
        ArrayList<Route> routes = new ArrayList<>();
        for (Point cur : route.nexts.keySet()) {
            Route newer = new Route(route);
            newer.insert(cur, client);
            routes.add(newer);
        }
        return routes;
    }

    public static List<PointEvaluationRes> getAllPossiblePosOfInsertionToRouteByRealDistance(Route route, ClientNode client, List<Integer> carCapacities, double shift) {
        List<PointEvaluationRes> res = new ArrayList<>();
        for (Route r : getAllPossibleRouteAfterInsertion(route, client)) {
            Path newer = getRouteOptimalSubPath(r, carCapacities);
            double realDis = newer.distance;
            PointEvaluationRes peval = new PointEvaluationRes(route, r.getPrev(client), realDis + shift);
            res.add(peval);
        }
        return res;
    }

    public static List<PointEvaluationRes> getGoodPositionsToInsertGloballyByRealDistance(Solution solution, ClientNode client) {
        List<PointEvaluationRes> res = new ArrayList<>();
        for (Route route : solution.Routs.values()) {
            List<PointEvaluationRes> tmp = getAllPossiblePosOfInsertionToRouteByRealDistance(route, client, solution.nodesManager.carCapacity, 0);
            res.add(Collections.min(tmp));
        }
        Collections.sort(res);
        return res;
    }

    public static List<PointEvaluationRes> getGoodPositionsToInsertGlobally(Solution solution, ClientNode client) {
        List<PointEvaluationRes> res = new ArrayList<>();
        for (Route route : solution.Routs.values()) {
            List<PointEvaluationRes> tmp = getAllPossiblePosOfInsertionToRoute(route, client, 0);
            res.add(tmp.get(0));
        }
        Collections.sort(res);
        return res;
    }

    public static double solutionMoveOneStepToLocalOptimal(Solution solution) {
        double res = 0;
        for (Route route : solution.Routs.values()) {
            for (ClientNode client : route.getClientsByOrder()) {
                double removeScore = Route.insertionDistance(client, route.getPrev(client), route.getNext(client));
                route.remove(client);
                List<PointEvaluationRes> tmp = getGoodPositionsToInsertGlobally(solution, client);
                PointEvaluationRes best = tmp.get(0);
                best.route.insert(best.point, client);
                res += best.score - removeScore;
            }
        }
        System.out.println("Solution reduce:" + res);
        return res;
    }

    public static double solutionMoveOneStepToLocalOptimalByRealDistance(Solution solution) {
        double res = 0;
        for (Route route : solution.Routs.values()) {
            for (ClientNode client : route.getClientsByOrder()) {
                double s1 = getRouteOptimalSubPath(route, solution.nodesManager.carCapacity).distance;
                Point recoverPoint = route.getPrev(client);
                route.remove(client);
                double s2 = getRouteOptimalSubPath(route, solution.nodesManager.carCapacity).distance;
                List<PointEvaluationRes> tmp = getGoodPositionsToInsertGloballyByRealDistance(solution, client);
                PointEvaluationRes best = tmp.get(0);
                double origScore = getRouteOptimalSubPath(best.route, solution.nodesManager.carCapacity).distance;
                double delta = best.score - origScore + s2 - s1;
                if (delta < -epsilon) {
                    best.route.insert(best.point, client);
                    res += delta;
                } else {
                    route.insert(recoverPoint, client);
                }
            }
        }
        return res;
    }

    public static void solutionToLocalOptimal(Solution solution) {
        while (solutionMoveOneStepToLocalOptimal(solution) < 0) ;
    }

    public static void solutionToLocalOptimalByRealScore(Solution solution) {
        while (solutionMoveOneStepToLocalOptimalByRealDistance(solution) < 0) ;
    }

    public static void RandomdiversifyOneStepSolution(Solution s){
        List<Point> points = new ArrayList<>(s.nodesManager.clients);
        Collections.shuffle(points);
        Point clientNode = points.get(0);
        points.remove(0);
        points.addAll(s.nodesManager.depots);
        Collections.shuffle(points);
        Point toInsertP = points.get(0);
        s.getRoute(clientNode).remove(clientNode);
        s.getRoute(toInsertP).insert(toInsertP,(ClientNode) clientNode);
    }

    public static Map<DepotNode, List<Route>> parseSolution(Solution solution) {
        Map<DepotNode, List<Route>> res = new Hashtable<>();

        for (DepotNode depot : solution.Routs.keySet()) {
            res.put(depot, new ArrayList<>());
            Route bigRoute = solution.Routs.get(depot);

            List<ClientNode> clients = bigRoute.getClientsByOrder();
            Path path = getRouteOptimalSubPath(bigRoute, solution.nodesManager.carCapacity);
            int prev = -1;
            for (int i : path.path) {
                if (prev >= 0) {
                    Route newSubRout = new Route(depot);
                    List<ClientNode> subPath = clients.subList(prev, i);
                    for (ClientNode c : subPath) {
                        newSubRout.insert(newSubRout.getPrev(depot), c);
                    }
                    res.get(depot).add(newSubRout);
                }
                prev = i;
            }
        }
        return res;
    }


    public static void saveString(String str, String filePath) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
