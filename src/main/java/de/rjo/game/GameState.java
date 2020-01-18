package de.rjo.game;

import de.rjo.hex.Hexagon;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

// stores the game state for a particular hexagon
public class GameState {

    private Team team = Team.NOT_SET;
    private int nbrUnits = 0;
    private Label label; // this display the info

    public GameState() {
	label = new Label("");
	label.setFont(new Font(20));
//	label.setVisible(false);
    }

    public boolean isEmpty() {
	return nbrUnits == 0;
    }

    public void setUnits(Team team, int nbrUnits) {
	this.team = team;
	this.nbrUnits = nbrUnits;
	updateLabel();
    }

    private void updateLabel() {
	this.label.setText((nbrUnits > 0) ? "" + nbrUnits : "");
    }

    public Team getTeam() {
	return team;
    }

    public int getNbrUnits() {
	return nbrUnits;
    }

    public Label getLabel() {
	return label;
    }

    public void increment(int nbr) {
	if (nbrUnits != 0) {
	    nbrUnits += nbr;
	    updateLabel();
	}
    }

    public void decrement(final Hexagon hex, int nbr) {
	if (nbrUnits >= nbr) {
	    nbrUnits -= nbr;
	    updateLabel();
	    if (nbrUnits == 0) {
		team = Team.NOT_SET;
		hex.setColour(Team.NOT_SET.getColor());
	    }
	}
    }

    /**
     * can the given number of units be removed from this hex?
     */
    public boolean canMoveFrom(int nbr) {
	return nbrUnits >= nbr;
    }

    /**
     * can a move of the given player be made to this hex?
     */
    public boolean canMoveTo(Team player) {
	return team == Team.NOT_SET || team == player;
    }
}
