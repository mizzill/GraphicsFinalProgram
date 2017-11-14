import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JMorph extends JFrame {

    //Constructor
    public JMorph(){
        super("JMorph brought to you by MC Productions");

        setSize(800, 600 );
        setVisible(true);
    }

    //Initialize and run class
    public static void main(String args[])
    {
        JMorph j = new JMorph();
        j.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }
}
