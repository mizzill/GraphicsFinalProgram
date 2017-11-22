import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PreviewDialog extends JDialog {

    private Timer animTimer;
    private PreviewImagePanel previewImagePanel;

    // How much of the animation has been completed
    private double percentCompletion;

    // Constructor
    public PreviewDialog(Window owner, JMorph morph, ImageViewController ivc) {

        // Make this dialog a popup modal
        super(owner, "Preview", ModalityType.APPLICATION_MODAL);

        // Get information from the ImageViewController about the source image view and pass it to a new image panel
        previewImagePanel = new PreviewImagePanel(
                ivc.gridRows,
                ivc.gridCols,
                ivc.src.gridCellWidth,
                ivc.src.gridCellHeight,
                ivc.src.controlPoints,
                ivc.dest.controlPoints,
                ivc.src.gridCellCoords,
                ivc.pointRadius
        );
        add(previewImagePanel);

        // Set up the animation timer
        animTimer = new Timer(1000 / morph.FPS, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                percentCompletion += 1.0 / (morph.FPS * morph.animationLength);
                if (percentCompletion >= 1) {
                    percentCompletion = 1;
                    animTimer.stop();
                }
                previewImagePanel.update(percentCompletion);
            }
        });

        // Pack the dialog
        pack();
    }

    // Reveals the dialog and resets the animation
    public void revealPreview() {
        percentCompletion = 0;
        previewImagePanel.reset();
        //animTimer.restart();
        setVisible(true);
    }

}
