import vnbfs_optimizer.model.Resolution;
import vnbfs_optimizer.model.Voisinable;

import java.util.*;

public class VrpResolution extends Resolution {
    private static ResolutionCache<Integer,Double> scores = new ResolutionCache<>(1000000);
    private static HashSet<Integer> visitedResolutions = new HashSet<>();
    public Solution solution;

    private double score = 0;

    public VrpResolution(Solution solution) {
        this.solution = solution;
        hashCodeCache = solution.hashCode();
    }

    private int hashCodeCache = 0;
    private static int maxNbGenerate = 30000;

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
                    res.add(new VrpResolution(toAddSol));
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
        boolean calculated = scores.containsKey(this.hashCode());
        if (!calculated){
            score = Algo.evaluateSolution(solution);
            scores.put(this.hashCode(),score);
        }
        else
            score = scores.get(this.hashCode());
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
        return other.evaluate() - evaluate();
    }

    @Override
    public boolean getVisited() {
        return visitedResolutions.contains(hashCode());
    }

    @Override
    public void setVisited() {
        visitedResolutions.add(hashCode());
        visited = true;
    }

    @Override
    public List<VrpResolution> getAllVoisinNonVisited() {
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
                    if(getVisited())
                        res.add(new VrpResolution(toAddSol));
                    cur = route.getNext(cur);
                }while (cur!=route.depot);
            }
        }
        return res;
    }

    @Override
    public List<VrpResolution> getAllVoisin() {
        System.out.println("hashsetSize:" + scores.size());
        return voisin_insertion();
    }

    @Override
    public List<VrpResolution> getRandomVoisin(int nb) {
        List<Point> rands = new ArrayList<>(solution.nodesManager.clients);
        int idx = Algo.getRandomInt(0,solution.nodesManager.clients.size());
        ClientNode clientNode = solution.nodesManager.clients.get(idx);
        rands.remove(idx);

        Solution solution1 = new Solution(solution);
        Route route = solution1.getRoute(clientNode);
        route.remove(clientNode);
        rands.addAll(solution.nodesManager.depots);
        Collections.shuffle(rands);

        List<VrpResolution> res = new ArrayList<>(nb);
        for (Point p:rands) {
            Route route1 = solution1.getRoute(p);
            route1.insert(p,clientNode);
            res.add(new VrpResolution(solution1));
            if(res.size()>=nb)
                return res;
            solution1 = new Solution(solution);
        }
        return res;
    }

    @Override
    public int distance(Voisinable other) {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof VrpResolution){
            VrpResolution other = (VrpResolution) obj;
            return solution.equals(other.solution);
        }
        return false;
    }
}
