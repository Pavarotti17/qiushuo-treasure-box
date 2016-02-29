/**
 */
package treasurebox.guitar;

import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 * 
 * @author shuo.qius
 * @version $Id: Main.java, v 0.1 Feb 22, 2016 2:51:20 PM qiushuo Exp $
 */
public class Main {
    /**
     * @param args
     */
    public static void main(String[] args) throws Throwable {
        EventQueue.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);
        });
    }

    public static class MainFrame extends JFrame {
        private static final long serialVersionUID = -9161862898198550670L;

        public MainFrame() {
            final ScoreComponent comp = new ScoreComponent();
            add(comp);
            pack();
            new Thread(() -> {
                comp.repaint();
                sleep();
            }).start();
            ;
        }
    }

    private static void sleep() {
        try {
            Thread.sleep(500);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static class ScoreComponent extends JComponent {
        private static final int DEFAULT_WIDTH  = 600;
        private static final int DEFAULT_HEIGHT = 400;

        /** 
         * @see javax.swing.JComponent#paint(java.awt.Graphics)
         */
        @Override
        public void paint(Graphics g) {
            System.out.println("paint");
            Graphics2D g2 = (Graphics2D) g;
            Line2D line = new java.awt.geom.Line2D.Double(50, new Random().nextInt(100), new Random().nextInt(100), new Random().nextInt(100));
            g2.draw(line);
            // QS_TODO
        }

        /** 
         * @see javax.swing.JComponent#getPreferredSize()
         */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(DEFAULT_WIDTH, DEFAULT_HEIGHT);
        }
    }

}
