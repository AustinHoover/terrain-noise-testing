package electrosphere.main;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

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
        {0.05, 1.0},
        {0.1, 1.0},
        {0.3, 1.0},
    };

    //the different scales of noise to sample from
    static final double[][] GRAD_NOISE = new double[][]{
        {0.01, 2.0},
        {0.02, 2.0},
        {0.05, 1.0},
        {0.1, 1.0},
        {0.3, 1.0},
    };

    //distance from origin to sample for gradient calculation
    static float GRADIENT_DIST = 0.01f;

    //param for controlling how pointer the initial layers are
    static float GRAD_INFLUENCE_DROPOFF = 0.35f;

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
                GRAD_INFLUENCE_DROPOFF = slider.getValue() / 1000.0f;
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
                GRADIENT_DIST = slider.getValue() / 400.0f;
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
        rVal = gradientHeight(SEED, x, y);
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

}
