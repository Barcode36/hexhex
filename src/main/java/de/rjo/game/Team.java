package de.rjo.game;

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
}
