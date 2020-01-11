package de.rjo.game;

import de.rjo.hex.Hexagon;
import javafx.scene.control.Label;
import javafx.scene.text.Font;

public class GameState {

    private Team team = Team.NOT_SET;
    private int nbrPlayers = 0;
    private Label label; // this display the info

    public GameState() {
	label = new Label("");
	label.setFont(new Font(20));
//	label.setVisible(false);
    }

    public boolean isEmpty() {
	return nbrPlayers == 0;
    }

    public void setPlayers(Team team, int nbrPlayers) {
	this.team = team;
	this.nbrPlayers = nbrPlayers;
	updateLabel();
    }

    private void updateLabel() {
	this.label.setText((nbrPlayers > 0) ? "" + nbrPlayers : "");
    }

    public Team getTeam() {
	return team;
    }

    public int getNbrPlayers() {
	return nbrPlayers;
    }

    public Label getLabel() {
	return label;
    }

    public void increment() {
	if (nbrPlayers != 0) {
	    nbrPlayers++;
	    updateLabel();
	}
    }

    public void decrement(final Hexagon hex) {
	if (nbrPlayers > 0) {
	    nbrPlayers--;
	    updateLabel();
	    if (nbrPlayers == 0) {
		team = Team.NOT_SET;
		hex.setColour(Team.NOT_SET.getColor());
	    }
	}
    }
}
