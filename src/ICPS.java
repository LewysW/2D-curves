import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

//Interpolating cubic polynomial spline class
public class ICPS {
    //Stores points of splines
    private ArrayList<Point2D> splines = new ArrayList<>();
    //Stores path of spline
    private Path2D  path = new Path2D.Double();

    //Clears spline points and path
    public void clear() {
        splines.clear();
        path.reset();
    }

    //Getter for path
    public Path2D getPath() {
        return path;
    }

    //Getter for points of splines
    private ArrayList<Point2D> getSplines() {
        return splines;
    }

    //Generates curve of spline given plotted points
    public void generateCurve(ArrayList<Point2D> points) {
        //Stores coefficients of x and y in cubic spline formulae
        double ax, bx, cx, dx;
        double ay, by, cy, dy;

        //Matrices used to solve for value of coefficient values (solved using AX = B)
        double[][] matrixA = new double[points.size()][points.size() ];
        double[][] matrixB_X = new double[points.size()][1];
        double[][] matrixB_Y = new double[points.size()][1];

        //Initialises apache matrix objects using above 2D arrays
        initMatrixA(matrixA, points);
        initMatrixB(matrixB_X, points, true);
        initMatrixB(matrixB_Y, points, false);

        //Gets coefficients for b terms in equation for both x and y values
        RealMatrix D_X = getCoefficients(matrixA, matrixB_X);
        RealMatrix D_Y = getCoefficients(matrixA, matrixB_Y);

        //For each spline (i.e. between sets of two points)
        for (int i = 0; i < points.size() - 1; i++) {
            //Get a, b, c, d coefficients for x
            ax = points.get(i).getX();
            bx = D_X.getEntry(i, 0);
            cx = 3 * (points.get(i + 1).getX() - points.get(i).getX()) - 2 * D_X.getEntry(i, 0) - D_X.getEntry(i + 1, 0);
            dx = -2 * (points.get(i + 1).getX() - points.get(i).getX()) + D_X.getEntry(i, 0) + D_X.getEntry(i + 1, 0);

            //Get a, b, c, d coefficients for y
            ay = points.get(i).getY();
            by = D_Y.getEntry(i, 0);
            cy = 3 * (points.get(i + 1).getY() - points.get(i).getY()) - 2 * D_Y.getEntry(i, 0) - D_Y.getEntry(i + 1, 0);
            dy = -2 * (points.get(i + 1).getY() - points.get(i).getY()) + D_Y.getEntry(i, 0) + D_Y.getEntry(i + 1, 0);

            //Generate 1000 points for current spline
            for (double p = 0; p <= 1; p += 0.001) {
                //Calculates x and y coords of current spline using cubic spline formula
                // Xi(p) = ai + biP + ciP^2 + diP^3
                double x = ax + (bx * p) + (cx * Math.pow(p, 2)) + (dx * Math.pow(p, 3));
                // Yi(p) = ai + biP + ciP^2 + diP^3
                double y = ay + (by * p) + (cy * Math.pow(p, 2)) + (dy * Math.pow(p, 3));

                Point2D point = new Point2D.Double();
                point.setLocation(x, y);

                splines.add(point);
            }
        }

        generatePath();
    }

    //Solves AX = B and returns X (list of b coefficients)
    private RealMatrix getCoefficients(double[][] matrixA, double[][] matrixB) {
        //Create matrix object for each 2D array
        RealMatrix A = MatrixUtils.createRealMatrix(matrixA);
        RealMatrix B = MatrixUtils.createRealMatrix(matrixB);

        //Generate decomposition solver using matrix A
        DecompositionSolver solver = new LUDecomposition(A).getSolver();

        //Need to pass B in as a parameter to get X from AX = B
        //Gives us values for each bi coefficient, which also lets us express the ci and di coefficients
        return solver.solve(B);
    }

    //Initialises matrix A of expression AX = B
    private void initMatrixA(double[][] matrix, ArrayList<Point2D> points) {
        //INITIALISE MATRIX A:
        //Initialises the first row of matrix A
        //Ensures the slope of the first spline is initially equal to that of the Bezier curve
        matrix[0][0] = 1;

        //Initialises the middle rows of the A matrix
        int col = 0;
        for (int row = 1; row < points.size() - 1; row++) {
            //Sets A array values 1 4 1 for each row
            matrix[row][col] = 1;
            matrix[row][col + 1] = 4;
            matrix[row][col + 2] = 1;
            col++;
        }

        //Initialises the last row of matrix A
        //Ensures the slope of the last spline is equal to that of the Bezier curve at the end
        matrix[points.size() - 1][points.size() - 1] = 1;
    }

    //Initialises matrix B given a list of points and boolean to signify whether to use the x or y coordinates
    private void initMatrixB(double[][] matrix, ArrayList<Point2D> points, boolean x) {
        //Sets first row to either dx/du or dy/du to match slope of bezier curve
        matrix[0][0] = (x) ? (points.get(1).getX() - points.get(0).getX()) : (points.get(1).getY() - points.get(0).getY());

        for (int row = 1; row < points.size() - 1; row++) {
            //Sets B array for middle rows
            //i.e. 3(Pi+1 - Pi-1)
            matrix[row][0] = (x) ? (3 * (points.get(row + 1).getX() - points.get(row - 1).getX()))
                                : (3 * (points.get(row + 1).getY() - points.get(row - 1).getY()));
        }

        //Sets last value to either dx/du or dy/du to match slope of bezier curve
        matrix[points.size() - 1][0] = (x) ? (points.get(points.size() - 1).getX() - points.get(points.size() - 2).getX())
                                        : (points.get(points.size() - 1).getY() - points.get(points.size() - 2).getY());
    }

    //Generates the path of the bezier splines to be drawn through
    private void generatePath() {
        //For each point in the splines
        for (int i = 0; i < splines.size(); i++) {
            Point2D point = getSplines().get(i);

            //If the initial point, move to it
            if (i == 0) {
                path.moveTo(point.getX(), point.getY());
            }

            //Otherwise connect path to subsequent points
            path.lineTo(point.getX(), point.getY());
        }
    }

}
