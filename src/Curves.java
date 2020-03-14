import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class Curves extends JPanel {
    private ArrayList<Point2D> points = new ArrayList<>();
    private Bezier bezier = new Bezier();
    private BezierSpline bezierSpline = new BezierSpline();
    private ICPS ICPS = new ICPS();

    private final int BORDER_GAP = 30;
    private JFrame frame;
    private JPanel panel;
    private JButton clear = new JButton("Clear");
    private JButton draw = new JButton("Draw");
    private JButton gen = new JButton("Generate");

    static private JCheckBox bezierCheck = new JCheckBox("Bezier");
    static private JCheckBox bezierSplineCheck = new JCheckBox("Bezier Spline");
    static private JCheckBox ICPSCheck = new JCheckBox("Interpolating Cubic Polynomial Spline");

    public static void main(String[] args) {
        Curves curves = new Curves();
        curves.frame = new JFrame();

        curves.panel = new JPanel();
        curves.panel.setLayout(new BoxLayout(curves.panel, BoxLayout.PAGE_AXIS));

        curves.panel.add(curves.clear);
        curves.panel.add(curves.draw);
        curves.panel.add(curves.gen);

        curves.panel.add(bezierCheck);
        curves.panel.add(bezierSplineCheck);
        curves.panel.add(ICPSCheck);


        curves.panel.updateUI();

        curves.frame.setTitle("Curves in Computer Graphics");
        curves.frame.setSize(1280, 720);
        Container contentPane = curves.frame.getContentPane();
        contentPane.add(curves, BorderLayout.CENTER);
        curves.panel.setBorder(BorderFactory.createLineBorder(Color.gray));
        contentPane.add(curves.panel, BorderLayout.LINE_START);
        curves.frame.setVisible(true);

        curves.repaint();
        curves.frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        curves.frame.setResizable(false);
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
                ICPS.clear();
                bezier.setPoint(null);
                repaint();
            }
        });

        draw.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (points.size() <= 1) return;

                //Removes points from array list points
                bezier.clear();
                bezier.setPoint(null);
                bezier.generateCurve(points);

                bezierSpline.clear();
                bezierSpline.generateCurve(points);

                ICPS.clear();
                ICPS.generateCurve(points);


                repaint();
            }
        });

        gen.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                if (bezier.getCurve().size() <= 1) return;

                bezier.resetTangent();
                bezier.resetNormal();

                if (bezierCheck.isSelected()) {
                    Random random = new Random();
                    double randomDouble = random.nextDouble();
                    int index = (int) (bezier.getCurve().size() * randomDouble);

                    bezier.setPoint(bezier.getCurve().get(index));
                    Point2D vector = bezier.derivative(index * (1.0 / bezier.getCurve().size()));

                    bezier.generateTangent(vector);
                    bezier.generateNormal(vector);

                    repaint();
                }
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D graphics2D = (Graphics2D) g;

        //Clears points if list of points are empty
        if (points.isEmpty()) {
            clearDisplay(g);
            g.setColor(Color.BLACK);
            g.drawLine(0, getHeight()/2, getWidth(), getHeight() / 2);
            g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());
            return;
        }


        //Plots point where user has clicked
        for (Point2D p : points) {
            graphics2D.setColor(Color.BLACK);
            graphics2D.fillOval((int) (p.getX() - 5.0 / 2.0), (int) (p.getY() - 5.0 / 2.0), 5, 5);
        }

        if (bezierSplineCheck.isSelected()) {
            //Draws bezier spline
            graphics2D.setColor(Color.GREEN);

            for (Path2D path2D : bezierSpline.getPaths()) {
                graphics2D.draw(path2D);
            }
        }


        if (bezierCheck.isSelected()) {
            //Draws bezier curve
            graphics2D.setColor(Color.RED);
            graphics2D.draw(bezier.getPath());

            if (bezier.getPoint() != null) {
                g.setColor(Color.BLACK);
                graphics2D.drawOval((int) ((bezier.getPoint().getX() - 5.0 / 2.0) - 7.5),
                        (int) ((bezier.getPoint().getY() - 5.0 / 2.0) - 7.5), 20, 20);

                g.setColor(Color.ORANGE);
                graphics2D.draw(bezier.getTangent());
                g.setColor(Color.magenta);
                graphics2D.draw(bezier.getNormal());
            }
        }

        if (ICPSCheck.isSelected()) {
            //Draws interpolating cubic polynomial spline
            graphics2D.setColor(Color.BLUE);
            graphics2D.draw(ICPS.getPath());
        }

        g.setColor(Color.BLACK);
        g.drawLine(0, getHeight()/2, getWidth(), getHeight() / 2);
        g.drawLine(getWidth() / 2, 0, getWidth() / 2, getHeight());

    }

    public void clearDisplay(Graphics g) {
        //Clears points by setting display to the default background colour
        g.setColor(frame.getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
    }

}