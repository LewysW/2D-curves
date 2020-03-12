import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;

//Interpolating cubic polynomial spline class
public class ICPS {
    private ArrayList<Point2D> spline = new ArrayList<>();
    private ArrayList<Path2D>  paths = new ArrayList<>();

    public void clear() {
        spline.clear();
        paths.clear();
    }

    public ArrayList<Path2D> getPaths() {
        return paths;
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

        RealMatrix A = MatrixUtils.createRealMatrix(matrixA);
        RealMatrix B_X = MatrixUtils.createRealMatrix(matrixB_X);
        RealMatrix B_Y = MatrixUtils.createRealMatrix(matrixB_Y);

        System.out.println(A);
        System.out.println(B_X);
        System.out.println(B_Y);

        //LUDecomposition luDecomposition = new LUDecomposition(m);

        //Need to pass B in as a parameter to get X from AX = B
        //luDecomposition.getSolver().solve();

        generatePaths();
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
        System.out.println(0);
        for (int row = 1; row < points.size() - 1; row++) {
            //Sets B array for middle rows
            //i.e. 3(Pi+1 - Pi-1)
            matrix[row][0] = (x) ? (3 * (points.get(row + 1).getX() - points.get(row - 1).getX()))
                                : (3 * (points.get(row + 1).getY() - points.get(row - 1).getY()));
            System.out.println("row + 1: " + Integer.toString(row + 1) + " row - 1: " + Integer.toString(row - 1));
        }

        matrix[points.size() - 1][0] = (x) ? (points.get(points.size() - 1).getX() - points.get(points.size() - 2).getX())
                                        : (points.get(points.size() - 1).getY() - points.get(points.size() - 2).getY());
        System.out.println(points.size() - 1);
    }

    public void generatePaths() {

    }

}
