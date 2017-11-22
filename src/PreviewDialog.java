import javax.swing.*;
import java.awt.*;

public class PreviewDialog extends JDialog {

    private Point[] gridCellCoords;
    private Point[] srcPoints;
    private Point[] destPoints;

    public PreviewDialog(Window owner, Dimension size, Point[] gridCellCoords, Point[] srcPoints, Point[] destPoints) {
        super(owner, "Preview", ModalityType.APPLICATION_MODAL);

        System.out.println("I");
        setPreferredSize(size);
        this.gridCellCoords = gridCellCoords;
        this.srcPoints = srcPoints;
        this.destPoints = destPoints;

        pack();
        setVisible(true);
    }

}
