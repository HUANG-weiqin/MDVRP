package visualisation;

import javafx.util.Pair;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.*;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class PointPlotter extends JPanel {

    class Point{
        float x;
        float y;
        Point (float x,float y) {
            this.x = x;
            this.y = y;
        }
    }

    class Window{
        float minx=Float.MAX_VALUE,miny=Float.MAX_VALUE,maxx=Float.MIN_VALUE,maxy=Float.MIN_VALUE;
        int marginw=10;
        int marginh=10;
        public void convertPoints(List<Point> ps){
            for (Point p:ps){
                minx = Math.min(p.x,minx);
                miny = Math.min(p.y,miny);
                maxx = Math.max(p.x,maxx);
                maxy = Math.max(p.y,maxy);
            }
            for (Point p:ps){
                p.x = (p.x-minx) / (maxx-minx) * (w-10*marginw) + 2*marginw;
                p.y = (p.y-miny) / (maxy-miny) * (h-10*marginh) +  2*marginh;
            }
        }
    }
    int w,h;

    Map<Integer,List<Point>> pointSet;
    List<Point> independentPoints;
    public PointPlotter(int w,int h){
        independentPoints = new ArrayList<>();
        pointSet = new Hashtable<>();
        this.w = w;
        this.h = h;
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g;
        int radius = 5;

        for (List<Point> points:pointSet.values()){
            Point prevPoint = null;
            g2d.setColor(Color.red);
            for (Point point : points) {
                int x = (int) point.x;
                int y = (int) point.y;
                if (prevPoint != null) {
                    int prevX =  (int) prevPoint.x;
                    int prevY =  (int) prevPoint.y;
                    g2d.drawLine(prevX, prevY, x, y);
                }
                prevPoint = point;
            }
            g2d.setColor(Color.black);
            for (Point p: points) {
                g2d.fillOval((int) p.x-radius, (int) p.y-radius, 2 * radius, 2 * radius);
            }
        }


        radius = 8;
        g2d.setColor(Color.blue);
        for (Point p: independentPoints) {
            g2d.fillOval((int) p.x-radius, (int) p.y-radius, 2 * radius, 2 * radius);
        }
    }

    public void addPointsToSet(int key,float x,float y){
        if(!pointSet.containsKey(key))
            pointSet.put(key,new ArrayList<>());
        pointSet.get(key).add(new Point(x,y));
    }

    public void addIndependentPoints(float x,float y){
        independentPoints.add(new Point(x,y));
    }

    public List<Point> getAllPoint(){
        List<Point> res = new ArrayList<>();
        for (List<Point> p:pointSet.values()) {
            res.addAll(p);
        }
        res.addAll(independentPoints);
        return res;
    }

    public void show(){
        Window window = new Window();
        window.convertPoints(getAllPoint());
        JFrame frame = new JFrame("Points");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.setSize(w, h);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}


