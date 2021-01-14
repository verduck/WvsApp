package wvs.map;

import wvs.wz.WzObject;
import wvs.wz.property.WzCanvas;
import wvs.wz.property.WzProperty;
import wvs.wz.property.WzVariant;

public class MiniMap {
	private WzCanvas canvas;
	private double width;
	private double height;
	private double centerX;
	private double centerY;
	private int mag;
	
	public MiniMap() {
		this.canvas = null;
		this.width = 800;
		this.height = 600;
		this.centerX = 0;
		this.centerY = 0;
		this.mag = 0;
	}
	
	public void loadMiniMap(WzObject wzObj) {
		WzProperty prop = (WzProperty) wzObj;
		this.canvas = ((WzCanvas) prop.getObject("canvas"));
		this.centerX = ((WzVariant<Integer>) prop.getObject("centerX")).getValue();
		this.centerY = ((WzVariant<Integer>) prop.getObject("centerY")).getValue();
		this.height = ((WzVariant<Integer>) prop.getObject("height")).getValue();
		this.width = ((WzVariant<Integer>) prop.getObject("width")).getValue();
		this.mag = ((WzVariant<Integer>) prop.getObject("mag")).getValue();
	}
	
	public WzCanvas getCanvas() {
		return canvas;
	}
	
	public double getWidth() {
		return width;
	}
	
	public double getHeight() {
		return height;
	}
	
	public double getCenterX() {
		return centerX;
	}
	
	public double getCenterY() {
		return centerY;
	}

}
