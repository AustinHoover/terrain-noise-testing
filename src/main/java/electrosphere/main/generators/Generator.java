package electrosphere.main.generators;

import javax.swing.JPanel;

/**
 * A generator
 */
public interface Generator {
    
    /**
     * Gets the display for a specific generator
     * @return The display
     */
    public JPanel getDisplay();

    

}
