package vnbfs_optimizer.parallel;

import java.util.ArrayList;
import java.util.List;

public abstract class CalculationChainNodes{
    protected List<CalculationChainNodes> dependency = new ArrayList<>();
    public abstract double getComplexity(int size);
}
