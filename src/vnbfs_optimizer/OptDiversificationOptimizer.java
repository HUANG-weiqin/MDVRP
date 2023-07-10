package vnbfs_optimizer;

import vnbfs_optimizer.model.Resolution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class OptDiversificationOptimizer extends Optimizer {
    private double augmentCostLimit;
    private int nbToGenerate;
    private int deep;

    public OptDiversificationOptimizer(double augmentCostLimit, int nbToGenerate,int deep) {
        this.augmentCostLimit = augmentCostLimit;
        this.nbToGenerate = nbToGenerate;
        this.deep = deep;
    }

    @Override
    public List<Resolution> toApproximateOptimalSolution(Resolution init) {
        return exec(init,augmentCostLimit,nbToGenerate,deep);
    }

    public List<Resolution> exec(Resolution init, double aug, int nb, int deep){
        List<Resolution> res = new ArrayList<>();
        if(deep==0)return res;
        List<Resolution> voisin = init.getAllVoisin(1);
        Collections.sort(voisin);
        for (Resolution v:voisin) {
            double score = -v.howMuchBetterThan(init);
            if(score <= 0) continue;
            if(score > augmentCostLimit) break;
            List<Resolution> tmp = exec(v, augmentCostLimit - score, nbToGenerate-res.size(), deep-1);
            if(tmp.size()==0)
                res.add(v);
            else
                res.addAll(tmp);
            if(res.size()>=nbToGenerate) break;
        }
        return res;
    }

    @Override
    public double getComplexity(int size) {
        return size*size/10;
    }
}
