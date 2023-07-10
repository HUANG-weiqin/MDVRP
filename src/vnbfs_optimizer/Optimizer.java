package vnbfs_optimizer;

import vnbfs_optimizer.model.Resolution;
import vnbfs_optimizer.parallel.CalculationChainNodes;

import java.util.List;

public abstract class Optimizer extends CalculationChainNodes{
    protected List<Resolution> results;
    public abstract List<Resolution> toApproximateOptimalSolution(Resolution init);

}
