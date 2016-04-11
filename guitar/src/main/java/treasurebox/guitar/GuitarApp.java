/**
 */
package treasurebox.guitar;

import java.awt.Container;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.sound.sampled.LineUnavailableException;
import javax.swing.BoxLayout;
import javax.swing.JFrame;

import treasurebox.guitar.audio.AudioPlayer;
import treasurebox.guitar.ui.GuitarPanel;

/**
 * 
 * @author shuo.qius
 * @version $Id: GuitarApp.java, v 0.1 Apr 11, 2016 6:58:50 PM qiushuo Exp $
 */
public class GuitarApp {
    private final AudioPlayer audio;
    private final GuitarPanel guitarPanel;

    public GuitarApp() throws LineUnavailableException {
        this.audio = new AudioPlayer();
        this.guitarPanel = new GuitarPanel();
    }

    private void constructGUI(JFrame appFrame) {
        Container contentPane = appFrame.getContentPane();
        contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));

    }

    public void start() throws Throwable {
        this.audio.start();
        final JFrame appFrame = new JFrame("Score");

        appFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                audio.stop();
                System.exit(0);
            }
        });

        constructGUI(appFrame);

        EventQueue.invokeLater(() -> {
            appFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            appFrame.setVisible(true);
        });
    }
    
    

    public static void main(String[] args) throws Throwable {
        new GuitarApp().start();
    }

}
