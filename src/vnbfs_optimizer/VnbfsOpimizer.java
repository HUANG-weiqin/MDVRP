package vnbfs_optimizer;

import vnbfs_optimizer.contaniner.DifferentiableContainer;
import vnbfs_optimizer.model.Resolution;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class VnbfsOpimizer extends Optimizer {

    private int maxNbVoisinToExplorer;

    private ArrayList<VosinOptimizer> vosinOptimizers;

    public VnbfsOpimizer(int maxNbVoisinToExplorer, int maxVoisinType) {
        this.maxNbVoisinToExplorer = maxNbVoisinToExplorer;
        vosinOptimizers = new ArrayList<>(maxVoisinType);
        for (int i=1;i<=maxVoisinType;++i){
            vosinOptimizers.add(new VosinOptimizer(i));
        }
    }

    private List<Resolution> getAllImproveVosin(Resolution solution){
        List<Resolution> returnSol = new LinkedList<>();
        for (VosinOptimizer optimizer:vosinOptimizers) {
            returnSol.addAll(optimizer.toApproximateOptimalSolution(solution));
        }
        return returnSol;
    }

    @Override
    public List<Resolution> toApproximateOptimalSolution(Resolution init) {
        DifferentiableContainer<Resolution> solutionHeap = new DifferentiableContainer<>();
        DifferentiableContainer<Resolution> res = new DifferentiableContainer<>();
        solutionHeap.add(init);
        do {
            Resolution cur = solutionHeap.pop();
            System.out.println("score:" + cur.evaluate() + " solutionHeap:"+solutionHeap.size() + " res:"+res.size());
            List<Resolution> sols = getAllImproveVosin(cur);

            if(sols.isEmpty()) res.add(cur);
            else {
                for (Resolution s: sols) {
                    if(solutionHeap.size()< maxNbVoisinToExplorer)
                        solutionHeap.add(s);
                    else if (s.betterThan(solutionHeap.top())) {
                        solutionHeap.pop();
                        solutionHeap.add(s);
                    }
                }
            }
        }while (solutionHeap.size()>0);

        return res.toArrayList();
    }

    @Override
    public double getComplexity(int size) {
        return 0;
    }
}
