import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Bezier {
    private ArrayList<Point2D> curve = new ArrayList<>();
    private Path2D path = new Path2D.Double();
    private Point2D point = null;

    public Point2D getPoint() {
        return point;
    }

    public void setPoint(Point2D point) {
        this.point = point;
    }

    public ArrayList<Point2D> getCurve() {
        return curve;
    }

    public void clear() {
        curve.clear();
        path.reset();
    }

    public void generateCurve(ArrayList<Point2D> points) {
        curve.clear();

        for (double u = 0; u <= 1; u += 0.0001) {
            curve.add(formula(points, u, points.size() - 1));
        }

        generatePath();
    }

    private Point2D formula(ArrayList<Point2D> points, double u, double n) {
        Point2D point = new Point2D.Double();
        double coefficient;
        double x = 0;
        double y = 0;

        for (int i = 0; i <= n; i++) {
            coefficient = binomialCoefficient(n, i) * Math.pow(u, i) * Math.pow(1 - u, n - i);
            x += coefficient * points.get(i).getX();
            y += coefficient * points.get(i).getY();
        }

        point.setLocation(x, y);

        return point;
    }

    private double binomialCoefficient(double n, double i) {
        //n!/i!(n - i)!
        return (factorial(n)/(factorial(i)* factorial(n - i)));
    }

    private double factorial(double i) {
        if (i <= 1) {
            return 1;
        } else {
            return i * (factorial(i - 1));
        }
    }

    public void generatePath() {
        for (int i = 0; i < curve.size(); i++) {
            Point2D point = getCurve().get(i);

            if (i == 0) {
                path.moveTo(point.getX(), point.getY());
            }

            path.lineTo(point.getX(), point.getY());
        }
    }

    public Path2D getPath() {
        return path;
    }
}
