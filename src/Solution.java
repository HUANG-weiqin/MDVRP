import javafx.util.Pair;

import java.util.*;

public class Solution {
    public Map<DepotNode,Route> Routs;
    public List<ClientNode> borderlineClientsToInsert;
    public NodesManager nodesManager;
    public Solution(NodesManager nodesManager) {
        this.nodesManager = nodesManager;
        borderlineClientsToInsert = new LinkedList<>(nodesManager.boderlineClients);

        Routs = new Hashtable<>();
        for (ClientNode client:nodesManager.nonBoderlineClientsInitDepot.keySet()) {
            DepotNode depot = nodesManager.nonBoderlineClientsInitDepot.get(client);
            if( !Routs.containsKey(depot) ){
                Routs.put(depot,new Route(depot));
            }
            Route route = Routs.get(depot);
            PointEvaluationRes res = Algo.optimalDistancePosOfInsertionToRoute(route,client);
            route.insert(res.point,client);
        }
    }

    public Solution(Solution solution) {
        this.nodesManager = solution.nodesManager;
        borderlineClientsToInsert = new LinkedList<>(solution.borderlineClientsToInsert);
        Routs = new Hashtable<>();
        for (DepotNode depot: solution.Routs.keySet()) {
            Routs.put(depot,new Route(solution.Routs.get(depot)));
        }
    }

    public float evaluatDistance(){
        float res = 0;
        for (Route rout: Routs.values()) {
            res += rout.distance;
        }
        return res;
    }

    @Override
    public String toString() {
        String res = "";
        res += "---------SOLUTION-----------\n";
        res += "Boderline clients to insert: " + borderlineClientsToInsert.size();
        res += "\n------------------------\n";
        res += "Total distance: " + evaluatDistance();
        res += "\n------------------------\n";
        for (Route rout:Routs.values()) {
            res += rout.toString();
            res += "------------------------\n";
        }
        return res;
    }

}
