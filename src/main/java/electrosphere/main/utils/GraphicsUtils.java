package electrosphere.main.utils;

/**
 * Graphics Utilities
 */
public class GraphicsUtils {

    //dimension of color space
    static final int MIN_COLOR = 0;
    static final int MAX_COLOR = 255;

    //heights
    static float MIN_HEIGHT = 0;
    static float MAX_HEIGHT = 0;

    /**
     * Converts a height to a corresponding color
     */
    public static int heightToColor(float height){
        int rVal = (int)((height - MIN_HEIGHT) / (MAX_HEIGHT - MIN_HEIGHT) * (MAX_COLOR - MIN_COLOR) + MIN_COLOR);
        if(rVal > MAX_COLOR || rVal < MIN_COLOR){
            System.out.println(rVal + " " + height);
        }
        return rVal;
    }

    /**
     * Sets the height range
     * @param min The minimum height
     * @param max The maximum height
     */
    public static void setHeightRange(float min, float max){
        MIN_HEIGHT = min;
        MAX_HEIGHT = max;
    }
    
}
