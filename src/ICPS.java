import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class ICPS {
    private ArrayList<Point2D> points = new ArrayList<>();
    private ArrayList<Point2D> spline = new ArrayList<>();
    private ArrayList<Path2D>  paths = new ArrayList<>();

    public ICPS(ArrayList<Point2D> points) {
        this.points = points;
    }

    public void clear() {
        spline.clear();
        paths.clear();
    }

    public ArrayList<Path2D> getPaths() {
        return paths;
    }

    public void generateCurve(ArrayList<Point2D> points) {
        for (int p = 0; p < points.size() - 1; p++) {
            spline.addAll(getPoints(points.get(p),  points.get(p + 1)));
        }

        generatePaths();
    }

    public void generatePaths() {

    }

    public ArrayList<Point2D> getPoints(Point2D p1, Point2D p2) {
        return null;
    }

    private long binomialCoefficient(long n, long i) {
        //n!/i!(n - i)!
        return (factorial(n)/(factorial(i)* factorial(n - i)));
    }

    private long factorial(long i) {
        if (i <= 1) {
            return 1;
        } else {
            return i * (factorial(i - 1));
        }
    }
}
