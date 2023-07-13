import visualisation.PointPlotter;
import vnbfs_optimizer.model.Resolution;

import java.util.*;

public class Main {

    public static Solution initialisation(String filePath){
        NodesManager instance1= new NodesManager(filePath);
        Solution solution = new Solution(instance1);
        return  solution;
    }
    public static Solution borderlineInsertion(Solution solution){
        Algo.LocalOptimalBorderlineInsertion(solution);
        return solution;
    }

    public static void diversifyLocal(Route route, double limit){
        Algo.DiversificationToLimit(route,limit,1f,1.3f);
    }

    public static double percent(double start,double fin,double maxStep,double step,boolean inverse){
        if(inverse)
           return  start + fin  - fin*step/maxStep;
        return start + fin*step/maxStep;
    }

    public static double evaluator(Solution solution, boolean byRealDistance){
        if(byRealDistance)
            return Algo.evaluateSolution(solution);
        else
            return solution.evaluatDistance();
    }

    public static Solution vnsLocally(Solution solution,int maxstep,boolean byRealDistance){

        double bestDist= evaluator(solution,byRealDistance);
        Solution bestSol = solution;;
        for(int i=0;i<maxstep;++i) {
            for (Route route:solution.Routs.values()) {
                diversifyLocal(route,3 + route.distance*percent(0.05f,0.5f,maxstep,i,true));
            }

            for (Route route:solution.Routs.values()) {
                while (Algo.RouteMoveOneStepToLocalOptimal(route));
            };

            double dis = evaluator(solution,byRealDistance);
            if( dis < bestDist){
                bestDist = dis;
                bestSol = new Solution(solution);
            }
        }
        System.out.println("RealDistance: "+ byRealDistance + "  BestDis: " +bestDist);
        return bestSol;
    }

    public static void diversifyGlobal(Solution solution){
        for (Route route:solution.Routs.values()) {
            diversifyLocal(route,route.distance*0.2f);
        }
        return;
    }

    public static Solution vnsGlobally( Solution solution ){
        double reduce = Algo.solutionMoveOneStepToLocalOptimalByRealDistance(solution);
        return solution;
    }
    public static void termination(){}

    public static void visulaliserSolution(Solution solution){
        Map<DepotNode, List<Route>> res = Algo.parseSolution(solution);
        PointPlotter plotter = new PointPlotter(1000,800);
        for (DepotNode depot:res.keySet()) {

            for (Route route:res.get(depot)){
                plotter.addPointsToSet(depot.id,depot.x,depot.y);
                for (Point client:route.getClientsByOrder()){
                    plotter.addPointsToSet(depot.id,client.x,client.y);
                }
                plotter.addPointsToSet(depot.id,depot.x,depot.y);
            }
        }
        for (DepotNode depot:res.keySet()) {
            plotter.addIndependentPoints(depot.x,depot.y);
        }

        plotter.show();
    }

    public static void save(Solution solution,String path){
        Map<DepotNode, List<Route>> sols = Algo.parseSolution(solution);
        StringBuilder res = new StringBuilder();
        res.append(solution.evaluate());
        res.append('\n');
        for (DepotNode depot:sols.keySet()) {
            for (Route route:sols.get(depot)){
                res.append(depot.id);
                for (ClientNode client:route.getClientsByOrder()){
                    res.append(" ");
                    res.append(client.id);
                }
                res.append('\n');
            }
        }
        Algo.saveString(res.toString(),path);
    }

    public static Solution vnsFast(Solution solution,int iteration) {
        solution = vnsLocally(solution,10,true);
        solution = vnsGlobally(solution);
        Solution bestSolution = new Solution(solution);
        double bestPoint = Algo.evaluateSolution(bestSolution);
        for (int i=0;i<iteration;++i){
            System.out.println("---Vsn Dfs step--->"+i);
            solution = vnsLocally(solution,10,true);
            solution = vnsGlobally(solution);

            double tmpPoint = Algo.evaluateSolution(solution);
            if(tmpPoint<bestPoint){
                bestSolution = new Solution(solution);
                bestPoint = tmpPoint;
            }
            else if(tmpPoint - bestPoint > 100)
                solution = new Solution(bestSolution);
        }
        return bestSolution;
    }

    public static Solution vnsBfs(Solution solution,int large,int tagetLocalOptNb){
        VrpResolution.maxNbVoisin = (int) Math.pow(solution.nodesManager.nbClients + solution.nodesManager.nbDepots + 1,2);
        VrpResolution vrpResolution= new VrpResolution(solution);
        VnsBfsOptimizer optimizer = new VnsBfsOptimizer(large,tagetLocalOptNb);
        List<Resolution> resolutions = optimizer.toApproximateOptimalSolution(vrpResolution);
        resolutions.add(vrpResolution);
        VrpResolution res = (VrpResolution) Collections.min(resolutions);
        System.out.println(Algo.evaluateSolution(res.solution));
        return res.solution;
    }

    public static void main(String[] args) {
        String dataPath = "src\\dataSet\\p12";
        String savePath = "src\\results\\p12";
        Solution solution = initialisation(dataPath);
        solution = vnsLocally(solution,100,false);
        solution = borderlineInsertion(solution);
        Solution bestSolution = new Solution(solution);
        for (int i=0;i<20;++i){
            solution = vnsFast(solution,10);
            //Si le temp est trop long, pensez à modifier ces deux paramètres,
            solution = vnsBfs(solution,13,3);  // >=2  et >3

            double diff = solution.evaluate() - bestSolution.evaluate();
            if(diff<0){
                bestSolution = new Solution(solution);
                save(bestSolution,savePath);
                visulaliserSolution(solution); //La désactivation de l'affichage des images interrompt le programme
            }
            else if(diff > bestSolution.evaluate()*0.1){
                solution = new Solution(bestSolution);
            }

            System.out.println("------- global best: " +bestSolution.evaluate() + "-------");
        }

    }

}