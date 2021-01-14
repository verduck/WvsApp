package wvs.map;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import wvs.WzManager;
import wvs.wz.WzObject;
import wvs.wz.property.WzCanvas;
import wvs.wz.property.WzProperty;
import wvs.wz.property.WzVariant;

public class MapObject extends MapEntity {
	private int f;
	
	private boolean isAnimation;
	private ArrayList<WzCanvas> canvases = new ArrayList<WzCanvas>();
	private int frame = 0;
	
	private long startTime;
	private ArrayList<Integer> delaies = new ArrayList<Integer>();
	
	public MapObject(int id, WzObject wzObj, double centerX, double centerY) {
		WzProperty prop = (WzProperty) wzObj;
		String l0, l1, l2, oS;
		this.id = id;
		this.f = ((WzVariant<Integer>) prop.getObject("f")).getValue();
		l0 = ((WzVariant<String>) prop.getObject("l0")).getValue();
		l1 = ((WzVariant<String>) prop.getObject("l1")).getValue();
		l2 = ((WzVariant<String>) prop.getObject("l2")).getValue();
		oS = ((WzVariant<String>) prop.getObject("oS")).getValue();
		this.x = ((WzVariant<Integer>) prop.getObject("x")).getValue() + centerX;
		this.y = ((WzVariant<Integer>) prop.getObject("y")).getValue() + centerY;
		this.z = ((WzVariant<Integer>) prop.getObject("z")).getValue();
		this.zM = ((WzVariant<Integer>) prop.getObject("zM")).getValue();
		
		WzObject obj = WzManager.getInstance().getObject("Data/Map/Obj/" + oS + ".img/" + l0 + "/" + l1 + "/" + l2);
		if (obj instanceof WzProperty) {
			for (WzObject c : ((WzProperty) obj).getProps()) {
				if (c instanceof WzCanvas) {
					canvases.add((WzCanvas) c);
				}
			}
			if (canvases.size() > 1) {
				isAnimation = true;
				startTime = System.nanoTime();
				for (WzCanvas c : canvases) {
					int delay;
					try {
						delay = ((WzVariant<Integer>) c.getObject("delay")).getValue();
					} catch (NullPointerException e) {
						delay = 0;
					}
					delaies.add(delay);
				}
			} else {
				isAnimation = false;
			}
		}
	}

	@Override
	public void update(double delta) {
		if (!isAnimation) {
			return;
		}
		if (delta * delaies.get(frame) >= delaies.get(frame)) {
			frame++;
			startTime = System.nanoTime();
		}
		if (frame == canvases.size()) {
			frame = 0;
		}
	}

	@Override
	public void render(GraphicsContext gc) {
		WzCanvas canvas = canvases.get(frame);
		if (f == 1) {
			gc.drawImage(canvas.getFxImage(), x - canvas.getOrigin().getX() + canvas.getImage().getWidth(), y - canvas.getOrigin().getY(), -canvas.getImage().getWidth(), canvas.getImage().getHeight());
		} else {
			gc.drawImage(canvas.getFxImage(), x - canvas.getOrigin().getX(), y - canvas.getOrigin().getY());
		}
	}
}
