import java.util.Collections;

public class Main {


    public static Solution initialisation(){
        NodesManager instance1= new NodesManager("C:\\Users\\11053\\Desktop\\michel\\mdvrp\\evovrp-master\\datasets\\C-mdvrp\\pr04");
        Solution solution = new Solution(instance1);
        return  solution;
    }
    public static Solution vnsLocally(Solution solution){
        for (Route route:solution.Routs.values()) {
            while (Algo.RouteMoveOneStepToLocalOptimal(route));
        };
        return solution;
    }
    public static Solution borderlineInsertion(Solution solution){
        Algo.LocalOptimalBorderlineInsertion(solution);
        return solution;
    }
    public static void vnsGlobally(){}
    public static void diversifyLocal(Route route,float limit){
        Algo.DiversificationToLimit(route,limit,1f,1.3f);
    }
    public static Solution diversifyGlobal(Solution solution){
        return null;
    }
    public static void termination(){}

    public static float percent(float start,float fin,float maxStep,float step,boolean inverse){
        if(inverse)
           return  start + fin  - fin*step/maxStep;
        return start + fin*step/maxStep;
    }
    public static void main(String[] args) {
        Solution solution = initialisation();
        solution = borderlineInsertion(solution);

        float bestDist= solution.evaluatDistance();
        Solution bestSol = solution;
        int maxstep = 1000;
        for(int i=0;i<maxstep;++i) {
            for (Route route:solution.Routs.values()) {
                diversifyLocal(route,3 + route.distance*percent(0,0.25f,maxstep,i,true));

            }
            System.out.println("soldiff:"+Algo.SolutionDiff(solution,bestSol));

            solution = vnsLocally(solution);
            if(bestDist > solution.evaluatDistance()){
                bestDist = solution.evaluatDistance();
                bestSol = new Solution(solution);
            }
            System.out.println("------step: "+i + " cur-score: " + solution.evaluatDistance() + " best-score: " + bestDist);
        }
        System.out.println("\n*******************\n");
        System.out.println("best: " + bestSol.evaluatDistance());
        System.out.println("real distance: " + Algo.evaluateSolution(bestSol));
    }
}