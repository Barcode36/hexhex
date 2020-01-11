package de.rjo.hex;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;

// https://stackoverflow.com/questions/41353685/how-to-draw-arrow-javafx-pane
// changed to use a filled polygon as the arrow
public class Arrow extends Group {

    private final Line line;

    public Arrow() {
	this(new Line(), new Polygon());
    }

    private static final double arrowLength = 10;
    private static final double arrowWidth = 4;

    private Arrow(Line line, Polygon arrow) {
	super(line, arrow);
	this.line = line;
	InvalidationListener updater = o -> {
	    double ex = getEndX();
	    double ey = getEndY();
	    double sx = getStartX();
	    double sy = getStartY();

	    arrow.getPoints().clear();
	    if (ex == sx && ey == sy) {
		// arrow parts of length 0
		arrow.getPoints().addAll(new Double[] { ex, ey, ex, ey, ex, ey });
	    } else {
		double factor = arrowLength / Math.hypot(sx - ex, sy - ey);
		double factorO = arrowWidth / Math.hypot(sx - ex, sy - ey);

		// part in direction of main line
		double dx = (sx - ex) * factor;
		double dy = (sy - ey) * factor;

		// part ortogonal to main line
		double ox = (sx - ex) * factorO;
		double oy = (sy - ey) * factorO;

//		System.out.println("1. (" + ex + "," + ey + ")");
//		System.out.println("2. (" + (ex + dx - oy) + "," + (ey + dy + ox) + ")");
//		System.out.println("3. (" + (ex + dx + oy) + "," + (ey + dy - ox) + ")");

		arrow.getPoints()
			.addAll(new Double[] { ex, ey, ex + dx - oy, ey + dy + ox, ex + dx + oy, ey + dy - ox });
	    }

	};

	// add updater to properties
	startXProperty().addListener(updater);
	startYProperty().addListener(updater);
	endXProperty().addListener(updater);
	endYProperty().addListener(updater);
	updater.invalidated(null);
    }

    // start/end properties

    public final void setStartX(double value) {
	line.setStartX(value);
    }

    public final double getStartX() {
	return line.getStartX();
    }

    public final DoubleProperty startXProperty() {
	return line.startXProperty();
    }

    public final void setStartY(double value) {
	line.setStartY(value);
    }

    public final double getStartY() {
	return line.getStartY();
    }

    public final DoubleProperty startYProperty() {
	return line.startYProperty();
    }

    public final void setEndX(double value) {
	line.setEndX(value);
    }

    public final double getEndX() {
	return line.getEndX();
    }

    public final DoubleProperty endXProperty() {
	return line.endXProperty();
    }

    public final void setEndY(double value) {
	line.setEndY(value);
    }

    public final double getEndY() {
	return line.getEndY();
    }

    public final DoubleProperty endYProperty() {
	return line.endYProperty();
    }

}