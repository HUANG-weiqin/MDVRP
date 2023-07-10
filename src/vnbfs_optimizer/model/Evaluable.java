package vnbfs_optimizer.model;

public interface Evaluable extends Comparable<Evaluable>{
    double evaluate();
}
