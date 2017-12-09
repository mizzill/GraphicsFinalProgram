import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.*;
import java.util.ArrayList;

public class JMorph extends JFrame {

    private BufferedImage src; //The Source Image
    private BufferedImage dest; // The Destination Image
    private ImageView srcView; // Displays Source Image
    private ImageView destView; //Displays Destination Image
    private String srcPath; //Stores the path to the source image
    private String destPath; //Stores the path to the destination image
    private ImageViewController ivc; // Controls the Image Views

    private JSlider lengthSlider; // The slider that controls the animation length
    private JLabel sliderLabel;
    private JPanel sliderPanel;

    private JSlider srcBrightSlider; //Src Brightness Slider
    private JSlider destBrightSlider; //Dest Brightness Slider

    //Brightness Slider Labels
    private JLabel srcBrightLabel;
    private JLabel destBrightLabel;

    //Resolution Slider
    private JSlider resolutionSlider;
    private JLabel resolutionLabel;

    private JButton previewButton;
    private PreviewDialog previewDialog;

    // Length of animation (in seconds)
    private final int MIN_LENGTH = 1;
    private final int MAX_LENGTH = 10;
    private final int INIT_LENGTH = 3;

    //Constants for brightness
    private final int MIN_LUMINANCE = 10;
    private final int MAX_LUMINANCE = 25;
    private final int INIT_LUMINANCE = 10;

    //Constants For Control Grid Resolution
    private final int MIN_POINTS = 1;
    private final int MAX_POINTS = 20;
    private final int INIT_POINTS = 10;

    // Allows the user to select a directory for saving/loading settings
    private final JFileChooser fc = new JFileChooser(".");

    // Animation variables
    public final int FPS = 30;
    public int animationLength;

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

        // Change Source Image
        JMenuItem changeSrcImage = new JMenuItem("Change Source Image");
        fileMenu.add( changeSrcImage );
        changeSrcImage.addActionListener(e -> {
                int dialogSelection = fc.showOpenDialog(JMorph.this);
                if (dialogSelection == JFileChooser.APPROVE_OPTION) {
                    fc.setDialogTitle("Select source image");
                    File file = fc.getSelectedFile();
                    try {
                        src = ImageIO.read(file);
                        srcView.setImage(src);
                        srcPath = file.getPath();
                    } catch (IOException e1){}
                }
            }
        );

        // Change Destination Image
        JMenuItem changeDestImage = new JMenuItem("Change Destination Image");
        fileMenu.add( changeDestImage );
        changeDestImage.addActionListener(e -> {
                int dialogSelection = fc.showOpenDialog(JMorph.this);
                if (dialogSelection == JFileChooser.APPROVE_OPTION) {
                    fc.setDialogTitle("Select destination image");
                    File file = fc.getSelectedFile();
                    try {
                        dest = ImageIO.read(file);
                        destView.setImage(dest);
                        destPath = file.getPath();
                    } catch (IOException e1){ }
                }
            }
        );

        // Reset
        JMenuItem resetImgs = new JMenuItem("Reset");
        fileMenu.add( resetImgs );
        resetImgs.addActionListener(e -> {
                ivc.changeGridResolution(INIT_POINTS);
                resolutionSlider.setValue(INIT_POINTS);
                destBrightSlider.setValue(INIT_LUMINANCE);
                srcBrightSlider.setValue(INIT_LUMINANCE);
                lengthSlider.setValue(INIT_LENGTH);
            }
        );

        // Save
        JMenuItem save = new JMenuItem("Save Settings");
        fileMenu.add( save );
        save.addActionListener(e -> saveConfig());

        // Load
        JMenuItem load = new JMenuItem("Load Settings");
        fileMenu.add( load );
        load.addActionListener(e -> loadConfig());

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

        // Set up the controller
        ivc = new ImageViewController();

        // Set up the initial image views
        String bearPicPath = "src/bear.jpg";
        srcView = new ImageView( readImage(bearPicPath), ivc );
        srcPath = bearPicPath;

        String shrekPicPath = "src/shrek.jpg";
        destView = new ImageView( readImage(shrekPicPath), ivc );
        destPath = shrekPicPath;

        // Attach the views to the image view controller
        ivc.setViews(srcView, destView);

        // Set up the slider panel
        sliderPanel = new JPanel(new GridBagLayout());

        // Set up the slider label
        sliderLabel = new JLabel("Set the Length of the preview in seconds");

        // Set up the animation length slider
        lengthSlider = new JSlider(SwingConstants.HORIZONTAL, MIN_LENGTH, MAX_LENGTH, INIT_LENGTH);
        lengthSlider.setMajorTickSpacing(9);
        lengthSlider.setMinorTickSpacing(1);
        lengthSlider.setPaintLabels(true);
        lengthSlider.setPaintTicks(true);
        lengthSlider.addChangeListener(e ->
                animationLength = lengthSlider.getValue()
        );

