package de.rjo.game;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.scene.control.ProgressBar;

/**
 * the progress bar starts "full" and goes down to 0.
 */
public class MoveProgressBar extends ProgressBar {

    public MoveProgressBar(ObjectProperty<Team> playerToMove, IntegerProperty value, int maxValue) {
	progressProperty().bind(Bindings.divide(value, maxValue * 1.0));

	// change the colour of the bar when the player-to-move changes
	playerToMove.addListener(l -> {
	    getStyleClass().removeAll(Team.getAllStyles());
	    getStyleClass().add(playerToMove.get().getStyleClass());
	    System.out.println(getStyleClass());
	});

    }
}
