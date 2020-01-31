package de.rjo.game;

import de.rjo.hex.Hexagon;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;

// stores the game state for a particular hexagon
public class GameState {

    private Team team = Team.NOT_SET;
    private IntegerProperty nbrUnits = new SimpleIntegerProperty(0);
    private Label label; // this display the info in the hexagon itself

    public GameState() {
	label = new Label("");
	label.getStyleClass().add("gamestate");
	// need a special binding, since nbrUnits should not be displayed if zero
	StringBinding sb = new StringBinding() {
	    {
		super.bind(nbrUnits);
	    }

	    @Override
	    protected String computeValue() {
		return (nbrUnits.get() > 0) ? "" + nbrUnits.get() : "";
	    }
	};
	label.textProperty().bind(sb);
    }

    public boolean isEmpty() {
	return nbrUnits.get() == 0;
    }

    public void setUnits(Team team, int nbrUnits) {
	this.team = team;
	this.nbrUnits.set(nbrUnits);
    }

    public Team getTeam() {
	return team;
    }

    public int getNbrUnits() {
	return nbrUnits.get();
    }

    public Label getLabel() {
	return label;
    }

    public void increment(int nbr) {
	if (nbrUnits.get() != 0) {
	    nbrUnits.set(nbrUnits.get() + nbr);
	}
    }

    public void decrement(final Hexagon hex, int nbr) {
	if (nbrUnits.get() >= nbr) {
	    nbrUnits.set(nbrUnits.get() - nbr);
	    if (nbrUnits.get() == 0) {
		team = Team.NOT_SET;
		hex.setStyleClass(Team.NOT_SET.getStyleClass());
	    }
	}
    }

    /**
     * can the given number of units be removed from this hex?
     */
    public boolean canMoveFrom(int nbr) {
	return nbrUnits.get() >= nbr;
    }

    /**
     * can a move of the given player be made to this hex?
     */
    public boolean canMoveTo(Team player) {
	return team == Team.NOT_SET || team == player;
    }
}
