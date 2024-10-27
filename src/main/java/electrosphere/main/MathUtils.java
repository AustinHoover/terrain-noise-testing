package electrosphere.main;

/**
 * Utility functions that perform math
 */
public class MathUtils {

    /**
     * The magnitude of the random oscillator
     */
    static final float RANDOM_MAGNITUDE = 100000.0f;

    /**
     * Half of pi
     */
    static final double HALF_PI = Math.PI / 2.0;

    /**
     * Values for the vector dot'd to create a pseudorandom number
     */
    static final double RAND_VEC_X_VAL = 111.154315;
    static final double RAND_VEC_Y_VAL = 123.631631;
    static final double RAND_VEC_Z_VAL = 117.724545;
    static final double RAND_VEC_W_VAL = 141.724545;

    /**
     * x offsets for a 3x3 kernel
     */
    static final int[] KERNEL_3_3_X = new int[]{
        1, 0, -1,
        1, 0, -1,
        1, 0, -1,
    };

    /**
     * y offsets for a 3x3 kernel
     */
    static final int[] KERNEL_3_3_Y = new int[]{
         1,  1,  1,
         0,  0,  0,
        -1, -1, -1,
    };

    /**
     * x offsets for a 3x3x3 kernel
     */
    static final int[] KERNEL_3_3_3_X = new int[]{
        1, 0, -1,
        1, 0, -1,
        1, 0, -1,
        1, 0, -1,
        1, 0, -1,
        1, 0, -1,
        1, 0, -1,
        1, 0, -1,
        1, 0, -1,
    };

    /**
     * y offsets for a 3x3x3 kernel
     */
    static final int[] KERNEL_3_3_3_Y = new int[]{
        1,  1,  1,
        0,  0,  0,
       -1, -1, -1,
        1,  1,  1,
        0,  0,  0,
       -1, -1, -1,
        1,  1,  1,
        0,  0,  0,
       -1, -1, -1,
    };

    /**
     * z offsets for a 3x3x3 kernel
     */
    static final int[] KERNEL_3_3_3_Z = new int[]{
        1,  1,  1,
        1,  1,  1,
        1,  1,  1,
        0,  0,  0,
        0,  0,  0,
        0,  0,  0,
       -1, -1, -1,
       -1, -1, -1,
       -1, -1, -1,
    };

    /**
     * Calculates the fractional component of a number
     * @param num The number
     * @return The fractional component
     */
    public static float fract(float num){
        return num - (float)Math.floor(num);
    }

    /**
     * Calculates the fractional component of a number
     * @param num The number
     * @return The fractional component
     */
    public static double fract(double num){
        return num - Math.floor(num);
    }

    /**
     * Calculates a random number
     * @param in The input
     * @return The output
     */
    public static double rand(double in){
        return fract(Math.sin(in) * RANDOM_MAGNITUDE);
    }

    /**
     * Calculates a random number
     * @param x The x input
     * @param y The y input
     * @return The output
     */
    public static double rand(double x, double y){
        return fract(Math.sin(dot(x,y,RAND_VEC_X_VAL,RAND_VEC_Y_VAL)) * RANDOM_MAGNITUDE);
    }

    /**
     * Calculates a random number
     * @param x The x input
     * @param y The y input
     * @param z The z input
     * @return The output
     */
    public static double rand(double x, double y, double z){
        return fract(Math.sin(dot(x,y,z,RAND_VEC_X_VAL,RAND_VEC_Y_VAL,RAND_VEC_Z_VAL)) * RANDOM_MAGNITUDE);
    }

    /**
     * Calculates a random number
     * @param x The x input
     * @param y The y input
     * @param z The z input
     * @param w The w input
     * @return The output
     */
    public static double rand(double x, double y, double z, double w){
        return fract(Math.sin(dot(x,y,z,w,RAND_VEC_X_VAL,RAND_VEC_Y_VAL,RAND_VEC_Z_VAL,RAND_VEC_W_VAL)) * RANDOM_MAGNITUDE);
    }

    /**
     * Calculates the dot product of a 2d vector
     * @param x1 The x coordinate of the first vector
     * @param y1 The y coordinate of the first vector
     * @param x2 The x coordinate of the second vector
     * @param y2 The y coordinate of the second vector
     * @return The dot product of the vectors
     */
    public static double dot(double x1, double y1, double x2, double y2){
        return (x1*x2) + (y1*y2);
    }

