import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

/**
 * Class to manage the swing user interface
 */
public class Curves extends JPanel {
    //Stores the list of points entered by the user
    private ArrayList<Point2D> points = new ArrayList<>();

    //Creates the instances of the three class which generate the specified curves
    private Bezier bezier = new Bezier();
    private BezierSpline bezierSpline = new BezierSpline();
    private ICPS ICPS = new ICPS();

    //JFrame representing the GUI window
    private JFrame frame;
    //Panel to place buttons and checkboxes on
    private JPanel panel;

    //Button to clear the display
    private JButton clear = new JButton("Clear");
    //Button to draw the curves on the display
    private JButton draw = new JButton("Draw");
    //Button to generate a point on the bezier curve, then draw a tangent and perpendicular line
    private JButton gen = new JButton("Generate");

    //Check boxes for each curve to allow the user to toggle that curve on or off
    static private JCheckBox bezierCheck = new JCheckBox("Bezier");
    static private JCheckBox bezierSplineCheck = new JCheckBox("Bezier Spline");
    static private JCheckBox ICPSCheck = new JCheckBox("Interpolating Cubic Polynomial Spline");

    public static void main(String[] args) {
        Curves curves = new Curves();
        curves.frame = new JFrame();

        //Initialise panel to place buttons on
        curves.panel = new JPanel();
        curves.panel.setLayout(new BoxLayout(curves.panel, BoxLayout.PAGE_AXIS));

        //Add buttons to panel
        curves.panel.add(curves.clear);
        curves.panel.add(curves.draw);
        curves.panel.add(curves.gen);

        //Add check boxes to panel
        curves.panel.add(bezierCheck);
        curves.panel.add(bezierSplineCheck);
        curves.panel.add(ICPSCheck);

        //Specifies layout of frame
        curves.frame.setTitle("Curves in Computer Graphics");
        curves.frame.setSize(1280, 720);
        Container contentPane = curves.frame.getContentPane();
        contentPane.add(curves, BorderLayout.CENTER);

        //Specifies border of panel
        curves.panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        //Adds panel to frame
        contentPane.add(curves.panel, BorderLayout.LINE_START);
        //Sets frame to visible
        curves.frame.setVisible(true);

        //Draws the frame
        curves.repaint();

        //Ensures that program exits when window is closed
        curves.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        //Sets resizable to false for the window
        curves.frame.setResizable(false);
    }


    public Curves() {
        //Exits program on window being closed
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

        //Adds points to the display when clicked
        addMouseListener(new MouseAdapter() {// provides empty implementation of all
            // MouseListener`s methods, allowing us to
            // override only those which interests us
            @Override //I override only one method for presentation
            public void mousePressed(MouseEvent e) {
                points.add(new Point(e.getX(), e.getY()));
                repaint();
            }
        });

        //Clears the curves and plotted points from the display and repaints
        clear.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //Removes points from array list points
                points.clear();
                //Clears bezier curve
                bezier.clear();
                //Clears bezier spline
                bezierSpline.clear();
                //Clears interpolating cubic polynomial spline
                ICPS.clear();
                //Sets random point plotted on bezier to null
                bezier.setPoint(null);
                repaint();
            }
        });

        //Draws the curves on the display
        draw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //If 1 or fewer points, display nothing
                if (points.size() <= 1) return;

                //Clears bezier curve and point and generates new curve
                bezier.clear();
                bezier.setPoint(null);
                bezier.generateCurve(points);

                //Clears bezier spline and generates new curve
                bezierSpline.clear();
                bezierSpline.generateCurve(points);

                //Clears interpolating cubic polynomial spline and generates new curve
                ICPS.clear();
                ICPS.generateCurve(points);


                repaint();
            }
        });

        //Generates the random point on the bezier curve
        gen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                //Do nothing if 1 or less points have been plotted
                if (bezier.getCurve().size() <= 1) return;

                //Reset the tangent and normal of bezier curve
                bezier.resetTangent();
                bezier.resetNormal();

                //If the bezier curve has been toggled on
                if (bezierCheck.isSelected()) {
                    //Generate a random point
                    Random random = new Random();
                    double randomDouble = random.nextDouble();
                    int index = (int) (bezier.getCurve().size() * randomDouble);
                    bezier.setPoint(bezier.getCurve().get(index));

                    //Get vector of tangent to bezier curve using bezier function derivative
                    Point2D vector = bezier.derivative(index * (1.0 / bezier.getCurve().size()));

                    //Generates tangent and perpendicular line to bezier curve
                    bezier.generateTangent(vector);
                    bezier.generateNormal(vector);

                    repaint();
                }
            }
        });
    }

    //Paints the display
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;

        //If no points have been plotted
        if (points.isEmpty()) {
            //clear display and draw set of axes
            clearDisplay(g);
            g.setColor(Color.BLACK);
            g.drawLine(0, getHeight()/2, getWidth(), getHeight() / 2);
            g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
            return;
        }


        //Plots points where user has clicked
        for (Point2D p : points) {
            graphics2D.setColor(Color.BLACK);
            graphics2D.fillOval((int) (p.getX() - 5.0 / 2.0), (int) (p.getY() - 5.0 / 2.0), 5, 5);
        }

        //If bezier spline has been toggled on
        if (bezierSplineCheck.isSelected()) {
            //Set colour to green
            graphics2D.setColor(Color.GREEN);

            //Draw bezier spline
            for (Path2D path2D : bezierSpline.getPaths()) {
                graphics2D.draw(path2D);
            }
        }

        //If bezier curve has been toggled on
        if (bezierCheck.isSelected()) {
            //Set colour to red
            graphics2D.setColor(Color.RED);
            //Draw bezier curve
            graphics2D.draw(bezier.getPath());

            //If random point has been generated
            if (bezier.getPoint() != null) {
                //Set colour to black
                g.setColor(Color.BLACK);
                //Draw oval to mark random point
                graphics2D.drawOval((int) ((bezier.getPoint().getX() - 5.0 / 2.0) - 7.5),
                        (int) ((bezier.getPoint().getY() - 5.0 / 2.0) - 7.5), 20, 20);

                //Set colour to orange
                g.setColor(Color.ORANGE);
                //Draw tangent to bezier curve at random point
                graphics2D.draw(bezier.getTangent());
                //Set colour to magenta
                g.setColor(Color.magenta);
                //Draw perpendicular line to bezier curve
                graphics2D.draw(bezier.getNormal());
            }
        }

        //If interpolating cubic polynomial spline has been toggled on
        if (ICPSCheck.isSelected()) {
            //Set colour to blue
            graphics2D.setColor(Color.BLUE);
            //Draw interpolating cubic polynomial spline
            graphics2D.draw(ICPS.getPath());
        }

        //Sets colour to black
        g.setColor(Color.BLACK);
        //Draw set of axes
        g.drawLine(0, getHeight()/2, getWidth(), getHeight() / 2);
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

    }

    //Clears the display
    private void clearDisplay(Graphics g) {
        //Clears points by setting display to the default background colour
        g.setColor(frame.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

}