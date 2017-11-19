import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JMorph extends JFrame {

    //Right now planning on reusing Buffered Image and MyImageObj class from Week 10 code
    private BufferedImage src; //The Source Image
    private BufferedImage dest; // The Destination Image
    private ImageView srcView; // Displays Source Image
    private ImageView destView; //Displays Destination Image

    private final int MIN_SECONDS = 15;
    private final int MAX_SECONDS = 60;
    private final int INIT_SECONDS = 30;

    private JSlider frameSlider;
    private int seconds;

    //Constructor
    public JMorph(){
        super("Mighty JMorphin' Power Rangers");

        seconds = INIT_SECONDS;
        setupMenu();
        buildComponents();
        buildDisplay();

    }

    /* Helper Method
    *  Creates Menu bar, items, and their handlers
    * */
    private void setupMenu(){
        JMenu fileMenu = new JMenu("File");
        final JFileChooser fc = new JFileChooser(".");

        //Change Source Image
        JMenuItem changeSrcImage = new JMenuItem("Change Source Image");
        fileMenu.add( changeSrcImage );
        changeSrcImage.addActionListener(e -> {
                int returnVal = fc.showOpenDialog(JMorph.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        src = ImageIO.read(file);
                    } catch (IOException e1){
                        //TODO handle this exception
                    };

                    srcView.setImage(src);
                }
            }
        );

        //Change Destination Image
        JMenuItem changeDestImage = new JMenuItem("Change Destination Image");
        fileMenu.add( changeDestImage );
        changeDestImage.addActionListener(e -> {
                int returnVal = fc.showOpenDialog(JMorph.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        dest = ImageIO.read(file);
                    } catch (IOException e1){
                        //TODO handle this exception
                    };

                    destView.setImage(dest);
                }
            }
        );

        //Save
        JMenuItem saveCtrlPts = new JMenuItem("Save");
        fileMenu.add( saveCtrlPts );

        /*TODO Save Handler*/

        //Reset
        JMenuItem resetImgs = new JMenuItem("Reset");
        fileMenu.add( resetImgs );
        resetImgs.addActionListener(e -> {
                srcView.setupControlGrid();
                destView.setupControlGrid();
            }
        );

        //Exit
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add( exit );
        exit.addActionListener(e -> System.exit(0));

        //Add the menus to a JMenuBar
        JMenuBar bar = new JMenuBar();
        setJMenuBar( bar );
        bar.add(fileMenu);

    }

    /*Initializes components with values
    * Sets up Action Listeners if necessary*/
    private void buildComponents(){
        srcView = new ImageView( readImage("src/boat.gif") );
        destView = new ImageView( readImage("src/island.jpg") );

        frameSlider = new JSlider(SwingConstants.HORIZONTAL, MIN_SECONDS, MAX_SECONDS, INIT_SECONDS);
        frameSlider.setMajorTickSpacing(5);
        frameSlider.setPaintLabels(true);
        frameSlider.setPaintTicks(true);

        frameSlider.addChangeListener(e ->
            seconds = frameSlider.getValue()
        );
    }

    // Adds all the content views to the frame
    private void buildDisplay(){
        Container c = this.getContentPane();

        c.add(frameSlider, BorderLayout.NORTH);
        c.add(srcView, BorderLayout.WEST);
        c.add(destView,BorderLayout.EAST);

        pack();
        setSize(800, 600);
        setVisible(true);
    }

    // This method reads an Image object from a file indicated by
    // the string provided as the parameter.  The image is converted
    // here to a BufferedImage object, and that new object is the returned
    // value of this method.
    // The mediatracker in this method can throw an exception

    public BufferedImage readImage (String file) {

        Image image = Toolkit.getDefaultToolkit().getImage(file);
        MediaTracker tracker = new MediaTracker (new Component () {});
        tracker.addImage(image, 0);
        try { tracker.waitForID (0); }
        catch (InterruptedException e) {}
        BufferedImage bim = new BufferedImage
                (image.getWidth(this), image.getHeight(this),
                        BufferedImage.TYPE_INT_RGB);
        Graphics2D big = bim.createGraphics();
        big.drawImage (image, 0, 0, this);
        return bim;

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
