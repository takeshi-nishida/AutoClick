package autoclick;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.ListCellRenderer;
import javax.swing.border.Border;
import util.ImageUtilities;

public class ListImageRenderer extends JPanel implements ListCellRenderer {

  public static final Dimension size = new Dimension(50, 50);
  public final Border selectedBorder, normalBorder;
  private BufferedImage img;

  public ListImageRenderer(){
    normalBorder = BorderFactory.createLineBorder(Color.black);
    selectedBorder = BorderFactory.createLineBorder(Color.red);
    setPreferredSize(size);
  }

  public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
    img = (BufferedImage) value;
    setBorder(isSelected ? selectedBorder : normalBorder);
//    Dimension d = new Dimension(img.getWidth(), img.getHeight());
//    setSize(d);
//    setPreferredSize(d);
    return this;
  }

  @Override
  public void paintComponent(Graphics g) {
    super.paintComponent(g);

    if (img != null) {
      double scale = ImageUtilities.getFittingScaleFactor(img, getWidth(), getHeight());
      g.drawImage(img, 0, 0, (int) (img.getWidth() * scale), (int) (img.getHeight() * scale), this);
    }
  }
}
