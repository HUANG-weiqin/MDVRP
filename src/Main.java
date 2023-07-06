public class Main {


    public static Solution initialisation(){
        NodesManager instance1= new NodesManager("C:\\Users\\11053\\Desktop\\michel\\mdvrp\\evovrp-master\\datasets\\C-mdvrp\\p05");
        System.out.println(instance1);
        Solution initSolution = new Solution(instance1);
        return  initSolution;
    }
    public static Solution vnsLocally(){return null;}
    public static Solution borderlineInsertion(Solution solution){
        Algo.LocalOptimalBorderlineInsertion(solution);
        return solution;
    }
    public static void vnsGlobally(){}
    public static void diversify(){}
    public static void termination(){}
    public static void main(String[] args) {
        Solution solution = initialisation();
        System.out.println(solution);
        borderlineInsertion(solution);
        System.out.println(solution);

    }
}