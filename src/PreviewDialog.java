import javax.swing.*;
import java.awt.*;

public class PreviewDialog extends JDialog {

    private JMorph jMorph;
    private Timer animTimer;
    private PreviewImagePanel previewImagePanel;

    // How much of the animation has been completed
    private double percentCompletion;

    // Constructor
    public PreviewDialog(Window owner, JMorph morph, ImageViewController ivc) {

        // Make this dialog a popup modal
        super(owner, "Preview", ModalityType.APPLICATION_MODAL);

        // Get reference to the JMorph instance
        this.jMorph = morph;

        // Create and add the preview image panel
        previewImagePanel = new PreviewImagePanel(ivc);
        add(previewImagePanel);

        // Set up the animation timer
        animTimer = new Timer(1000 / jMorph.FPS, e -> {
            percentCompletion += 1.0 / (jMorph.FPS * jMorph.animationLength);
            if (percentCompletion >= 1) {
                percentCompletion = 1;
                animTimer.stop();
            }
            previewImagePanel.update(percentCompletion);
        });

        // Pack the dialog
        pack();
    }

    // Reveals the dialog and resets the animation
    public void revealPreview() {
        percentCompletion = 0;
        previewImagePanel.reset();

        animTimer.setDelay(1000 / jMorph.FPS);
        animTimer.start();

        setVisible(true);
    }

}
