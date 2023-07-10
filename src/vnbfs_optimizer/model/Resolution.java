package vnbfs_optimizer.model;

public abstract class Resolution extends Differentiable{
    @Override
    public abstract int hashCode();

    @Override
    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }

    public abstract boolean betterThan(Resolution other);

    public abstract double howMuchBetterThan(Resolution other);

}
