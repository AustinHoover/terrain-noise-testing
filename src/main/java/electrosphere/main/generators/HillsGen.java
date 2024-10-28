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
 * Generates a hilly heightmap
 */
public class HillsGen implements Generator {

    //generate heightmap
    float[][] heightmap = new float[Main.HEIGHTMAP_DIM][Main.HEIGHTMAP_DIM];

    /**
     * The different scales of noise to sample from
     */
    static final double[][] GRAD_NOISE = new double[][]{
        {0.01, 2.0},
        {0.02, 2.0},
        {0.05, 1.0},
        {0.1, 1.0},
        {0.3, 1.0},
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
        return gradientHeight(SEED, x, y);
    }


    /**
     * Applies a gradient approach to heightfield generation
     * @param SEED The seed
     * @param x The x value
     * @param y The y value
     * @return The elevation at x,y
     */
    static float gradientHeight(long SEED, float x, float y){
        float rVal = 0;

        float gradXAccum = 0;
        float gradYAccum = 0;
        for(int n = 0; n < GRAD_NOISE.length; n++){
            //get noise samples
            float noiseOrigin = (float)(OpenSimplex2S.noise2_ImproveX(SEED, x * GRAD_NOISE[n][0], y * GRAD_NOISE[n][0]) * GRAD_NOISE[n][1]);
            float noiseX = (float)(OpenSimplex2S.noise2_ImproveX(SEED, x * GRAD_NOISE[n][0] + GRADIENT_DIST, y * GRAD_NOISE[n][0]) * GRAD_NOISE[n][1]);
            float noiseY = (float)(OpenSimplex2S.noise2_ImproveX(SEED, x * GRAD_NOISE[n][0], y * GRAD_NOISE[n][0] + GRADIENT_DIST) * GRAD_NOISE[n][1]);
            //calculate gradient accumulation
            float gradX = (noiseX - noiseOrigin) / GRADIENT_DIST;
            float gradY = (noiseY - noiseOrigin) / GRADIENT_DIST;
            gradXAccum = gradXAccum + gradX;
            gradYAccum = gradYAccum + gradY;
            //determine current noise's influence based on gradient
            float gradientMagnitude = (float)Math.sqrt(gradXAccum * gradXAccum + gradYAccum * gradYAccum);
            float influence = 1.0f / (1.0f + gradientMagnitude * GRAD_INFLUENCE_DROPOFF);

            //add to height
            rVal = rVal + (float)(OpenSimplex2S.noise2_ImproveX(SEED, x * GRAD_NOISE[n][0], y * GRAD_NOISE[n][0]) * GRAD_NOISE[n][1]) * influence;
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
        for(int n = 0; n < GRAD_NOISE.length; n++){
            MIN_HEIGHT = MIN_HEIGHT - (float)GRAD_NOISE[n][1];
            MAX_HEIGHT = MAX_HEIGHT + (float)GRAD_NOISE[n][1];
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
        {
            JPanel containingPanel = new JPanel();
            JButton buttonRegenerate = new JButton("New Seed");
            buttonRegenerate.addActionListener(new ActionListener() {public void actionPerformed(ActionEvent e) {
                Main.SEED = new Random().nextInt();
                generateHeightmap(heightmap);
                rVal.repaint();
            }});
            containingPanel.add(buttonRegenerate);
            controlsPanel.add(containingPanel);
        }

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
