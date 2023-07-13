
import vnbfs_optimizer.Optimizer;
import vnbfs_optimizer.contaniner.DifferentiableContainer;
import vnbfs_optimizer.model.Resolution;

import java.util.ArrayList;
import java.util.List;

public class VnsBfsOptimizer extends Optimizer {

    private int maxNbVoisinToExplorer;
    private int targetLocalOptmalNb;

    public VnsBfsOptimizer(int maxNbVoisinToExplorer ,int target) {
        this.maxNbVoisinToExplorer = maxNbVoisinToExplorer;
        this.targetLocalOptmalNb = target;
    }

    private List<Resolution> getAllImproveVosin(Resolution solution){
        List<Resolution> solutions = (List<Resolution>) solution.getAllVoisin();
        List<Resolution> res = new ArrayList<>(20);
        for (Resolution resolution:solutions){
            if(resolution.betterThan(solution))
                res.add(resolution);
        }
        return res;
    }


    @Override
    public List<Resolution> toApproximateOptimalSolution(Resolution init) {
        DifferentiableContainer<Resolution> solutionHeap = new DifferentiableContainer<>();
        DifferentiableContainer<Resolution> res = new DifferentiableContainer<>();
        solutionHeap.add(init);
        while (solutionHeap.size()>0){
            Resolution cur = solutionHeap.pop();
            String tmp="score:" + cur.evaluate() + " solutionHeap:"+solutionHeap.size() + " res:"+res.size();
            if(res.size()>0)
                tmp += " bfs_local_best:"+ res.top().evaluate();
            System.out.println(tmp);
            List<Resolution> sols = getAllImproveVosin(cur);
            cur.setVisited();

            if(sols.isEmpty()){
                res.add(cur);
                if(res.size()>=targetLocalOptmalNb) return res.toArrayList();
            }
            else {
                for (Resolution s: sols) {
                    if(s.getVisited()) continue;
                    if(solutionHeap.size() < maxNbVoisinToExplorer)
                        solutionHeap.add(s);
                    else if (s.betterThan(solutionHeap.top())) {
                        solutionHeap.pop().setVisited();
                        solutionHeap.add(s);
                    }
                }
            }

            if(solutionHeap.size() == 0 && res.size()>0){
                List<Resolution> news1 = ((List<Resolution>) cur.getRandomVoisin(maxNbVoisinToExplorer));
                List<Resolution> news2 = ((List<Resolution>) res.top().getDiversifiedVoisin(maxNbVoisinToExplorer));
                solutionHeap.addAll(news1);
                solutionHeap.addAll(news2);
            }
        }
        return res.toArrayList();
    }

    @Override
    public double getComplexity(int size) {
        return 0;
    }
}
