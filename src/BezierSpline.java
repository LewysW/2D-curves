import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

public class BezierSpline {
    private final int CUBIC = 4;
    private Bezier bezier = new Bezier();
    private ArrayList<Point2D> spline = new ArrayList<>();
    private ArrayList<Path2D>  paths = new ArrayList<>();


    public void clear() {
        spline.clear();
        paths.clear();
    }

    public void generateCurve(ArrayList<Point2D> points) {
        ArrayList<Point2D> splinePoints = new ArrayList<>();
        //If point, line, quadratic, or cubic, call superclass
        if (points.size() <= CUBIC) {
            bezier.generateCurve(points);
            spline = bezier.getCurve();
            return;
        }

        int controlPoint = 3;

        //Adds the required number of control points
        for (int i = 0; i < points.size(); i++) {
            splinePoints.add(points.get(i));

            if (i == controlPoint) {
                controlPoint += 3;
                splinePoints.add(i, generateControlPoint(points.get(i - 1), points.get(i)));
            }
        }

        this.spline = splinePoints;
        System.out.println("Points: " + points.size());

        generatePaths();
    }

    public void generatePaths() {
        if (spline.size() <= CUBIC) {
            paths.add(bezier.getPath());
        }

        Bezier bezier = new Bezier();
        ArrayList<Point2D> points = new ArrayList<>();

        for (int p = 0; p < spline.size(); p++) {
            Point2D point = spline.get(p);
            points.add(point);

            if (points.size() == CUBIC) {
                bezier.generateCurve(points);
                bezier.generatePath();
                paths.add(bezier.getPath());
                System.out.println("points.size():" + points.size());
                points.clear();

                //Includes imaginary point in next bezier curve
                p--;
            }
        }
        System.out.println(spline.size());
        bezier.generateCurve(points);
        bezier.generatePath();
        paths.add(bezier.getPath());
        System.out.println("Paths: " + paths.size());
    }

    private Point2D generateControlPoint(Point2D point1, Point2D point2) {
        return new Point2D.Double(((point2.getX() - point1.getX()) / 2.0) + point1.getX(),
                ((point2.getY() - point1.getY()) / 2.0) + point1.getY());
    }

    public ArrayList<Path2D> getPaths() {
        return paths;
    }
}
