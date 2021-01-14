package wvs.character;

import java.util.ArrayList;

import javafx.scene.canvas.GraphicsContext;
import wvs.WzManager;
import wvs.wz.WzObject;
import wvs.wz.property.WzProperty;

public class WvsAvatar {
	private ArrayList<MFrame> frames = new ArrayList<MFrame>();
	private int frame;
	private long startTime;
	
	private int faceId;
	
	private boolean horizontally;
	
	public WvsAvatar(int faceId) {
		WzProperty prop = (WzProperty) WzManager.getInstance().getObject(String.format("Data/Mob/%07d.img/fly", faceId));
		for (WzObject o : prop.getProps()) {
			frames.add(new MFrame(o));
		}
		horizontally = false;
	}
	
	public void setHorizontally(boolean value) {
		this.horizontally = value;
	}
	
	public void update(double delta) {
		if (delta * frames.get(frame).getDelay() >= frames.get(frame).getDelay()) {
			frame++;
			startTime = System.nanoTime();
		}
		if (frame == frames.size()) {
			frame = 0;
		}
	}
	
	public void render(GraphicsContext gc, double x, double y) {
		frames.get(frame).render(gc, x, y, horizontally);
	}
}
