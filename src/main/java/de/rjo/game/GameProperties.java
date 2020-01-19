package de.rjo.game;

import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

public class GameProperties extends Properties {

    private static final long serialVersionUID = 1L;

    public static final String MAIN_PANE_WIDTH = "mainpane_width";
    public static final String MAIN_PANE_HEIGHT = "mainpane_height";

    public final static String NBR_ROWS = "nbrRows";
    public final static String NBR_COLS = "nbrCols";

    public static final String NBR_START_HEXES = "nbrStartHexes";
    public static final String MAX_UNITS_IN_HEX = "maxUnitsInHex";

    public static final String INITIAL_ENERGY = "initialEnergy";

    private static GameProperties instance = new GameProperties();

    private GameProperties() {
	try {
	    load(new FileReader(Game.class.getResource("hexhex.properties").getFile()));
	} catch (IOException e) {
	    throw new RuntimeException("could not find game properties", e);
	}
    }

    public static GameProperties instance() {
	return instance;
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