        //Build out the Brightness Sliders
        srcBrightSlider = new JSlider(SwingConstants.HORIZONTAL, MIN_LUMINANCE, MAX_LUMINANCE, INIT_LUMINANCE);
        srcBrightSlider.setPaintTicks(true);
        srcBrightSlider.addChangeListener(e -> {
                int value = srcBrightSlider.getValue();
                float brightness = value / 10f;
                srcView.changeBrightness(brightness);

            }
        );

        destBrightSlider = new JSlider(SwingConstants.HORIZONTAL, MIN_LUMINANCE, MAX_LUMINANCE, INIT_LUMINANCE);
        destBrightSlider.setPaintTicks(true);
        destBrightSlider.addChangeListener(e -> {
                    int value = destBrightSlider.getValue();
                    float brightness = value / 10f;
                    destView.changeBrightness(brightness);
                }
        );

        srcBrightLabel = new JLabel("Source Brightness:");
        destBrightLabel = new JLabel("Destination Brightness:");

        //Resolution Slider
        resolutionLabel = new JLabel("Grid Resolution:");
        resolutionSlider = new JSlider(SwingConstants.HORIZONTAL, MIN_POINTS, MAX_POINTS, INIT_POINTS);
        resolutionSlider.setMajorTickSpacing(19);
        resolutionSlider.setMinorTickSpacing(1);
        resolutionSlider.setPaintLabels(true);
        resolutionSlider.setPaintTicks(true);
        resolutionSlider.addChangeListener(e -> {
                int resolution = resolutionSlider.getValue();
                ivc.changeGridResolution(resolution);
            }
        );

