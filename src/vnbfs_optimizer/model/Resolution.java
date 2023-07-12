package vnbfs_optimizer.model;

import java.util.List;

public abstract class Resolution extends Differentiable{

    protected boolean visited = false;
    @Override
    public abstract int hashCode();

    @Override
    public boolean equals(Object obj) {
        return hashCode() == obj.hashCode();
    }

    public abstract boolean betterThan(Resolution other);

    public abstract double howMuchBetterThan(Resolution other);

    public abstract boolean getVisited();
    public abstract void setVisited();

    public abstract List<? extends Resolution> getAllVoisinNonVisited();

}
