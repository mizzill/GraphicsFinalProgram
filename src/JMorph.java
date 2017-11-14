import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class JMorph extends JFrame {

    //Constructor
    public JMorph(){
        super("JMorph brought to you by MC Productions");

        setupMenu();

        setSize(800, 600 );
        setVisible(true);
    }

    /*Helper Method
    * Creates Menu bar and items, also adds handlers to them
    * */
    private void setupMenu(){
        JMenu fileMenu = new JMenu("File");

        //Change Source Image
        JMenuItem changeSrcImage = new JMenuItem("Change Source Image");
        fileMenu.add( changeSrcImage );
        /*TODO Handler*/
        //Change Destination Image
        JMenuItem changeDestImage = new JMenuItem("Change Destination Image");
        fileMenu.add( changeDestImage );
        /*TODO Handler*/
        //Save
        JMenuItem saveCtrlPts = new JMenuItem("Save");
        fileMenu.add( saveCtrlPts );
        /*TODO Handler*/
        //Reset
        JMenuItem resetImgs = new JMenuItem("Reset");
        fileMenu.add( resetImgs );
        /*TODO Handler*/

        //Add the menus to a JMenuBar
        JMenuBar bar = new JMenuBar();
        setJMenuBar( bar );
        bar.add(fileMenu);

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
