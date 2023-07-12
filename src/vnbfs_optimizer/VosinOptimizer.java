package vnbfs_optimizer;

import vnbfs_optimizer.model.Resolution;

import java.util.ArrayList;
import java.util.List;

public class VosinOptimizer extends Optimizer{

    @Override
    public List<Resolution> toApproximateOptimalSolution(Resolution init) {
        List<Resolution> solutions = (List<Resolution>) init.getAllVoisinNonVisited();
        List<Resolution> res = new ArrayList<>();
        for (Resolution s:solutions) {
            if(s.betterThan(init)){
                res.add(s);
            }
        }
        return res;
    }

    @Override
    public double getComplexity(int size) {
        return size + size + 1;
    }
}
