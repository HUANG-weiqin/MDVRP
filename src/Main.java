import java.util.Collections;

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

    public static void diversifyLocal(Route route,float limit){
        Algo.DiversificationToLimit(route,limit,1f,1.3f);
    }

    public static float percent(float start,float fin,float maxStep,float step,boolean inverse){
        if(inverse)
           return  start + fin  - fin*step/maxStep;
        return start + fin*step/maxStep;
    }

    public static float evaluator(Solution solution,boolean byRealDistance){
        if(byRealDistance)
            return Algo.evaluateSolution(solution);
        else
            return solution.evaluatDistance();
    }

    public static Solution vnsLocally(Solution solution,int maxstep,boolean byRealDistance){

        float bestDist= evaluator(solution,byRealDistance);
        Solution bestSol = solution;;
        for(int i=0;i<maxstep;++i) {
            for (Route route:solution.Routs.values()) {
                diversifyLocal(route,3 + route.distance*percent(0.05f,0.5f,maxstep,i,true));
            }

            for (Route route:solution.Routs.values()) {
                while (Algo.RouteMoveOneStepToLocalOptimal(route));
            };

            float dis = evaluator(solution,byRealDistance);
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
        float reduce = Algo.solutionMoveOneStepToLocalOptimalByRealDistance(solution);
        return solution;
    }
    public static void termination(){}

    public static void main(String[] args) {
        Solution solution = initialisation("src\\dataSet\\p15");
        solution = vnsLocally(solution,100,false);
        solution = borderlineInsertion(solution);
        solution = vnsLocally(solution,100,false);

        Solution bestSolution = new Solution(solution);
        float bestPoint = Algo.evaluateSolution(bestSolution);
        for (int i=0;i<50;++i){
            System.out.println("---step--->"+i);
            solution = vnsLocally(solution,30,true);
            solution = vnsGlobally(solution);
            float tmpPoint = Algo.evaluateSolution(solution);
            if(tmpPoint<=bestPoint){
                bestSolution = new Solution(solution);
                bestPoint = tmpPoint;
            }
            else if(bestPoint - tmpPoint > 50)
                solution = new Solution(bestSolution);
        }

        System.out.println("BEST RESULT ------------> "+ bestPoint);
    }
}