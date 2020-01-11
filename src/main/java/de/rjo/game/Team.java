package de.rjo.game;

import javafx.scene.paint.Color;

public enum Team {

    NOT_SET(Color.WHITE), RED(Color.RED), BLUE(Color.LIGHTBLUE);

    private Color color;

    private Team(Color color) {
	this.color = color;
    }

    public Color getColor() {
	return color;
    }
}
