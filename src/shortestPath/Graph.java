package shortestPath;


import java.util.*;

public class Graph {
    float[][] matrix;
    int nbNode;

    Map<Integer,Path> shortesPath = new Hashtable<>();

    public Graph(int nbNode){
        matrix = new float[nbNode][nbNode];
        this.nbNode = nbNode;

        for(int i=0;i<nbNode;++i){
            for (int j=0;j<nbNode;++j){
                matrix[i][j] = Float.MIN_VALUE;
            }
        }
    }
    public float getEdgeWeight(int from, int to){
        if(from<0)
            return 0;
        return matrix[from][to];
    }

    public void setEdge(int from, int to, float weight){
        matrix[from][to] = weight;
    }

    public ArrayList<Integer> getPrevs(int j){
        ArrayList<Integer> res = new ArrayList<>();
        for (int i=0;i<nbNode;++i){
            if(matrix[i][j] != Float.MIN_VALUE){
                res.add(i);
            }
        }
        return res;
    }

    public ArrayList<Integer> getNexts(int i){
        ArrayList<Integer> res = new ArrayList<>();
        for (int j=0;j<nbNode;++j){
            if(matrix[i][j] != Integer.MIN_VALUE){
                res.add(i);
            }
        }
        return res;
    }

    public Path getShortestPath(int to){
        if(shortesPath.containsKey(to))
            return shortesPath.get(to);
        ArrayList<Path> prevPaths = new ArrayList<>();
        for (int prev:getPrevs(to)){
            prevPaths.add(getShortestPath(prev));
        }
        if(prevPaths.size()==0){
            Path best = new Path(to);
            shortesPath.put(to,best);
            return best;
        }
        Path bestPrev = Collections.min(prevPaths);
        Path best = new Path(bestPrev);
        best.addNextPoint(to, getEdgeWeight(best.getEndPoint(),to) );
        shortesPath.put(to,best);
        return best;
    }
}
