import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class BezierSpline {
    private final int CUBIC = 4;
    Bezier bezier = new Bezier();
    private ArrayList<Point2D> spline = new ArrayList<>();
    private ArrayList<Path2D>  paths = new ArrayList<>();


    public void clear() {
        spline.clear();
        paths.clear();
    }

    public void generateCurve(ArrayList<Point2D> points) {
        //If point, line, quadratic, or cubic, call superclass
        if (points.size() <= CUBIC) {
            bezier.generateCurve(points);
            spline = bezier.getCurve();
            return;
        }

        int controlPoint = 3;

        //Adds the required number of control points
        for (int i = 0; i < points.size(); i++) {
            if (i == controlPoint) {
                controlPoint += 3;
                points.add(i, generateControlPoint(points.get(i - 1), points.get(i)));
            }
        }

        this.spline = points;
        System.out.println("Points: " + points.size());
    }

    public void generatePaths() {
        if (spline.size() <= CUBIC) {
            paths.add(bezier.getPath());
        }

        Bezier bezier = new Bezier();
        ArrayList<Point2D> points = new ArrayList<>();

        for (Point2D point : spline) {
            points.add(point);

            if (points.size() == CUBIC) {
                bezier.generateCurve(points);
                bezier.generatePath();
                paths.add(bezier.getPath());
                points.clear();
            }
        }
        System.out.println(spline.size());
        bezier.generateCurve(points);
        bezier.generatePath();
        paths.add(bezier.getPath());
        System.out.println("Paths: " + paths.size());
    }

    private Point2D generateControlPoint(Point2D point1, Point2D point2) {
        return new Point2D.Double((point2.getX() - point1.getX()) / 2.0, (point2.getY() - point1.getY()) / 2.0);
    }

    public ArrayList<Path2D> getPaths() {
        return paths;
    }
}
