package wvs.character;

import javafx.scene.canvas.GraphicsContext;
import wvs.wz.WzObject;
import wvs.wz.property.WzCanvas;
import wvs.wz.property.WzVariant;

public class MFrame {
	private WzCanvas canvas;
	private int delay;
	
	@SuppressWarnings("unchecked")
	public MFrame(WzObject wzObj) {
		canvas = (WzCanvas) wzObj;
		try {
			delay = ((WzVariant<Integer>) canvas.getObject("delay")).getValue();
		} catch(NullPointerException e) {
			delay = 0;
		}
	}
	
	public int getDelay() {
		return delay;
	}
	
	public void render(GraphicsContext gc, double x, double y, boolean horizontally) {
		if (horizontally) {
			gc.drawImage(canvas.getFxImage(), x - canvas.getOrigin().getX() + canvas.getImage().getWidth(), y - canvas.getOrigin().getY() + 20, -canvas.getImage().getWidth(), canvas.getImage().getHeight());
		} else {
			gc.drawImage(canvas.getFxImage(), x - canvas.getOrigin().getX(), y - canvas.getOrigin().getY() + 20);
		}
	}
}
