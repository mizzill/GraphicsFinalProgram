public class ImageViewController {
    int selected;
    ImageView src,dest;
    //Constructor
    public ImageViewController(ImageView src, ImageView dest){
        selected = -1;
        this.src = src;
        this.dest = dest;
    }

    public void updateSelected(int clickedPoint){
        this.selected = clickedPoint;
        src.updateSelectedExternally(clickedPoint);
        dest.updateSelectedExternally(clickedPoint);

        src.repaint();
        dest.repaint();
    }
}
