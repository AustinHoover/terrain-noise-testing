package electrosphere.main;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import electrosphere.main.generators.HillsGen;
import electrosphere.main.generators.PlainsGen;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author amaterasu
 */
public class Main {

    //heightmap dimension
    public static final int HEIGHTMAP_DIM = 256;

    //seed for generation
    public static int SEED = 0;

    //resolution of image
    public static final int RESO = 2;

    //size of frame component
    public static final int FRAME_DIM = HEIGHTMAP_DIM * RESO;

    /**
     * Preferred size of the window
     */
    public static final int WINDOW_PREFERRED_SIZE = 1000;
    
    /**
     * Main
     */
    public static void main(String args[]){
        //graphics
        JFrame frame = new JFrame();
        

        //contains the display + controls for the current generator
        JPanel generatorContentContainer = new JPanel();
        //contains all buttons to switch between different generators
        JPanel generatorSelectionButtons = new JPanel();

        //hills generator
        {
            HillsGen hillsGen = new HillsGen();
            JButton selectbutton = new JButton("Hills");
            selectbutton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
                generatorContentContainer.removeAll();
                generatorContentContainer.add(hillsGen.getDisplay());
                frame.setLayout(new GridLayout(2, 1));
                frame.pack();
                frame.repaint();
            }});
            generatorSelectionButtons.add(selectbutton);
        }

        //plains generator
        {
            PlainsGen plainsGen = new PlainsGen();
            JButton selectbutton = new JButton("Plains");
            selectbutton.addActionListener(new ActionListener(){public void actionPerformed(ActionEvent e) {
                generatorContentContainer.removeAll();
                generatorContentContainer.add(plainsGen.getDisplay());
                frame.setLayout(new GridLayout(2, 1));
                frame.pack();
                frame.repaint();
            }});
            generatorSelectionButtons.add(selectbutton);
        }



        //add top level panels
        frame.add(generatorSelectionButtons);
        frame.add(generatorContentContainer);
        frame.setLayout(new GridLayout(2, 1));


        //setup frame
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }

}
