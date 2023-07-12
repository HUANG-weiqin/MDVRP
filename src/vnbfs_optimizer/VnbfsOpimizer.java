package vnbfs_optimizer;

import vnbfs_optimizer.contaniner.DifferentiableContainer;
import vnbfs_optimizer.model.Resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class VnbfsOpimizer extends Optimizer {

    private int maxNbVoisinToExplorer;

    private VosinOptimizer vosinOptimizer;
    private OptDiversificationOptimizer optDiversificationOptimizer;

    private int targetLocalOptmalNb;

    public VnbfsOpimizer(int maxNbVoisinToExplorer, int maxVoisinType ,int target) {
        this.maxNbVoisinToExplorer = maxNbVoisinToExplorer;
        this.targetLocalOptmalNb = target;
        vosinOptimizer = new VosinOptimizer();
        optDiversificationOptimizer = new OptDiversificationOptimizer(10,50);
    }

    private List<Resolution> getAllImproveVosin(Resolution solution){
        List<Resolution> returnSol = new LinkedList<>();
        returnSol.addAll(vosinOptimizer.toApproximateOptimalSolution(solution));
        return returnSol;
    }

    @Override
    public List<Resolution> toApproximateOptimalSolution(Resolution init) {
        DifferentiableContainer<Resolution> solutionHeap = new DifferentiableContainer<>();
        DifferentiableContainer<Resolution> res = new DifferentiableContainer<>();
        solutionHeap.add(init);
        while (true){
            Resolution cur = solutionHeap.pop();
            if(cur.getVisited())continue;
            cur.setVisited();
            System.out.println("score:" + cur.evaluate() + " solutionHeap:"+solutionHeap.size() + " res:"+res.size());
            List<Resolution> sols = getAllImproveVosin(cur);

            if(sols.isEmpty()){
                res.add(cur);
                if(res.size()>=targetLocalOptmalNb) return res.toArrayList();
            }
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

            if(solutionHeap.size()==0){
                solutionHeap.addAll(optDiversificationOptimizer.toApproximateOptimalSolution(res.top()));
            }
        }

    }

    @Override
    public double getComplexity(int size) {
        return 0;
    }
}
