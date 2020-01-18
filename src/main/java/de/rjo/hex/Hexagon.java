package de.rjo.hex;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * https://www.redblobgames.com/grids/hexagons/
 *
 * Represents hexagons with "pointy-top".
 *
 * horizontal distance between adjacent hexagon centers is "width".
 *
 * vertical distance between adjacent hexagon centers is "height" * 3/4.
 */
public class Hexagon extends Polygon {
    public static int size = 50;
    public static /* final */ int X_OFFSET = 100; // defaults -- get set in Main
    public static /* final */ int Y_OFFSET = 100;

    private static final double verticalDistanceFactor = 0.75;

    final double width = Math.sqrt(3) * size; // sqrt(3) == sin(60°)
    final double height = 2 * size;

    private Color fillColor;
    private final GridCoordinate gridCoordinates;
    private final double centreX;
    private final double centreY;

    public Hexagon(int row, int col, Color fillColor) {
	this.fillColor = fillColor;
	this.gridCoordinates = new GridCoordinate(row, col);
	Double[] points = new Double[12];
	centreX = centreX(gridCoordinates);
	centreY = centreY(gridCoordinates);
	for (int i = 0; i < 6; i++) {
	    var point = pointy_hex_corner(centreX, centreY, size, i);
	    points[i * 2] = point.x;
	    points[i * 2 + 1] = point.y;
	}
	getPoints().addAll(points);
	setFill(fillColor);
	setStroke(Color.BLACK);

    }

    /**
     * @param maxRows maximum nbr of Rows
     * @param maxCols maximum nbr of Columns
     * @return the coordinates of the neighbours
     */
    public GridCoordinate[] getNeighbours(final int maxRows, final int maxCols) {
	List<GridCoordinate> list = new ArrayList<>();

	// first array are the offsets ((x,y) pairs) for even rows, 2nd array for odd
	// rows
	int[][] rowOffsets = new int[][] { //
		{ -1, -1, -1, 0, 0, -1, 0, 1, 1, -1, 1, 0 }, //
		{ -1, 0, -1, 1, 0, -1, 0, 1, 1, 0, 1, 1 }//
	};

	int offset = isOffsetRow(gridCoordinates.getRow());
	for (int i = 0; i < rowOffsets[offset].length; i = i + 2) {
	    var offsetX = rowOffsets[offset][i];
	    var offsetY = rowOffsets[offset][i + 1];
	    if (gridCoordinates.getRow() + offsetX >= 0 && gridCoordinates.getRow() + offsetX < maxRows
		    && gridCoordinates.getColumn() + offsetY >= 0 && gridCoordinates.getColumn() + offsetY < maxCols) {
		list.add(new GridCoordinate(gridCoordinates.getRow() + offsetX, gridCoordinates.getColumn() + offsetY));
	    }
	}
	System.out.println("me: " + gridCoordinates + ", neighbours: " + list);

	return list.toArray(new GridCoordinate[0]);
    }

    /**
     * @return 0 if not-offset, 1 if offset-row
     */
    private int isOffsetRow(int row) {
	return row % 2;
    }

    //
    // X: for even rows: centre of hex = offset + width * col
    // for odd rows: centre of hex is offset by width/2
    //
    private double centreX(final GridCoordinate coord) {
	double rowOffset = isOffsetRow(coord.getRow()) * (width / 2);
	return X_OFFSET + (width * coord.getColumn()) + rowOffset;
    }

    //
    // Y: centre of hex = offset + height * row * 0.75
    //
    private double centreY(final GridCoordinate coord) {
	return Y_OFFSET + (height * coord.getRow() * verticalDistanceFactor);
    }

    // returns the point of the hexagon corresponding to 'i', starting at\\
    // NE corner (== 0) and finishing at 'top' (== 5)
    private static Point2D.Double pointy_hex_corner(double centreX, double centreY, double size, int i) {
	var angle_deg = 60 * i - 30;
	var angle_rad = Math.PI / 180 * angle_deg;
	return new Point2D.Double(centreX + size * Math.cos(angle_rad), centreY + size * Math.sin(angle_rad));
    }

    @Override
    public String toString() {
	return "hex: " + gridCoordinates + ", " + super.toString();
    }

    // reset colour to what it was before
    public void resetColour() {
	setFill(fillColor);
    }

    // set colour to required color
    public void setColour(Color color) {
	fillColor = color;
	setFill(fillColor);
    }

    public Point2D.Double getPointAtNorthWest() {
	return new Point2D.Double(this.getPoints().get(8), this.getPoints().get(9));
    }

    public GridCoordinate getGridCoordinates() {
	return gridCoordinates;
    }

    public double getCentreX() {
	return centreX;
    }

    public double getCentreY() {
	return centreY;
    }
}
