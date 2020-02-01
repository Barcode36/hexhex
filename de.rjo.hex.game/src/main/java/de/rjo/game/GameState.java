package de.rjo.game;

import java.util.stream.Stream;

import de.rjo.hex.GridCoordinate;
import de.rjo.hex.Hexagon;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.control.Label;

// stores the game state for a particular hexagon
public class GameState {

    private Team team = Team.NOT_SET;
    private final IntegerProperty nbrUnits = new SimpleIntegerProperty(0);
    private final Label label; // this display the info in the hexagon itself
    private final GridCoordinate coordinates;

    public GameState(int row, int col) {
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
	this.coordinates = new GridCoordinate(row, col);
    }

    /**
     * return all neighbours as a stream of GridCoordinate objects.
     *
     * (Convenience method, delegates to the given 'board'.
     *
     * @param board   the hex board
     * @param maxRows maximum nbr of Rows
     * @param maxCols maximum nbr of Columns
     * @return the coordinates of the neighbours as a stream
     */
    public Stream<GridCoordinate> streamNeighbours(final Hexagon[][] board, final int maxRows, final int maxCols) {
	return board[coordinates.getRow()][coordinates.getColumn()].streamNeighbours(maxRows, maxCols);
    }

    public GridCoordinate getCoordinates() {
	return coordinates;
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
