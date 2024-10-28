package electrosphere.main;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import electrosphere.main.generators.HillsGen;
import electrosphere.main.utils.OpenSimplex2S;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

/**
 *
 * @author amaterasu
 */
public class Main {

    //heightmap dimension
    static final int HEIGHTMAP_DIM = 256;

    //seed for generation
    static int SEED = 0;

    //resolution of image
    static final int RESO = 2;

    //size of frame component
    static final int FRAME_DIM = HEIGHTMAP_DIM * RESO;

    //the different scales of noise to sample from
    static final double[][] NOISE_SCALES = new double[][]{
        {0.01, 3.0},
        {0.02, 2.0},
        {0.05, 0.8},
        {0.1, 0.3},
        {0.3, 0.2},
    };

    //dimension of color space
    static final int MIN_COLOR = 0;
    static final int MAX_COLOR = 255;

    //heights
    static float MIN_HEIGHT = 0;
    static float MAX_HEIGHT = 0;
    
    public static void main(String args[]){
        //generate heightmap
        float[][] heightmap = new float[HEIGHTMAP_DIM][HEIGHTMAP_DIM];
        generateHeightmap(heightmap);

        //dimension of heightmap space
        for(int n = 0; n < NOISE_SCALES.length; n++){
            MIN_HEIGHT = MIN_HEIGHT - (float)NOISE_SCALES[n][1];
            MAX_HEIGHT = MAX_HEIGHT + (float)NOISE_SCALES[n][1];
        }

        //graphics
        JFrame frame = new JFrame();
        frame.add(new JComponent() {
            @Override
            public void paint(Graphics g){
                 for(int x = 0; x < HEIGHTMAP_DIM; x++){
                     for(int y = 0; y < HEIGHTMAP_DIM; y++){
                         int colorVal = heightToColor(heightmap[x][y]);
                         g.setColor(new Color(colorVal, colorVal, colorVal));
                         g.fillRect(x * RESO, y * RESO, RESO, RESO);
                     }
                 }
            }

            @Override
            public Dimension getPreferredSize(){
                return new Dimension(FRAME_DIM, FRAME_DIM);
            }
        });

        //regenerate button
        JButton buttonRegenerate = new JButton("New Seed");
        buttonRegenerate.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            SEED = new Random().nextInt();
            generateHeightmap(heightmap);
            frame.repaint();
        }});
        frame.add(buttonRegenerate);

        //gradient influence dropoff slider
        {
            JLabel label = new JLabel("Gradient Influence Dropoff");
            frame.add(label);
            JSlider slider = new JSlider(0, 2000, 350);
            slider.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
                HillsGen.GRAD_INFLUENCE_DROPOFF = slider.getValue() / 1000.0f;
                generateHeightmap(heightmap);
                frame.repaint();
            }});
            JPanel combined = new JPanel();
            combined.add(label);
            combined.add(slider);
            frame.add(combined);
        }

        //gradient sampler distance slider
        {
            JLabel label = new JLabel("Gradient Sampler Distance");
            frame.add(label);
            JSlider slider = new JSlider(0, 100, 60);
            slider.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
                HillsGen.GRADIENT_DIST = slider.getValue() / 400.0f;
                generateHeightmap(heightmap);
                frame.repaint();
            }});
            JPanel combined = new JPanel();
            combined.add(label);
            combined.add(slider);
            frame.add(combined);
        }

        //setup frame
        frame.setLayout(new FlowLayout());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

    }

    /**
     * Converts a height to a corresponding color
     */
    static int heightToColor(float height){
        int rVal = (int)((height - MIN_HEIGHT) / (MAX_HEIGHT - MIN_HEIGHT) * (MAX_COLOR - MIN_COLOR) + MIN_COLOR);
        if(rVal > MAX_COLOR || rVal < MIN_COLOR){
            System.out.println(rVal + " " + height);
        }
        return rVal;
    }

    /**
     * Generates a heightmap
     * @param heightmap The array to fill
     */
    static void generateHeightmap(float[][] heightmap){
        for(int x = 0; x < HEIGHTMAP_DIM; x++){
            for(int y = 0; y < HEIGHTMAP_DIM; y++){
                heightmap[x][y] = getHeight(SEED, x, y);
            }
        }
    }

    /**
     * Gets the height at a given x,y position
     * @param SEED The seed
     * @param x The x position
     * @param y The y position
     * @return The height
     */
    static float getHeight(long SEED, float x, float y){
        float rVal = 0;
        rVal = sampleAllNoise(SEED, x, y);
        return rVal;
    }

    /**
     * Samples all noise values directly
     * @param SEED The seed
     * @param x The x value
     * @param y The y value
     * @return The elevation at x,y
     */
    static float sampleAllNoise(long SEED, float x, float y){
        float rVal = 0;
        for(int n = 0; n < NOISE_SCALES.length; n++){
            rVal = rVal + (float)(OpenSimplex2S.noise2_ImproveX(SEED, x * NOISE_SCALES[n][0], y * NOISE_SCALES[n][0]) * NOISE_SCALES[n][1]);
        }
        return rVal;
    }

}
