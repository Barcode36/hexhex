package de.rjo.game;

public enum Team {

    NOT_SET("team-notset"), RED("team-red"), BLUE("team-blue");

    private String styleClass;

    private Team(String styleClass) {
	this.styleClass = styleClass;
    }

    public String getStyleClass() {
	return styleClass;
    }
}
