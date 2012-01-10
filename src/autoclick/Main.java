/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package autoclick;

import javax.swing.JFrame;

/**
 *
 * @author tnishida
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
      JFrame frame = new MainFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      frame.setVisible(true);
    }

}
