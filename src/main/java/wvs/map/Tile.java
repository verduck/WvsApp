package wvs.map;

import java.awt.Graphics2D;

import javafx.scene.canvas.GraphicsContext;
import wvs.WzManager;
import wvs.wz.WzObject;
import wvs.wz.property.WzCanvas;
import wvs.wz.property.WzProperty;
import wvs.wz.property.WzVariant;

public class Tile extends MapEntity {
	private WzCanvas canvas;
	
	public Tile(WzObject wzObj, String info, double centerX, double centerY) {
		int no;
		String u;
		WzProperty prop = (WzProperty) wzObj;
		no = ((WzVariant<Integer>) prop.get("no")).getValue();
		u = ((WzVariant<String>) prop.get("u")).getValue();
		canvas = (WzCanvas) WzManager.getInstance().getObject("Data/Map/Tile/" + info + ".img/" + u + "/" + no);
		x = ((WzVariant<Integer>) prop.get("x")).getValue() + centerX;
		y = ((WzVariant<Integer>) prop.get("y")).getValue() + centerY;
		try {
			z = ((WzVariant<Integer>) prop.get("z")).getValue();
		} catch (NullPointerException e) {
			z = 3;
		}
		zM = ((WzVariant<Integer>) prop.get("zM")).getValue();
	}

	@Override
	public void update(double delta) {
		
	}
	@Override
	public void render(GraphicsContext gc) {
		gc.drawImage(canvas.getFxImage(), x - canvas.getOrigin().getX(), y - canvas.getOrigin().getY());
	}

	
}
