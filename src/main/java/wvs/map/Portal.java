package wvs.map;

import java.awt.geom.Rectangle2D;
import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import wvs.WzManager;
import wvs.wz.WzObject;
import wvs.wz.property.WzCanvas;
import wvs.wz.property.WzProperty;
import wvs.wz.property.WzVariant;

public class Portal {
	private ArrayList<WzCanvas> images = new ArrayList<WzCanvas>();
	private int frame = 0;
	private int delay = 120;
	private long startTime;
	
	private int type;
	private int x;
	private int y;
	private boolean useble;
	
	private Rectangle2D rect;
	
	public Portal(WzObject wzObj, double centerX, double centerY) {
		WzProperty prop = (WzProperty) wzObj;
		this.type = ((WzVariant<Integer>) prop.getObject("pt")).getValue();
		this.x = (int) (((WzVariant<Integer>) prop.getObject("x")).getValue() + centerX);
		this.y = (int) (((WzVariant<Integer>) prop.getObject("y")).getValue() + centerY);
		startTime = System.nanoTime();
		
		if (type == 2) {
			prop = (WzProperty) WzManager.getInstance().getObject("Data/Map/MapHelper.img/portal/game/pv");
			for (WzObject o : prop.getProps()) {
				images.add((WzCanvas) o);
			}
			useble = true;
		} else {
			useble = false;
		}

		rect = new Rectangle2D.Double(x - 30, y - 60, 60, 60);
	}
	
	public Portal(int type, int x, int y) {
		this.type = type;
		this.x = x;
		this.y = y;
		startTime = System.nanoTime();
		
		if (type == 2) {
			WzProperty prop = (WzProperty) WzManager.getInstance().getObject("Data/Map/MapHelper.img/portal/game/pv");
			for (WzObject o : prop.getProps()) {
				images.add((WzCanvas) o);
			}
			useble = true;
		} else {
			useble = false;
		}

		rect = new Rectangle2D.Double(x - 30, y - 60, 60, 60);
	}
	
	public boolean isUseble() {
		return useble;
	}
	
	public int getType() {
		return type;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Rectangle2D getRect() {
		return rect;
	}
	
	public void update(double delta) {
		if (type != 2) {
			return;
		}
		if (delta * delay >= delay) {
			frame++;
			startTime = System.nanoTime();
		}
		if (frame == images.size()) {
			frame = 0;
		}
	}
	
	public void render(GraphicsContext gc) {
		if (type != 2) {
			return;
		}
		gc.drawImage(images.get(frame).getFxImage(), x - images.get(frame).getOrigin().getX(), y - images.get(frame).getOrigin().getY());
		gc.rect(x - 30, y - 60, 60, 60);
	}
}
