import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

//Class to generate bezier spline
public class BezierSpline {
    //Represents number of points to draw and cubic
    private final int CUBIC = 4;
    //Stores points on bezier spline
    private ArrayList<Point2D> spline = new ArrayList<>();
    //Stores paths of each bezier curve
    private ArrayList<Path2D>  paths = new ArrayList<>();

    //Clears points and path of bezier spline
    public void clear() {
        spline.clear();
        paths.clear();
    }

    //Generates curve of bezier spline
    public void generateCurve(ArrayList<Point2D> points) {
        ArrayList<Point2D> splinePoints = new ArrayList<>();

        //Add all plotted points to spline
        for (Point2D point2D : points) {
            Point2D point = new Point2D.Double();
            point.setLocation(point2D.getX(), point2D.getY());
            splinePoints.add(point);
        }

        //Set start and end points of current bezier curve
        int start = 0;
        int end = start + 3;

        //While the end of the current bezier curve is not the final plotted point
        while (end < splinePoints.size() - 1) {
            //Add an imaginary point at the end index of the current curve (i.e. between the 3rd and 4th point of bezier)
            splinePoints.add(end, generateControlPoint(splinePoints.get(end - 1), splinePoints.get(end)));

            //Update start and end points of current curve
            start = end;
            end = start + 3;
        }

        //Assigns generated list of points to the bezier spline
        this.spline = splinePoints;

        //Generate path through points
        generatePaths();
    }

    //Generate list of paths, each passing through one of the generated bezier curves
    public void generatePaths() {
        ArrayList<Point2D> points = new ArrayList<>();
        int current = 0;

        //While current point is not the end of the spline
        while (current != spline.size()) {
            //If number of points traversed can produce a cubic curve
            if (points.size() == CUBIC) {
                //Create a new bezier curve
                Bezier bezier = new Bezier();
                //Generate a curve with the current list of points
                bezier.generateCurve(points);
                //Generate a path through the curve
                bezier.generatePath();
                //Add the path to the list of paths in the bezier spline
                paths.add(bezier.getPath());
                //Clear the list of points
                points.clear();
                //Decrement point by 1 (to include the current imaginary point in next bezier)
                current--;
            } else {
                //Otherwise add point to list of points and increment
                points.add(spline.get(current++));
            }
            
        }

        //Plot remaining points at end of spline (e.g. if 3 points left over for a quadratic curve)
        Bezier bezier = new Bezier();
        bezier.generateCurve(points);
        bezier.generatePath();
        paths.add(bezier.getPath());
        points.clear();
    }

    //Generates an imaginary control point halfway between two points
    private Point2D generateControlPoint(Point2D point1, Point2D point2) {
        return new Point2D.Double(((point2.getX() + point1.getX()) / 2.0),
                ((point2.getY() + point1.getY()) / 2.0));
    }

    //Getter for paths of bezier spline
    public ArrayList<Path2D> getPaths() {
        return paths;
    }
}
