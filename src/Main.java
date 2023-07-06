import java.util.Collections;

public class Main {


    public static Solution initialisation(){
        NodesManager instance1= new NodesManager("C:\\Users\\11053\\Desktop\\michel\\mdvrp\\evovrp-master\\datasets\\C-mdvrp\\p05");
        System.out.println(instance1);
        Solution initSolution = new Solution(instance1);
        return  initSolution;
    }
    public static Solution vnsLocally(){return null;}
    public static Solution borderlineInsertion(Solution solution){
        Solution bestSol = new  Solution(solution);
        Algo.LocalOptimalBorderlineInsertion(bestSol);
        float bestDis = bestSol.evaluatDistance();
        for(int i=0;i<10000;++i){
            Solution tmp = new Solution(solution);
            Collections.shuffle(tmp.borderlineClientsToInsert);
            Algo.LocalOptimalBorderlineInsertion(tmp);
            float dis = tmp.evaluatDistance();
            if(dis < bestDis){
                bestSol = tmp;
                bestDis = dis;
            }
        }

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