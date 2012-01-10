package autoclick;

import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

public class ProgramNode {

  private List<BufferedImage> templates;
  private boolean doClick;

  public ProgramNode(List<BufferedImage> templates, boolean doClick) {
    this.templates = templates;
    this.doClick = doClick;
  }

  public boolean isDoClick() {
    return doClick;
  }

  public List<BufferedImage> getTemplates() {
    return Collections.unmodifiableList(templates);
  }

  public boolean execute(BufferedImage ss, Robot robot) {
    for (BufferedImage t : templates) {
      long l = System.currentTimeMillis();
      Point p = TemplateFinder.lookup(ss, t);
      System.out.println("lookup:" + (System.currentTimeMillis() - l) + "ms");
      if (p != null) {
        p.move(p.x + t.getWidth() / 2, p.y + t.getHeight() / 2);
        robot.mouseMove(p.x, p.y);
        if (doClick) {
          robot.mousePress(InputEvent.BUTTON1_MASK);
          robot.mouseRelease(InputEvent.BUTTON1_MASK);
        }
        return true;
      }
    }
    return false;
  }
}
