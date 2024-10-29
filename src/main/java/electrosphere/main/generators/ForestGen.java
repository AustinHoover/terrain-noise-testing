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
import io.github.studiorailgun.NoiseUtils;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Generates forests
 */
public class ForestGen implements Generator {

    //formatter for decimals
    NumberFormat formatter = new DecimalFormat("#0.00");

    //generate heightmap
    float[][] heightmap = new float[Main.HEIGHTMAP_DIM][Main.HEIGHTMAP_DIM];

    //Scale of the noise
    double scale = 0.1;

    //Relaxation factor
    double relaxationFactor = 0.5;

    //The threshold
    double threshold = 0.1;

    /**
     * Generates a heightmap
     * @param heightmap The array to fill
     */
    void generateHeightmap(float[][] heightmap){
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
    public float getHeight(long SEED, float x, float y){
        return (float)NoiseUtils.relaxedPointGen(x * scale, y * scale, relaxationFactor, threshold);
    }


    @Override
    public JPanel getDisplay() {
        JPanel rVal = new JPanel();
        rVal.setLayout(new GridLayout(1, 2));


        //updated allowed height
        float MIN_HEIGHT = 0;
        float MAX_HEIGHT = 1;
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

        //Scale slider
        {
            JLabel label = new JLabel("Scale (500)");
            JSlider slider = new JSlider(0, 1000, 500);
            slider.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
                scale = slider.getValue() / 1000.0f;
                generateHeightmap(heightmap);
                label.setText("Scale (" + formatter.format(scale) + ")");
                rVal.repaint();
            }});
            JPanel combined = new JPanel();
            combined.add(label);
            combined.add(slider);
            controlsPanel.add(combined);
        }

        //Relaxation factor slider
        {
            JLabel label = new JLabel("Relaxation Factor (600)");
            JSlider slider = new JSlider(0, 1000, 600);
            slider.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
                relaxationFactor = slider.getValue() / 1000.0f;
                generateHeightmap(heightmap);
                label.setText("Relaxation Factor (" + formatter.format(relaxationFactor) + ")");
                rVal.repaint();
            }});
            JPanel combined = new JPanel();
            combined.add(label);
            combined.add(slider);
            controlsPanel.add(combined);
        }

        //Threshold slider
        {
            JLabel label = new JLabel("Threshold (100)");
            JSlider slider = new JSlider(0, 1000, 100);
            slider.addChangeListener(new ChangeListener() {public void stateChanged(ChangeEvent e) {
                threshold = slider.getValue() / 1000.0f;
                generateHeightmap(heightmap);
                label.setText("Threshold (" + formatter.format(threshold) + ")");
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
