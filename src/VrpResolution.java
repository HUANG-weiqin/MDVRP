import vnbfs_optimizer.model.Resolution;
import vnbfs_optimizer.model.Voisinable;

import java.util.*;

public class VrpResolution extends Resolution {
    private static ResolutionCache<Integer,Double> scores = new ResolutionCache<>(1000000);
    private static HashSet<Integer> visitedResolutions = new HashSet<>();
    public Solution solution;

    private double score = 0;
    public static int maxNbVoisin = 10000;

    public VrpResolution(Solution solution) {
        this.solution = solution;
        hashCodeCache = solution.hashCode();
    }

    private int hashCodeCache = 0;

    public List<VrpResolution> voisin_insertion(){
        List<VrpResolution> res = new ArrayList<>(maxNbVoisin);
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
        score = solution.evaluate();
        return score;
    }

    public double evalByCache(){
        boolean calculated = scores.containsKey(this.hashCode());
        if (!calculated){
            score = solution.evaluate();
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
        List<VrpResolution> res = new ArrayList<>(solution.nodesManager.clients.size());
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
        //System.out.println("hashsetSize:" + scores.size());
        return voisin_insertion();
    }

    @Override
    public List<VrpResolution> getRandomVoisin(int nb) {
        List<VrpResolution> res = new ArrayList<>(nb);
        for (int i=0;i<nb;++i)
            res.add(new VrpResolution( new Solution(solution) ));
        Collections.sort(res);
        res = res.subList(0,nb);
        for (VrpResolution v:res){
            for (int i=0;i<Algo.getRandomInt(2,4);++i){
                Algo.RandomdiversifyOneStepSolution(v.solution);
            }
        }
        return res;
    }

    @Override
    public List<? extends Voisinable> getDiversifiedVoisin(int nb) {
        List<VrpResolution> res = new ArrayList<>(nb);
        for(int i=0;i<nb;++i){
            Solution newer = new Solution(solution);
            for (Route route:newer.Routs.values()){
                Algo.DiversificationToLimit(route, 10 + route.distance * 0.05 ,1,1.3);
            }
            res.add(new VrpResolution(newer));
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
