import java.awt.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class Bezier {
    private ArrayList<Point2D> curve = new ArrayList<>();
    private Path2D path = new Path2D.Double();
    private Point2D point = null;
    private Path2D tangent = new Path2D.Double();
    private Path2D normal = new Path2D.Double();

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
        tangent.reset();
        normal.reset();
    }

    public void generateCurve(ArrayList<Point2D> points) {
        curve.clear();

        for (double u = 0; u <= 1; u += 0.01) {
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

    public Point2D derivative(double u) {
        Point2D point = new Point2D.Double();
        double coefficient;
        double x = 0;
        double y = 0;
        double n = curve.size();

        for (int i = 0; i <= (n - 2); i++) {
            coefficient = n * binomialCoefficient(n - 1, i) * Math.pow(u, i) * Math.pow(1 - u, n - 1 - i);
            x += coefficient * (curve.get(i + 1).getX() - curve.get(i).getX());
            y += coefficient * (curve.get(i + 1).getY() - curve.get(i).getY());
        }

        System.out.println(x);
        System.out.println(y);
        point.setLocation(x, y);

        return point;
    }

    public void generateTangent(Point2D vector) {
        Point2D prev = new Point2D.Double();
        Point2D next = new Point2D.Double();

        prev.setLocation(point.getX() - vector.getX(), point.getY() - vector.getY());
        next.setLocation(point.getX() + vector.getX(), point.getY() + vector.getY());

        tangent.moveTo(prev.getX(), prev.getY());
        tangent.lineTo(point.getX(), point.getY());
        tangent.lineTo(next.getX(), next.getY());
    }

    public void resetTangent() {
        tangent.reset();
    }

    public Path2D getTangent() {
        return tangent;
    }

    public void generateNormal(Point2D vector) {
        vector.setLocation(-1 * vector.getY(), vector.getX());

        Point2D prev = new Point2D.Double();
        Point2D next = new Point2D.Double();

        prev.setLocation(point.getX() - vector.getX(), point.getY() - vector.getY());
        next.setLocation(point.getX() + vector.getX(), point.getY() + vector.getY());

        normal.moveTo(prev.getX(), prev.getY());
        normal.lineTo(point.getX(), point.getY());
        normal.lineTo(next.getX(), next.getY());
    }

    public void resetNormal() {
        normal.reset();
    }

    public Path2D getNormal() {
        return normal;
    }

    private double binomialCoefficient(double n, double i) {
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
