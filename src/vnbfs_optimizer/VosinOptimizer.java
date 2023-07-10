package vnbfs_optimizer;

import vnbfs_optimizer.model.Resolution;

import java.util.ArrayList;
import java.util.List;

public class VosinOptimizer extends Optimizer{
    private int vType;
    public VosinOptimizer(int vType) {
        this.vType = vType;
    }

    @Override
    public List<Resolution> toApproximateOptimalSolution(Resolution init) {
        List<Resolution> solutions = init.getAllVoisin(vType);
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
        return size * vType + size + 1;
    }
}
