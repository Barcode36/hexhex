package de.rjo.game;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

/**
 * Loads the game properties from the given file.
 *
 * @author rich
 */
public class GameProperties extends Properties {

    private static final long serialVersionUID = 1L;
    private static final String FILENAME = "hexhex.properties";

    public static final String MAIN_PANE_WIDTH = "mainpane_width";
    public static final String MAIN_PANE_HEIGHT = "mainpane_height";

    public final static String NBR_ROWS_PARAM = "nbrRows";
    public final static String NBR_COLS_PARAM = "nbrCols";

    // the values of NBR_ROWS and NBR_COLS are read at startup and stored here
    public final static int NBR_ROWS;
    public final static int NBR_COLS;

    public static final String NBR_START_HEXES = "nbrStartHexes";
    public static final String MAX_UNITS_IN_HEX = "maxUnitsInHex";

    public static final String INITIAL_ENERGY = "initialEnergy";

    private static GameProperties instance = new GameProperties();

    static {
	try {
	    instance.load(new FileReader(Game.class.getResource(FILENAME).getFile()));
	    NBR_ROWS = instance.getPropertyInt(GameProperties.NBR_ROWS_PARAM);
	    NBR_COLS = instance.getPropertyInt(GameProperties.NBR_COLS_PARAM);
	} catch (IOException e) {
	    throw new RuntimeException("could not find game properties", e);
	}
    }

    public static GameProperties instance() {
	return instance;
    }

    private GameProperties() {
    }

    @Override
    public String getProperty(String key) {
	String val = super.getProperty(key);
	if (val == null) {
	    throw new IllegalArgumentException("Property '" + key + "' not defined");
	}
	return val;
    }

    public int getPropertyInt(String key) {
	String val = getProperty(key);
	return Integer.parseInt(val);
    }
}
