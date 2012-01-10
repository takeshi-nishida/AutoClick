package autoclick;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import javax.swing.JPanel;

public class SSView extends JPanel {

//  private double scale;
//  private Dimension d;
  private BufferedImage ss;
  private Rectangle r;

  public SSView(){
//    this.scale = scale;
    this.r = new Rectangle();
    MouseHandler mh = new MouseHandler();
    addMouseListener(mh);
    addMouseMotionListener(mh);

  }

  public void setImage(BufferedImage ss){
    this.ss = ss;
//    this.d = new Dimension((int) (ss.getWidth() * scale), (int) (ss.getHeight() * scale));
//    setSize(d);
//    setPreferredSize(d);
  }

  public BufferedImage getTemplate(){
    if(ss == null || r.getWidth() == 0 || r.getHeight() == 0) return null;

//    return ss.getSubimage((int) (r.x / scale), (int) (r.y / scale), (int) (r.width / scale), (int) (r.height / scale));
//    return ss.getSubimage(r.x, r.y, r.width, r.height); // BufferedImage.getSubimage shares data with the original image

    BufferedImage result = new BufferedImage(r.width, r.height, ss.getType());
    BufferedImage src = ss.getSubimage(r.x, r.y, r.width, r.height);
    result.getGraphics().drawImage(src, 0, 0, null);
    return result;
  }
  
  @Override
  public void paintComponent(Graphics g){
    super.paintComponent(g);

    if(ss != null){
      g.drawImage(ss, 0, 0, getWidth(), getHeight(), this);
    }

    if(r.width > 0 && r.height > 0){
      g.drawRect(r.x, r.y, r.width, r.height);
    }
  }


  class MouseHandler extends MouseAdapter {
    private Point dragStart;

    @Override
    public void mouseMoved(MouseEvent e){
      setToolTipText(e.getPoint().toString());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
      r.setFrameFromDiagonal(dragStart, e.getPoint());
      repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e){
      r.setFrameFromCenter(e.getX(), e.getY(), e.getX() - 8, e.getY() - 8);
      repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
      dragStart = e.getPoint();
      r.setBounds(dragStart.x, dragStart.y, 0, 0);
      repaint();
    }    
  }
}
