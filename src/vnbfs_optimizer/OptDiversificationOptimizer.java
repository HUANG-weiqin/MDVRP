package vnbfs_optimizer;

import vnbfs_optimizer.model.Resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptDiversificationOptimizer extends Optimizer {

    private int nbToGenerate;
    private double cost;

    public OptDiversificationOptimizer(int nbToGenerate,double cost) {
        this.nbToGenerate = nbToGenerate;
        this.cost = cost;
    }

    @Override
    public List<Resolution> toApproximateOptimalSolution(Resolution init) {
        List<Resolution> res = new ArrayList<>(nbToGenerate);
        Resolution v = (Resolution) init.getRandomVoisin(1).get(0);
        Resolution cur = v;
        while (true){
            cur = (Resolution) cur.getRandomVoisin(1).get(0);
            double score = cur.evaluate();
            if(score<=cost){
                res.add(cur);
                if(res.size()>=nbToGenerate)break;
            }
            else
                cur = v;
        }
        return res;
    }


    @Override
    public double getComplexity(int size) {
        return size*size/10;
    }
}
