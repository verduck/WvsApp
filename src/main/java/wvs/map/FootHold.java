package wvs.map;

import java.awt.geom.Line2D;

import javafx.scene.canvas.GraphicsContext;
import wvs.map.FootHold;
import wvs.wz.WzObject;
import wvs.wz.property.WzProperty;
import wvs.wz.property.WzVariant;

public class FootHold {
	private int id;
	private FootHold next;
	private FootHold prev;
	private Line2D line;
	private double x1;
	private double y1;
	private double x2;
	private double y2;
	
	@SuppressWarnings("unchecked")
	public FootHold(WzObject wzObj, double centerX, double centerY) {
		WzProperty fhProp = (WzProperty) wzObj;
		this.id = Integer.parseInt(fhProp.getName());
		x1 = ((WzVariant<Integer>) fhProp.getObject("x1")).getValue() + centerX;
		x2 = ((WzVariant<Integer>) fhProp.getObject("x2")).getValue() + centerX;
		y1 = ((WzVariant<Integer>) fhProp.getObject("y1")).getValue() + centerY;
		y2 = ((WzVariant<Integer>) fhProp.getObject("y2")).getValue() + centerY;
		this.line = new Line2D.Double(x1, y1, x2, y2);
	}
	
	public Line2D getLine() {
		return line;
	}
	
	public void render(GraphicsContext gc) {
		gc.strokeLine(x1, y1, x2, y2);
	}

}
