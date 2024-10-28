package electrosphere.main.generators;

import electrosphere.main.utils.OpenSimplex2S;

/**
 * Generates a hilly heightmap
 */
public class HillsGen {

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
    
}
