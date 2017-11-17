import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JMorph extends JFrame {

    //Right now planning on reusing Buffered Image and MyImageObj class from Week 10 code
    private BufferedImage src; //The Source Image
    private BufferedImage dest; // The Destination Image
    private MyImageObj srcView; // Displays Source Image
    private MyImageObj destView; //Displays destination Image

    private final int MIN_SECONDS = 15;
    private final int MAX_SECONDS = 60;
    private final int INIT_SECONDS = 30;

    private JSlider frameSlider;
    int seconds;



    //Constructor
    public JMorph(){
        super("JMorph brought to you by MC Productions");

        seconds = INIT_SECONDS;
        setupMenu();
        buildComponents();
        buildDisplay();



    }

    /*Helper Method
    * Creates Menu bar and items, also adds handlers to them
    * */
    private void setupMenu(){
        JMenu fileMenu = new JMenu("File");
        final JFileChooser fc = new JFileChooser(".");
        //Change Source Image
        JMenuItem changeSrcImage = new JMenuItem("Change Source Image");
        fileMenu.add( changeSrcImage );
        changeSrcImage.addActionListener(
                new ActionListener() {
                    public void actionPerformed (ActionEvent e) {
                        int returnVal = fc.showOpenDialog(JMorph.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                src = ImageIO.read(file);
                            } catch (IOException e1){};

                            srcView.setImage(src);
                            srcView.showImage();
                        }
                    }
                }
        );
        //Change Destination Image
        JMenuItem changeDestImage = new JMenuItem("Change Destination Image");
        fileMenu.add( changeDestImage );
        changeDestImage.addActionListener(
                new ActionListener() {
                    public void actionPerformed (ActionEvent e) {
                        int returnVal = fc.showOpenDialog(JMorph.this);
                        if (returnVal == JFileChooser.APPROVE_OPTION) {
                            File file = fc.getSelectedFile();
                            try {
                                dest = ImageIO.read(file);
                            } catch (IOException e1){};

                            destView.setImage(dest);
                            destView.showImage();
                        }
                    }
                }
        );
        //Save
        JMenuItem saveCtrlPts = new JMenuItem("Save");
        fileMenu.add( saveCtrlPts );
        /*TODO Handler*/
        //Reset
        JMenuItem resetImgs = new JMenuItem("Reset");
        fileMenu.add( resetImgs );
        resetImgs.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        srcView.showImage();
                        destView.showImage();
                        //Add in a dialog box or something to
                        //Warn the user before resettin
                        //infoLabel.setText("Original");
                    }
                }
        );

        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add( exit );
        exit.addActionListener(
                new ActionListener () {
                    public void actionPerformed (ActionEvent e) {
                        System.exit(0);
                    }
                }
        );

        //Add the menus to a JMenuBar
        JMenuBar bar = new JMenuBar();
        setJMenuBar( bar );
        bar.add(fileMenu);

    }

    /*Initializes components with values
    * Sets up Action Listeners if Necessary*/
    private void buildComponents(){
        srcView = new MyImageObj( readImage("src/boat.gif") );
        destView = new MyImageObj( readImage("src/boat.gif") );

        frameSlider = new JSlider(SwingConstants.HORIZONTAL, MIN_SECONDS, MAX_SECONDS, INIT_SECONDS);
        frameSlider.setMajorTickSpacing(5);
        frameSlider.setPaintLabels(true);
        frameSlider.setPaintTicks(true);

        frameSlider.addChangeListener(
                new ChangeListener() {
                    public void stateChanged (ChangeEvent e) {
                        seconds = frameSlider.getValue();
                    }
                }
        );
        srcView.addMouseMotionListener(
                new MouseMotionAdapter() {
                    public void mouseDragged(MouseEvent event) {
         /*               Graphics g = view.getGraphics();
                        g.setColor (Color.white);
                        if (firstdrag) {
                            x = event.getX();  y = event.getY();
                            firstdrag = false;
                        }
                        else {
                            view.showImage();
                            x=event.getX();
                            y=event.getY();
                            int w=view.getImage().getWidth();
                            int h=view.getImage().getHeight();
                            g.fillOval (x-5, y-5, 10, 10);
                            g.drawLine (0,0, x, y);
                            g.drawLine (0,h, x, y);
                            g.drawLine (w,h, x, y);
                            g.drawLine (w,0, x, y);
                        } */
                    }
                }
        );

        // Listen for mouse release to detect when we've stopped painting
        srcView.addMouseListener(
                new MouseAdapter() {
                    public void mouseReleased(MouseEvent event) {
        /*    Graphics g = view.getGraphics();
            firstdrag = true;
            x=event.getX();
            y=event.getY();
            int w=view.getImage().getWidth();
            int h=view.getImage().getHeight();
            g.fillOval (x-5, y-5, 10, 10);
            g.drawLine (0,0, x, y);
            g.drawLine (0,h, x, y);
            g.drawLine (w,h, x, y);
            g.drawLine (w,0, x, y);*/


                    }
                }
        );
    }
    private void buildDisplay(){


        Container c = this.getContentPane();

        c.add(frameSlider, BorderLayout.NORTH);
        c.add(srcView, BorderLayout.EAST);
        c.add(destView,BorderLayout.WEST);

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