    /**
     * Calculates the dot product of a 3d vector
     * @param x1 The x coordinate of the first vector
     * @param y1 The y coordinate of the first vector
     * @param z1 The y coordinate of the first vector
     * @param x2 The x coordinate of the second vector
     * @param y2 The y coordinate of the second vector
     * @param z2 The y coordinate of the first vector
     * @return The dot product of the vectors
     */
    public static double dot(double x1, double y1, double z1, double x2, double y2, double z2){
        return (x1*x2) + (y1*y2) + (z1*z2);
    }

    /**
     * Calculates the dot product of a 4d vector
     * @param x1 The x coordinate of the first vector
     * @param y1 The y coordinate of the first vector
     * @param z1 The y coordinate of the first vector
     * @param w1 The w coordinate of the first vector
     * @param x2 The x coordinate of the second vector
     * @param y2 The y coordinate of the second vector
     * @param z2 The y coordinate of the second vector
     * @param w2 The w coordinate of the second vector
     * @return The dot product of the vectors
     */
    public static double dot(double x1, double y1, double z1, double w1, double x2, double y2, double z2, double w2){
        return (x1*x2) + (y1*y2) + (z1*z2) + (w1*w2);
    }

    /**
     * Calculates voronoi noise on a 2d plane
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The voronoi value
     */
    public static double voronoi(double x, double y){
        //integer of the point coordinates
        double x_i = Math.floor(x);
        double y_i = Math.floor(y);

        //remainders of the point coordinates
        double x_r = x - x_i;
        double y_r = y - y_i;

        //calculate min dist
        double minDist = 10.0;
        for(int i = 0; i < KERNEL_3_3_X.length; i++){
            //the point of this cell
            double p_x = rand(x_i + KERNEL_3_3_X[i], y_i + KERNEL_3_3_Y[i], 0);
            double p_y = rand(x_i + KERNEL_3_3_X[i], y_i + KERNEL_3_3_Y[i], 1);

            //dist calc + comparison
            double dist = Math.sqrt((p_x + KERNEL_3_3_X[i] - x_r) * (p_x + KERNEL_3_3_X[i] - x_r) + (p_y + KERNEL_3_3_Y[i] - y_r) * (p_y + KERNEL_3_3_Y[i] - y_r));
            if(dist < minDist){
                minDist = dist;
            }
        }

        return minDist;
    }

    /**
     * Calculates voronoi noise within a cube
     * @param x The x coordinate
     * @param y The y coordinate
     * @param z The z coordinate
     * @return The voronoi value
     */
    public static double voronoi(double x, double y, double z){
        //integer of the point coordinates
        double x_i = Math.floor(x);
        double y_i = Math.floor(y);
        double z_i = Math.floor(z);

        //remainders of the point coordinates
        double x_r = x - x_i;
        double y_r = y - y_i;
        double z_r = z - z_i;

        //calculate min dist
        double minDist = 10.0;
        for(int i = 0; i < KERNEL_3_3_3_X.length; i++){
            //the point of this cell
            double p_x = rand(x_i + KERNEL_3_3_3_X[i], y_i + KERNEL_3_3_3_Y[i], z_i + KERNEL_3_3_3_Z[i], 0);
            double p_y = rand(x_i + KERNEL_3_3_3_X[i], y_i + KERNEL_3_3_3_Y[i], z_i + KERNEL_3_3_3_Z[i], 1);
            double p_z = rand(x_i + KERNEL_3_3_3_X[i], y_i + KERNEL_3_3_3_Y[i], z_i + KERNEL_3_3_3_Z[i], 2);

            //dist calc + comparison
            double dist = Math.sqrt(
                (p_x + KERNEL_3_3_3_X[i] - x_r) * (p_x + KERNEL_3_3_3_X[i] - x_r) +
                (p_y + KERNEL_3_3_3_Y[i] - y_r) * (p_y + KERNEL_3_3_3_Y[i] - y_r) +
                (p_z + KERNEL_3_3_3_Z[i] - z_r) * (p_z + KERNEL_3_3_3_Z[i] - z_r)
            );
            if(dist < minDist){
                minDist = dist;
            }
        }

        return minDist;
    }
    
}
