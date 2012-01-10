package autoclick;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ListModel;
import javax.swing.Timer;

public class MainFrame extends JFrame {

  private Rectangle screenRect;
  private int clickCount;
  private Timer timer;
  private Robot robot;

  // Data -------------------------------------------------
  private DefaultListModel templates;
  private DefaultListModel programs;

  // Components -------------------------------------------
  private SSView ssView;
  private JList templateList, programList;

  public MainFrame() {
    super("Image-based auto clicker");
    
    Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
    screenRect = new Rectangle(0, 0, d.width, d.height);
    templates = new DefaultListModel();
    programs = new DefaultListModel();
    timer = new Timer(1000, new ActionListener() {
      public void actionPerformed(ActionEvent e) { doTimer(); }
    });
    try {
      robot = new Robot();
      robot.setAutoDelay(100);
    } catch (AWTException ex) { }

    initComponents();
  }

  private void initComponents() {
    setResizable(false);
    addWindowFocusListener(new WindowAdapter(){
      @Override
      public void windowGainedFocus(WindowEvent e){
        if(timer.isRunning()) stopTimer();
      }
    });

    Box vBox = Box.createVerticalBox();

    ssView = new SSView();
    ssView.setPreferredSize(new Dimension(screenRect.width, screenRect.height));
    JScrollPane sp = addWithScrollPane(vBox, ssView);
    sp.setPreferredSize(new Dimension(screenRect.width / 2, screenRect.height / 2));

    // ------------------------------------------------------------------------
    Box hBox = Box.createHorizontalBox();
    Box vBox2 = Box.createVerticalBox();

    JButton button = new JButton("Capture screen");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { captureScreen(); }
    });
    alignXandAdd(vBox2, button, CENTER_ALIGNMENT);

    button = new JButton("Create template");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { createTemplate(); }
    });
    alignXandAdd(vBox2, button, CENTER_ALIGNMENT);

    hBox.add(vBox2);

    templateList = createHorizontalJList(templates);
    templateList.setCellRenderer(new ListImageRenderer());
    addWithScrollPane(hBox, templateList);
    vBox.add(hBox);

    // ------------------------------------------------------------------------
    hBox = Box.createHorizontalBox();
    vBox2 = Box.createVerticalBox();

    button = new JButton("Add CLICK");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { addNode(true); }
    });
    alignXandAdd(vBox2, button, CENTER_ALIGNMENT);

    button = new JButton("Add WAIT");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { addNode(false); }
    });
    alignXandAdd(vBox2, button, CENTER_ALIGNMENT);

    button = new JButton("Run");
    button.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { toggleTimer((JButton) e.getSource()); }
    });
    alignXandAdd(vBox2, button, CENTER_ALIGNMENT);

    hBox.add(vBox2);

    programList = createHorizontalJList(programs);
//    programList.setCellRenderer( );
    addWithScrollPane(hBox, programList);

    vBox.add(hBox);

    add(vBox);
  }

  private void captureScreen() {
    setExtendedState(Frame.ICONIFIED);
    robot.delay(500);
    BufferedImage ss = robot.createScreenCapture(screenRect);
    setExtendedState(Frame.NORMAL);
    ssView.setImage(ss);
    pack();
  }

  private void createTemplate(){
    BufferedImage t = ssView.getTemplate();
    if(t != null){
      templates.addElement(t);
    }
  }

  private void addNode(boolean doClick){
    List<BufferedImage> ts = new ArrayList<BufferedImage>();
    for(Object o : templateList.getSelectedValues()){
      ts.add((BufferedImage) o);
    }
    if(ts.size() > 0){
      programs.addElement(new ProgramNode(ts, doClick));
    }
  }

  private void toggleTimer(JButton button) {
    if (timer.isRunning()) {
      stopTimer();
    } else if(!programs.isEmpty()){
      clickCount = 0;
      setExtendedState(Frame.ICONIFIED);
      timer.start();
    } else{
      JOptionPane.showMessageDialog(this, "プログラムが空です");
    }
  }

  private void doTimer() {
    BufferedImage image = robot.createScreenCapture(screenRect);
    ProgramNode program = (ProgramNode) programs.getElementAt(clickCount % programs.size());

    if(program.execute(image, robot)){      
      if (clickCount++ > 100) {
        stopTimer();
      }
    }
  }

  private void stopTimer(){
    timer.stop();
    setExtendedState(Frame.NORMAL);
    JOptionPane.showMessageDialog(this, "終了しました (count=" + clickCount + ")");
  }

  private void alignXandAdd(Box box, JComponent c, float alignment){
    c.setAlignmentX(alignment);
    box.add(c);
  }

  private JScrollPane addWithScrollPane(Box box, JComponent c){
    JScrollPane sp = new JScrollPane(c);
    box.add(sp);
    return sp;
  }

  private JList createHorizontalJList(ListModel model){
    JList list = new JList(model);
    list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
    list.setVisibleRowCount(1);
    list.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
    return list;
  }
}
