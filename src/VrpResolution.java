import vnbfs_optimizer.model.Resolution;
import vnbfs_optimizer.model.Voisinable;

import java.util.*;

public class VrpResolution extends Resolution {
    private static Map<Integer,Double> scores = new Hashtable<>();
    private Solution solution;

    private double score = 0;

    private boolean visited =false;
    public VrpResolution(Solution solution) {
        this.solution = solution;
        hashCodeCache = solution.hashCode();
        visited = scores.containsKey(this.hashCode());
        if (!visited){
            score = Algo.evaluateSolution(solution);
            scores.put(this.hashCode(),score);
        }
        else
            score = scores.get(this);

    }

    private int hashCodeCache = 0;

    private static int maxNbGenerate = 10000;
    private static double maxCost = 30000;

    public List<VrpResolution> voisin_insertion(){
        List<VrpResolution> res = new ArrayList<>(maxNbGenerate);
        for (ClientNode client:solution.nodesManager.clients) {
            Solution newSol = new Solution(solution);
            Route rt = newSol.getRoute(client);
            rt.remove(client);

            for (Route route:newSol.Routs.values()){
                Point cur = route.depot;
                do {
                    Solution toAddSol = new Solution(newSol);
                    Route toAddRoute = toAddSol.Routs.get(route.depot);
                    toAddRoute.insert(cur,client);
                    toAddRoute.getClientsByOrder();
                    if(!scores.containsKey(toAddSol.hashCode())){
                        res.add(new VrpResolution(toAddSol));
                    }
                    cur = route.getNext(cur);
                }while (cur!=route.depot);
            }
        }
        return res;
    }

    public List<VrpResolution> voisin_interchange(){
        return voisin_insertion();
    }

    @Override
    public double evaluate() {
        return score;
    }

    @Override
    public int hashCode() {
        return hashCodeCache;
    }

    @Override
    public boolean betterThan(Resolution other) {
        return evaluate() < other.evaluate();
    }

    @Override
    public double howMuchBetterThan(Resolution other) {
        return other.evaluate() - other.evaluate();
    }

    @Override
    public <T> List<T> getAllVoisin(int type) {
        System.out.println("hashsetSize:" + scores.size());
        //scores.clear();
        List<T> res = new ArrayList<>();
        if(visited) return res;

        Collections.shuffle(solution.nodesManager.clients);
        if(type == 1){
            return (List<T>) voisin_insertion();
        }
        else {
            return (List<T>) voisin_interchange();
        }
    }

    @Override
    public int distance(Voisinable other) {
        return 0;
    }
}
