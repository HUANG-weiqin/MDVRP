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
            route.insert(depot,client);
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

    public double evaluatDistance(){
        double res = 0;
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

    public Route getRoute(Point point){
        for (Route route:Routs.values()) {
            if(route.nexts.containsKey(point)){
                return route;
            }
        }
        return null;
    }

    @Override
    public int hashCode() {
        return Routs.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Solution){
            Solution other = (Solution) obj;
            for (DepotNode depot: Routs.keySet()) {
                if(!Routs.get(depot).equals(other.Routs.get(depot)))
                    return false;
            }
            return true;
        }
        return false;
    }

    public double evaluate(){
        double res = 0;
        for (Route route:Routs.values()) {
            res += route.evaluate(nodesManager.carCapacity);
        }
        return res;
    }
}