        //Set up the preview button
        previewButton = new JButton("Preview");
        previewButton.addActionListener(e -> previewDialog.revealPreview());

    }

    // Adds all the content views to the frame
    private void buildDisplay() {

        Container c = this.getContentPane();
        GridBagConstraints gc = new GridBagConstraints();

        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 0;
        gc.insets = new Insets(0,0,10,0);
        sliderPanel.add(resolutionLabel,gc);

        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 1;
        gc.insets = new Insets(0,0,10,0);
        sliderPanel.add(resolutionSlider,gc);

        //Top Label of Slider
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 2;
        gc.insets = new Insets(0,0,10,0);

        sliderPanel.add(sliderLabel, gc);

        //Constraints for Slider
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 3;
        gc.insets = new Insets(0,0,10,0);

        sliderPanel.add(lengthSlider,gc);

        //Constraints for Preview Button
        gc.fill = GridBagConstraints.HORIZONTAL;
        gc.gridx = 0;
        gc.gridy = 4;

        sliderPanel.add(previewButton, gc);

        // Add the panel and image views to the containing frame
        c.add(sliderPanel, BorderLayout.CENTER);

        //Add the Images to the view
        c.add(srcView, BorderLayout.WEST);
        c.add(destView,BorderLayout.EAST);

        //Initialize Panel for the brightness sliders
        JPanel brightPanel = new JPanel( new GridLayout(2,2));

        //Add the sliders and the labels
        brightPanel.add(srcBrightLabel);
        brightPanel.add(destBrightLabel);
        brightPanel.add(srcBrightSlider);
        brightPanel.add(destBrightSlider);

        c.add(brightPanel, BorderLayout.SOUTH);

        // Add the Preview Dialog (invisible at the start)
        previewDialog = new PreviewDialog(SwingUtilities.getWindowAncestor(previewButton), this, ivc);

        // Reveal to the world
        pack();
        setVisible(true);

    }

    // This method reads an Image object from a file indicated by
    // the string provided as the parameter.  The image is converted
    // here to a BufferedImage object, and that new object is the returned
    // value of this method.
    // The mediatracker in this method can throw an exception

    private BufferedImage readImage (String file) {

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

    //Saves what images the user is using, brightness level, and control point locations for both images
    //Based on a combination of code from oracle docs at
    //https://docs.oracle.com/javase/tutorial/displayCode.html?code=https://docs.oracle.com/javase/tutorial/uiswing/examples/components/FileChooserDemoProject/src/components/FileChooserDemo.java
    //and here
    //https://www.caveofprogramming.com/java/java-file-reading-and-writing-files-in-java.html
    private void saveConfig(){

        fc.setDialogTitle("Save Settings");

        // The name of the file to open
        int dialogSelection = fc.showSaveDialog(this);
        if( dialogSelection == JFileChooser.APPROVE_OPTION ) {
            
            File file = fc.getSelectedFile();
            String fileName = file.getAbsolutePath();
            System.out.println(fileName);
            try {
                // Assume default encoding.
                FileWriter fileWriter =
                        new FileWriter(fileName);

                // Always wrap FileWriter in BufferedWriter.
                BufferedWriter bufferedWriter =
                        new BufferedWriter(fileWriter);

                //Image1 Path
                bufferedWriter.write(srcPath);
                bufferedWriter.newLine();
                //Image2 Path
                bufferedWriter.write(destPath);
                bufferedWriter.newLine();
                //Intensity for Image 1

                bufferedWriter.write( Integer.toString( srcBrightSlider.getValue() ) );
                bufferedWriter.newLine();
                //Intensity for Image 2
                bufferedWriter.write( Integer.toString( destBrightSlider.getValue() ) );
                bufferedWriter.newLine();
                //Control Point Resolution
                bufferedWriter.write( Integer.toString( resolutionSlider.getValue() ) );
                bufferedWriter.newLine();
                //Length of Preview
                bufferedWriter.write( Integer.toString( lengthSlider.getValue() ) );
                bufferedWriter.newLine();

                Point[] srcControlPoints = srcView.getControlPoints();
                Point[] destControlPoints = destView.getControlPoints();

                //Image 1 Control Point X
                for(int i = 0; i < srcControlPoints.length; i ++){
                    bufferedWriter.write( Integer.toString( srcControlPoints[i].x ) );
                    bufferedWriter.newLine();
                }
                //Image 1 Control Point y
                for(int i = 0; i < srcControlPoints.length; i ++){
                    bufferedWriter.write( Integer.toString( srcControlPoints[i].y ) );
                    bufferedWriter.newLine();
                }
                //Image 2 Control Point X
                for(int i = 0; i < destControlPoints.length; i ++){
                    bufferedWriter.write( Integer.toString( destControlPoints[i].x ) );
                    bufferedWriter.newLine();
                }
                //Image 2 Control Point Y
                for(int i = 0; i < destControlPoints.length; i ++){
                    bufferedWriter.write( Integer.toString( destControlPoints[i].y ) ) ;
                    bufferedWriter.newLine();
                }


                // Always close files.
                bufferedWriter.close();
            }
            catch(IOException ex) {
                System.out.println(
                        "Error writing to file '"
                                + fileName + "'");

            }
        }

    }

    //Loads user configuration from a text file the user chooses
    private void loadConfig(){

        fc.setDialogTitle("Load Settings");

        int dialogSelection = fc.showOpenDialog(this);
        if (dialogSelection == JFileChooser.APPROVE_OPTION) {

            File file = fc.getSelectedFile();
            String fileName = file.getAbsolutePath();

            String line;
            ArrayList<String> lineList = new ArrayList<>();
            try {
                // FileReader reads text files in the default encoding.
                FileReader fileReader =
                        new FileReader(fileName);

                // Always wrap FileReader in BufferedReader.
                BufferedReader bufferedReader =
                        new BufferedReader(fileReader);

                while((line = bufferedReader.readLine()) != null) {
                    //System.out.println(line);
                    lineList.add(line);
                }

                // Always close files.
                bufferedReader.close();
            }
            catch(FileNotFoundException ex) {
                System.out.println(
                        "Unable to open file '" +
                                fileName + "'");
            }
            catch(IOException ex) {
                System.out.println(
                        "Error reading file '"
                                + fileName + "'");
            }

            //After File has been read into array, we can use the data.
            //Set Source Image
            srcPath = lineList.get(0);
            srcView.setImage( readImage( srcPath ) );

            //Set Destination Image
            destPath = lineList.get(1);
            destView.setImage( readImage( destPath ) );

            //Set Source Image Intensity
            srcBrightSlider.setValue( Integer.parseInt( lineList.get(2) ) );

            //Set Destination Image Intensity
            destBrightSlider.setValue( Integer.parseInt( lineList.get(3) ) );

            //Set Control Point Resolution
            int resolution = Integer.parseInt( lineList.get(4) );
            resolutionSlider.setValue( resolution );

            //Set Length of Preview
            lengthSlider.setValue( Integer.parseInt( lineList.get(5) ) );

            int srcXCoords[] = new int[resolution * resolution];
            int srcYCoords[] = new int[resolution * resolution];
            int destXCoords[] = new int[resolution * resolution];
            int destYCoords[] = new int[resolution * resolution];

            //Read In Image 1 X coords
            int i;
            int arrayPosition = 0;
            for(i = 6; i < (resolution * resolution) + 6 ; i++){
                srcXCoords[arrayPosition] = Integer.parseInt( lineList.get(i) );
                arrayPosition++;
            }

            //Read in Image 1 Ycoords
            int j;
            arrayPosition = 0;
            for(j = i; j < (resolution * resolution) + i ; j++){
                srcYCoords[arrayPosition] = Integer.parseInt( lineList.get(j) );
                arrayPosition++;
            }

            //Read in Image 2 X Coords
            int k;
            arrayPosition = 0;
            for(k = j; k < (resolution * resolution) + j ; k++){
                destXCoords[arrayPosition] = Integer.parseInt( lineList.get(k) );
                arrayPosition++;
            }

            //Read in Image 2 Y Coords
            int m;
            arrayPosition = 0;
            for(m = k; m < (resolution * resolution) + k ; m++){
                destYCoords[arrayPosition] = Integer.parseInt( lineList.get(m) );
                arrayPosition++;
            }

            ivc.loadControlPoints(srcXCoords,srcYCoords,destXCoords,destYCoords);
        }
    }

    //Initialize and run class
    public static void main(String args[]) {
        JMorph j = new JMorph();
        j.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) { System.exit(0); }
        });
    }
}
