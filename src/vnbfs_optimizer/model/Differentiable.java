package vnbfs_optimizer.model;

public abstract class Differentiable implements Voisinable,Evaluable{
    @Override
    public int compareTo(Evaluable o) {
        return Double.compare(evaluate(),o.evaluate());
    }
}
