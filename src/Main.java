import java.util.Collections;

public class Main {


    public static Solution initialisation(){
        NodesManager instance1= new NodesManager("C:\\Users\\ASUS\\Desktop\\michel\\MDVRP-Instances-master\\MDVRP-Instances-master\\dat\\p05");
        System.out.println(instance1);
        Solution initSolution = new Solution(instance1);
        return  initSolution;
    }
    public static Solution vnsLocally(){return null;}
    public static Solution borderlineInsertion(Solution solution){
        Solution bestSol = new  Solution(solution);
        Algo.LocalOptimalBorderlineInsertion(bestSol);
        float bestDis = bestSol.evaluatDistance();
        for(int i=0;i<10;++i){
            Solution tmp = new Solution(solution);
            Collections.shuffle(tmp.borderlineClientsToInsert);
            Algo.LocalOptimalBorderlineInsertion(tmp);
            float dis = tmp.evaluatDistance();
            if(dis < bestDis){
                bestSol = tmp;
                bestDis = dis;
            }
        }
        Algo.SolutionToLocalOptimal(bestSol);
        return bestSol;
    }
    public static void vnsGlobally(){}
    public static void diversify(){}
    public static void termination(){}
    public static void main(String[] args) {
        Solution solution = initialisation();
        System.out.println(solution);
        solution = borderlineInsertion(solution);
        System.out.println(solution);

    }
}