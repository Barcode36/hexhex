package de.rjo.game;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Energy {

    private IntegerProperty energy;

    public Energy(int energy) {
	this.energy = new SimpleIntegerProperty(energy);
    }

    public IntegerProperty getEnergy() {
	return energy;
    }

    public void increase(int amt) {
	energy.set(energy.get() + amt);
    }

    public void reduce(int amt) {
	if ((energy.get() - amt) < 0) {
	    throw new IllegalStateException("energy cannot go below 0");
	}
	energy.set(energy.get() - amt);
    }
}
