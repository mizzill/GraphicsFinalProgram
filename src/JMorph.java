import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JMorph extends JFrame {

    private BufferedImage src; //The Source Image
    private BufferedImage dest; // The Destination Image
    private ImageView srcView; // Displays Source Image
    private ImageView destView; //Displays Destination Image
    private ImageViewController ivc; // Controls the Image Views

    private JSlider lengthSlider; // The slider that controls the animation length
    private JLabel sliderLabel;
    private JPanel sliderPanel;
    private JButton previewButton;

    // Length of animation (in seconds)
    private final int MIN_LENGTH = 1;
    private final int MAX_LENGTH = 60;
    private final int INIT_LENGTH = 5;

    private int animationLength;

    // Constructor
    public JMorph(){
        super("Mighty JMorphin' Power Rangers");

        animationLength = INIT_LENGTH;

        setupMenu();
        buildComponents();
        buildDisplay();
    }

    /* Helper Method
    *  Creates Menu bar, items, and their handlers
    * */
    private void setupMenu() {

        JMenu fileMenu = new JMenu("File");
        final JFileChooser fc = new JFileChooser(".");

        // Change Source Image
        JMenuItem changeSrcImage = new JMenuItem("Change Source Image");
        fileMenu.add( changeSrcImage );
        changeSrcImage.addActionListener(e -> {
                int returnVal = fc.showOpenDialog(JMorph.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        src = ImageIO.read(file);
                    } catch (IOException e1){};

                    srcView.setImage(src);
                }
            }
        );

        // Change Destination Image
        JMenuItem changeDestImage = new JMenuItem("Change Destination Image");
        fileMenu.add( changeDestImage );
        changeDestImage.addActionListener(e -> {
                int returnVal = fc.showOpenDialog(JMorph.this);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fc.getSelectedFile();
                    try {
                        dest = ImageIO.read(file);
                    } catch (IOException e1){};

                    destView.setImage(dest);
                }
            }
        );

        // Reset
        JMenuItem resetImgs = new JMenuItem("Reset");
        fileMenu.add( resetImgs );
        resetImgs.addActionListener(e -> {
                srcView.setupControlGrid();
                destView.setupControlGrid();
            }
        );

        // Exit
        JMenuItem exit = new JMenuItem("Exit");
        fileMenu.add( exit );
        exit.addActionListener(e -> System.exit(0));

        //Add the menus to a JMenuBar
        JMenuBar bar = new JMenuBar();
        setJMenuBar( bar );
        bar.add(fileMenu);

    }

    /* Initializes components with values
    *  Sets up Action Listeners if necessary */
    private void buildComponents() {

        // Set up the Image Views and the controller
        srcView = new ImageView( readImage("src/bear.jpg") );
        destView = new ImageView( readImage("src/shrek.jpg") );

        ivc = new ImageViewController(srcView, destView);
        srcView.setController(ivc);
        destView.setController(ivc);

        // Set up the slider panel
        sliderPanel = new JPanel(new GridBagLayout());

        // Set up the slider label
        sliderLabel = new JLabel("Set the Length of the preview in seconds");

        // Set up the slider itself
        lengthSlider = new JSlider(SwingConstants.HORIZONTAL, MIN_LENGTH, MAX_LENGTH, INIT_LENGTH);
        lengthSlider.setMajorTickSpacing(59);
        lengthSlider.setMinorTickSpacing(5);
        lengthSlider.setPaintLabels(true);
        lengthSlider.setPaintTicks(true);

        lengthSlider.addChangeListener(e ->
                animationLength = lengthSlider.getValue()
        );

        //Set up the preview button
        previewButton = new JButton("Preview");
        previewButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // For now, arbitrarily choose the dimensions of the source image for the dialog size
                new PreviewDialog(
                        SwingUtilities.getWindowAncestor(previewButton),
                        new Dimension(srcView.getWidth(), srcView.getHeight()),
                        srcView.gridCellCoords,
                        srcView.controlPoints,
                        destView.controlPoints
                );
            }
        });

    }

    // Adds all the content views to the frame
    private void buildDisplay() {

        Container c = this.getContentPane();
        GridBagConstraints gc = new GridBagConstraints();

        //Top Label of Slider
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(0,0,10,0);

        sliderPanel.add(sliderLabel, gc);

        //Constraints for Slider
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 1;
        gc.insets = new Insets(0,0,10,0);

        sliderPanel.add(lengthSlider,gc);

        //Constraints for Preview Button
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 2;

        sliderPanel.add(previewButton, gc);

        // Add the panel and image views to the containing frame
        c.add(sliderPanel, BorderLayout.CENTER);
        c.add(srcView, BorderLayout.WEST);
        c.add(destView,BorderLayout.EAST);

        pack();
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
    public static void main(String args[]) {
        JMorph j = new JMorph();
        j.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }
}
