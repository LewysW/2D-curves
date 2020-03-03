import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import javax.swing.*;

import static com.sun.java.accessibility.util.AWTEventMonitor.addWindowListener;

public class Curves extends JPanel {
    ArrayList<Point> points = new ArrayList<>();

    public static void main(String[] args) {
        Curves curves = new Curves();
        JFrame frame = new JFrame();
        frame.setTitle("Polygon");
        frame.setSize(350, 250);

        Container contentPane = frame.getContentPane();
        contentPane.add(curves);
        frame.setVisible(true);
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
                System.out.println(e.getX() + "," + e.getY());
                points.add(new Point(e.getX(), e.getY()));
                repaint();
            }
        });
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        for (Point p : points) {
            g.fillOval(p.x, p.y, 5, 5);
        }
    }

}