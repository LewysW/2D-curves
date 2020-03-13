import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class BezierSpline {
    private final int CUBIC = 4;
    private ArrayList<Point2D> spline = new ArrayList<>();
    private ArrayList<Path2D>  paths = new ArrayList<>();


    public void clear() {
        spline.clear();
        paths.clear();
    }

    public void generateCurve(ArrayList<Point2D> points) {
        ArrayList<Point2D> splinePoints = new ArrayList<>();
        for (Point2D point2D : points) {
            Point2D point = new Point2D.Double();
            point.setLocation(point2D.getX(), point2D.getY());
            splinePoints.add(point);
        }

        int start = 0;
        int end = start + 3;

        while (end < splinePoints.size() - 1) {
            System.out.println("start: " + start);
            System.out.println("end: " + end);

            splinePoints.add(end, generateControlPoint(splinePoints.get(end - 1), splinePoints.get(end)));

            start = end;
            end = start + 3;
        }

        this.spline = splinePoints;
        generatePaths();
    }

    public void generatePaths() {
        ArrayList<Point2D> points = new ArrayList<>();
        int current = 0;

        while (current != spline.size()) {
            if (points.size() == 4) {
                Bezier bezier = new Bezier();
                bezier.generateCurve(points);
                bezier.generatePath();
                paths.add(bezier.getPath());
                points.clear();
                current--;
            } else {
                points.add(spline.get(current++));
            }

            System.out.println("Points.size1(): " + points.size());
        }
        System.out.println("points.size2(): " + points.size());
        Bezier bezier = new Bezier();
        bezier.generateCurve(points);
        bezier.generatePath();
        paths.add(bezier.getPath());
        points.clear();
    }

    private Point2D generateControlPoint(Point2D point1, Point2D point2) {
        return new Point2D.Double(((point2.getX() + point1.getX()) / 2.0),
                ((point2.getY() + point1.getY()) / 2.0));
    }

    public ArrayList<Path2D> getPaths() {
        return paths;
    }
}
