import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

//Interpolating cubic polynomial spline class
public class ICPS {
    private ArrayList<Point2D> spline = new ArrayList<>();
    private Path2D  path = new Path2D.Double();

    public void clear() {
        spline.clear();
        path.reset();
    }

    public Path2D getPath() {
        return path;
    }

    public ArrayList<Point2D> getSpline() {
        return spline;
    }

    public void generateCurve(ArrayList<Point2D> points) {
        double ax, bx, cx, dx;
        double ay, by, cy, dy;

        double[][] matrixA = new double[points.size()][points.size() ];
        double[][] matrixB_X = new double[points.size()][1];
        double[][] matrixB_Y = new double[points.size()][1];

        initMatrixA(matrixA, points);
        initMatrixB(matrixB_X, points, true);
        initMatrixB(matrixB_Y, points, false);

        RealMatrix D_X = getCoefficients(matrixA, matrixB_X, points, true);
        RealMatrix D_Y = getCoefficients(matrixA, matrixB_Y, points, false);

        for (int i = 0; i < points.size() - 1; i++) {
            //Gets coefficients for x
            ax = points.get(i).getX();
            bx = D_X.getEntry(i, 0);
            cx = 3 * (points.get(i + 1).getX() - points.get(i).getX()) - 2 * D_X.getEntry(i, 0) - D_X.getEntry(i + 1, 0);
            dx = -2 * (points.get(i + 1).getX() - points.get(i).getX()) + D_X.getEntry(i, 0) + D_X.getEntry(i + 1, 0);

            //Gets coefficients for y
            ay = points.get(i).getY();
            by = D_Y.getEntry(i, 0);
            cy = 3 * (points.get(i + 1).getY() - points.get(i).getY()) - 2 * D_Y.getEntry(i, 0) - D_Y.getEntry(i + 1, 0);
            dy = -2 * (points.get(i + 1).getY() - points.get(i).getY()) + D_Y.getEntry(i, 0) + D_Y.getEntry(i + 1, 0);


            for (double p = 0; p <= 1; p += 0.001) {
                //Calculates x and y coords of current spline using cubic spline formula
                // Xi(p) = ai + biP + ciP^2 + diP^3
                double x = ax + (bx * p) + (cx * Math.pow(p, 2)) + (dx * Math.pow(p, 3));
                // Yi(p) = ai + biP + ciP^2 + diP^3
                double y = ay + (by * p) + (cy * Math.pow(p, 2)) + (dy * Math.pow(p, 3));

                Point2D point = new Point2D.Double();
                point.setLocation(x, y);

                spline.add(point);
            }
        }

        generatePath();
    }

    //Solves AX = B and returns X
    RealMatrix getCoefficients(double[][] matrixA, double[][] matrixB, ArrayList<Point2D> points, boolean x) {
        RealMatrix A = MatrixUtils.createRealMatrix(matrixA);
        RealMatrix B = MatrixUtils.createRealMatrix(matrixB);

        DecompositionSolver solver = new LUDecomposition(A).getSolver();

        //Need to pass B in as a parameter to get X from AX = B
        //Gives us the coordinate to plug into the equations for bi, ci and di
        return solver.solve(B);
    }

    void initMatrixA(double[][] matrix, ArrayList<Point2D> points) {
        //INITIALISE MATRIX A:
        //Initialises the first row of matrix A
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
        matrix[points.size() - 1][points.size() - 1] = 1;
    }

    void initMatrixB(double[][] matrix, ArrayList<Point2D> points, boolean x) {
        matrix[0][0] = (x) ? (points.get(1).getX() - points.get(0).getX()) : (points.get(1).getY() - points.get(0).getY());

        for (int row = 1; row < points.size() - 1; row++) {
            //Sets B array for middle rows
            //i.e. 3(Pi+1 - Pi-1)
            matrix[row][0] = (x) ? (3 * (points.get(row + 1).getX() - points.get(row - 1).getX()))
                                : (3 * (points.get(row + 1).getY() - points.get(row - 1).getY()));
        }

        matrix[points.size() - 1][0] = (x) ? (points.get(points.size() - 1).getX() - points.get(points.size() - 2).getX())
                                        : (points.get(points.size() - 1).getY() - points.get(points.size() - 2).getY());
    }

    public void generatePath() {
        for (int i = 0; i < spline.size(); i++) {
            Point2D point = getSpline().get(i);

            if (i == 0) {
                path.moveTo(point.getX(), point.getY());
            }

            path.lineTo(point.getX(), point.getY());
        }
    }

}
