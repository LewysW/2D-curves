import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import javax.swing.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addActionListener;
import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class Curves extends JPanel {
    private ArrayList<Point2D> points = new ArrayList<>();
    private Bezier bezier = new Bezier();
    private BezierSpline bezierSpline = new BezierSpline();
    private JFrame frame;
    private JPanel panel;
    private JButton clear = new JButton("Clear");
    private JButton draw = new JButton("Draw");

    public static void main(String[] args) {
        double[][] matrix = {{1d, 2d, 3d}, {2d, 5d, 3d}};

        RealMatrix m = MatrixUtils.createRealMatrix(matrix);

        System.out.println(m);

        LUDecomposition luDecomposition = new LUDecomposition(m);

        //Need to pass B in as a parameter to get X from AX = B
        //luDecomposition.getSolver().solve();

        Curves curves = new Curves();
        curves.frame = new JFrame();

        curves.panel = new JPanel();
        curves.clear.setBounds(0, 0, 75, 75);
        curves.draw.setBounds(75, 0, 75, 75);
        curves.panel.add(curves.clear);
        curves.panel.add(curves.draw);
        curves.panel.setBounds(0, 0, 100, 100);

        curves.frame.setTitle("Curves in Computer Graphics");
        curves.frame.setSize(1280, 720);
        Container contentPane = curves.frame.getContentPane();
        contentPane.add(curves, BorderLayout.CENTER);
        curves.panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        contentPane.add(curves.panel, BorderLayout.LINE_START);
        curves.frame.setVisible(true);
    }


    public Curves() {
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        addMouseListener(new MouseAdapter() {// provides empty implementation of all
            // MouseListener`s methods, allowing us to
            // override only those which interests us
            @Override //I override only one method for presentation
            public void mousePressed(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY()));
                repaint();
            }
        });

        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //Removes points from array list points
                points.clear();
                bezier.clear();
                bezierSpline.clear();
                repaint();
            }
        });

        draw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //Removes points from array list points
                bezier.clear();
                bezier.generateCurve(points);

                bezierSpline.clear();
                bezierSpline.generateCurve(points);

                repaint();
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;

        //Clears points if list of points are empty
        if (points.isEmpty()) {
            clearDisplay(g);
            return;
        }

        //Plots point where user has clicked
        for (Point2D p : points) {
            graphics2D.setColor(Color.BLACK);
            graphics2D.fillOval((int) (p.getX() - 5.0 / 2.0), (int) (p.getY() - 5.0 / 2.0), 5, 5);
        }

        //Draws bezier curve
        graphics2D.setColor(Color.RED);
        graphics2D.draw(bezier.getPath());

        //Draws bezier spline
        graphics2D.setColor(Color.GREEN);

        for (Path2D path2D : bezierSpline.getPaths()) {
            graphics2D.draw(path2D);
        }

    }

    public void clearDisplay(Graphics g) {
        //Clears points by setting display to the default background colour
        g.setColor(frame.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        //Buttons disappear when drawn over, so are simply
        // re-added to avoid this issue in a simple way
        panel.remove(clear);
        panel.remove(draw);
        panel.add(clear);
        panel.add(draw);
    }

}