package vnbfs_optimizer.contaniner;
import vnbfs_optimizer.model.Differentiable;

import java.util.*;

public class DifferentiableContainer <T extends Differentiable> {
    private PriorityQueue<T> minHeap = new PriorityQueue<>();
    private HashSet<T> hashSet = new HashSet<>();

    public void add(T v){
        if(hashSet.contains(v))
            return;
        hashSet.add(v);
        minHeap.offer(v);
    }

    public T pop(){
        T res = minHeap.poll();
        hashSet.remove(res);
        return res;
    }

    public T top(){
        return minHeap.peek();
    }

    public boolean contains(T df){
        return hashSet.contains(df);
    }

    public void addAll(Collection<T> collection){
        for (T t:collection){
            add(t);
        }
    }

    public int size(){
        return hashSet.size();
    }

    public ArrayList<T> toArrayList(){
        return new ArrayList<>(minHeap);
    }
}
