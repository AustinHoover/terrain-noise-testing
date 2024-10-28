package electrosphere.main.generators;

import java.util.Random;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import electrosphere.main.Main;
import electrosphere.main.utils.GraphicsUtils;
import electrosphere.main.utils.OpenSimplex2S;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Generates plains heightmaps
 */
public class PlainsGen implements Generator {
    
    //generate heightmap
    float[][] heightmap = new float[Main.HEIGHTMAP_DIM][Main.HEIGHTMAP_DIM];

    //the different scales of noise to sample from
    static final double[][] NOISE_SCALES = new double[][]{
        {0.01, 3.0},
        {0.02, 2.0},
        {0.05, 0.8},
        {0.1, 0.3},
        {0.3, 0.2},
    };

    //distance from origin to sample for gradient calculation
    public static float GRADIENT_DIST = 0.01f;

    //param for controlling how pointer the initial layers are
    public static float GRAD_INFLUENCE_DROPOFF = 0.35f;


    /**
     * Generates a heightmap
     * @param heightmap The array to fill
     */
    static void generateHeightmap(float[][] heightmap){
        for(int x = 0; x < Main.HEIGHTMAP_DIM; x++){
            for(int y = 0; y < Main.HEIGHTMAP_DIM; y++){
                heightmap[x][y] = getHeight(Main.SEED, x, y);
            }
        }
    }


    /**
     * Gets the height at a given position for this generation approach
     * @param SEED The seed
     * @param x The x position
     * @param y The y position
     * @return The height
     */
    public static float getHeight(long SEED, float x, float y){
        return sampleAllNoise(SEED, x, y);
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


    @Override
    public JPanel getDisplay() {
        JPanel rVal = new JPanel();
        rVal.setLayout(new GridLayout(1, 2));


        //updated allowed height
        float MIN_HEIGHT = 0;
        float MAX_HEIGHT = 0;
        for(int n = 0; n < NOISE_SCALES.length; n++){
            MIN_HEIGHT = MIN_HEIGHT - (float)NOISE_SCALES[n][1];
            MAX_HEIGHT = MAX_HEIGHT + (float)NOISE_SCALES[n][1];
        }
        GraphicsUtils.setHeightRange(MIN_HEIGHT, MAX_HEIGHT);

        //generate the heightmap
        generateHeightmap(heightmap);

        //graphics
        rVal.add(new JComponent() {
            @Override
            public void paint(Graphics g){
                 for(int x = 0; x < Main.HEIGHTMAP_DIM; x++){
                     for(int y = 0; y < Main.HEIGHTMAP_DIM; y++){
                         int colorVal = GraphicsUtils.heightToColor(heightmap[x][y]);
                         g.setColor(new Color(colorVal, colorVal, colorVal));
                         g.fillRect(x * Main.RESO, y * Main.RESO, Main.RESO, Main.RESO);
                     }
                 }
            }

            @Override
            public Dimension getPreferredSize(){
                return new Dimension(Main.FRAME_DIM, Main.FRAME_DIM);
            }
        });

        //panel that contains all controls
        JPanel controlsPanel = new JPanel();
        controlsPanel.setLayout(new GridLayout(2, 2));
        rVal.add(controlsPanel);

        //regenerate button
        JButton buttonRegenerate = new JButton("New Seed");
        buttonRegenerate.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
            Main.SEED = new Random().nextInt();
            generateHeightmap(heightmap);
            rVal.repaint();
        }});
        controlsPanel.add(buttonRegenerate);

        //gradient influence dropoff slider
        {
            JLabel label = new JLabel("Gradient Influence Dropoff");
            JSlider slider = new JSlider(0, 2000, 350);
            slider.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
                HillsGen.GRAD_INFLUENCE_DROPOFF = slider.getValue() / 1000.0f;
                generateHeightmap(heightmap);
                rVal.repaint();
            }});
            JPanel combined = new JPanel();
            combined.add(label);
            combined.add(slider);
            controlsPanel.add(combined);
        }

        //gradient sampler distance slider
        {
            JLabel label = new JLabel("Gradient Sampler Distance");
            JSlider slider = new JSlider(0, 100, 60);
            slider.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
                HillsGen.GRADIENT_DIST = slider.getValue() / 400.0f;
                generateHeightmap(heightmap);
                rVal.repaint();
            }});
            JPanel combined = new JPanel();
            combined.add(label);
            combined.add(slider);
            controlsPanel.add(combined);
        }

        return rVal;
    }

}
