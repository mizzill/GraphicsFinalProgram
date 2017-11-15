import javax.swing.*;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;

public class MyImageObj extends JLabel {

    // instance variable to hold the buffered image
    private BufferedImage bim=null;
    private BufferedImage filteredbim=null;

    //  tell the paintcomponent method what to draw
    private boolean showfiltered=false;


    // Default constructor
    public MyImageObj() {
    }

    // This constructor stores a buffered image passed in as a parameter
    public MyImageObj(BufferedImage img) {
        bim = img;
        filteredbim = new BufferedImage
                (bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));

        this.repaint();
    }

    // This mutator changes the image by resetting what is stored
    // The input parameter img is the new image;  it gets stored as an
    //     instance variable
    public void setImage(BufferedImage img) {
        if (img == null) return;
        bim = img;
        filteredbim = new BufferedImage
                (bim.getWidth(), bim.getHeight(), BufferedImage.TYPE_INT_RGB);
        setPreferredSize(new Dimension(bim.getWidth(), bim.getHeight()));
        showfiltered=false;
        this.repaint();
    }

    // accessor to get a handle to the bufferedimage object stored here
    public BufferedImage getImage() {
        return bim;
    }



    //  show current image by a scheduled call to paint()
    public void showImage() {
        if (bim == null) return;
        showfiltered=false;
        this.repaint();
    }

    //  get a graphics context and show either filtered image or
    //  regular image
    public void paintComponent(Graphics g) {
        Graphics2D big = (Graphics2D) g;
        if (showfiltered)
            big.drawImage(filteredbim, 0, 0, this);
        else
            big.drawImage(bim, 0, 0, this);
    }
}