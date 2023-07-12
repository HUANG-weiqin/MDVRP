import visualisation.PointPlotter;
import vnbfs_optimizer.VnbfsOpimizer;
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

    public static Solution vnsFast(Solution solution) {
        solution = vnsLocally(solution,100,false);
        solution = borderlineInsertion(solution);
        solution = vnsLocally(solution,10,true);
        solution = vnsGlobally(solution);

        Solution bestSolution = new Solution(solution);
        double bestPoint = Algo.evaluateSolution(bestSolution);
        for (int i=0;i<1;++i){
            System.out.println("---step--->"+i);
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

        System.out.println("BEST RESULT ------------> "+ bestPoint);
        return bestSolution;
    }

    public static void main(String[] args) {
        Solution solution = initialisation("src\\dataSet\\p17");
        solution = vnsFast(solution);
        /*
        VrpResolution vrpResolution= new VrpResolution(solution);
        VnbfsOpimizer opimizer = new VnbfsOpimizer(100,1,100);
        List<Resolution> resolutions = opimizer.toApproximateOptimalSolution(vrpResolution);
        VrpResolution res = (VrpResolution) Collections.min(resolutions);
        visulaliserSolution(res.solution);
        Set<Resolution> st = new HashSet<>(resolutions);
        System.out.println(st.size());
        System.out.println(Algo.evaluateSolution(res.solution));
        */
    }

    public static void maintest(String[] args) {
        Solution solution = initialisation("src\\dataSet\\p01");
        solution = vnsLocally(solution,10,false);
        solution = borderlineInsertion(solution);

    }
}