package de.rjo.game;

import java.util.ArrayList;
import java.util.List;

public enum Team {

    BLUE("blue", "team-blue"), RED("red", "team-red"), NOT_SET("n/a", "team-notset");

    private String styleClass;
    private String name;

    private Team(String name, String styleClass) {
	this.name = name;
	this.styleClass = styleClass;
    }

    public String getStyleClass() {
	return styleClass;
    }

    public String getName() {
	return name;
    }

    public static List<String> getAllStyles() {
	List<String> styles = new ArrayList<>();
	for (Team t : values()) {
	    styles.add(t.getStyleClass());
	}
	return styles;
    }

    /**
     * @return the opponent of this team
     */
    public Team getOpponent() {
	switch (this) {
	case BLUE:
	    return RED;
	case RED:
	    return BLUE;
	default:
	    return NOT_SET;
	}
    }
}
