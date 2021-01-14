package wvs.map;

import javafx.scene.Camera;
import javafx.scene.canvas.GraphicsContext;
import wvs.WzManager;
import wvs.wz.WzObject;
import wvs.wz.property.WzCanvas;
import wvs.wz.property.WzProperty;
import wvs.wz.property.WzVariant;

enum BackgroudType {
	Regular,
	HorizontalTiling,
	VerticalTiling,
	HVTiling,
	HorizontalMoving,
	VerticalMoving,
	HorizontalMMovingHVTiling,
	VerticalMovingHVTiling
}

public class Background {
	private WzCanvas canvas;
	
	private MiniMap miniMap;
	
	private int a;
	private String bS;
	private int cx;
	private int cy;
	private int no;
	private int rx;
	private int ry;
	private int type;
	private double x;
	private double y;
	private double dx;
	private double dy;

	public Background(WzObject wzObj, MiniMap miniMap) {
		WzProperty prop = (WzProperty) wzObj;
		this.miniMap = miniMap;
		a = ((WzVariant<Integer>) prop.getObject("a")).getValue();
		bS = ((WzVariant<String>) prop.getObject("bS")).getValue();
		cx = ((WzVariant<Integer>) prop.getObject("cx")).getValue();
		cy = ((WzVariant<Integer>) prop.getObject("cy")).getValue();
		no = ((WzVariant<Integer>) prop.getObject("no")).getValue();
		rx = ((WzVariant<Integer>) prop.getObject("rx")).getValue();
		ry = ((WzVariant<Integer>) prop.getObject("ry")).getValue();
		type = ((WzVariant<Integer>) prop.getObject("type")).getValue();
		x = ((WzVariant<Integer>) prop.getObject("x")).getValue() + miniMap.getCenterX();
		y = ((WzVariant<Integer>) prop.getObject("y")).getValue() + miniMap.getCenterY();
		canvas = ((WzCanvas) WzManager.getInstance().getObject("Data/Map/Back/" + bS + ".img/back/" + no));
		dx = x;
		dy = y;
		cx = cx == 0 ? canvas.getWidth() : cx;
		cy = cy == 0 ? canvas.getHeight() : cy;
	}
	
	public void update(double delta, Camera camera) {
		/*double mapShiftX, mapShiftY;
		double w = 800 / 2;
		double h = 600 / 2;
		mapShiftX = camera.getTranslateX() + (800 / 2) + miniMap.getCenterX();
		mapShiftY = camera.getTranslateY() + (600 / 2) + miniMap.getCenterY();
		dx = rx * (w - mapShiftX) / 100 + w + x;
		dy = ry * (h - mapShiftY) / 100 + h + y;*/
		dx = (rx + 100) * (camera.getTranslateX() - 800 / 2) / 100 + x;
		dy = (ry + 100) * (camera.getTranslateY() - 600 / 2) / 100 + y;
	}
	
	public void drawHorizontal(GraphicsContext gc, double x, double y, double cx) {
		gc.drawImage(canvas.getFxImage(), dx - canvas.getOrigin().getX(), dy - canvas.getOrigin().getY());
		double copyX = x - cx;
		while (copyX + canvas.getWidth() > 0) {
			gc.drawImage(canvas.getFxImage(), copyX - canvas.getOrigin().getX(), y - canvas.getOrigin().getY());
			copyX -= cx;
		}
		copyX = x + cx;
		while (copyX < miniMap.getWidth()) {
			gc.drawImage(canvas.getFxImage(), copyX - canvas.getOrigin().getX(), y - canvas.getOrigin().getY());
			copyX += cx;
		}
	}
	
	public void drawVertical(GraphicsContext gc, double x, double y, double cy) {
		gc.drawImage(canvas.getFxImage(), x - canvas.getOrigin().getX(), y - canvas.getOrigin().getY());
		double copyY = y - cy;
		while (copyY + canvas.getHeight() > 0) {
			gc.drawImage(canvas.getFxImage(), x - canvas.getOrigin().getX(), copyY - canvas.getOrigin().getY());
			copyY -= cy;
		}
		copyY = y + cy;
		while (copyY < miniMap.getHeight()) {
			gc.drawImage(canvas.getFxImage(), x - canvas.getOrigin().getX(), copyY - canvas.getOrigin().getY());
			copyY += cy;
		}
	}
	
	public void drawHV(GraphicsContext gc, double x, double y, double cx, double cy) {
		drawVertical(gc, x, y, cy);
		double copyX = x - cx;
		while (copyX + canvas.getWidth() >= 0) {
			drawVertical(gc, copyX, y, cy);
			copyX -= cx;
		}
		copyX = x + cx;
		while (copyX <= miniMap.getWidth()) {
			drawVertical(gc, copyX, y, cy);
			copyX += cx;
		}
	}
	
	public void render(GraphicsContext gc) {
		switch (type) {
		case 0:
			gc.drawImage(canvas.getFxImage(), dx - canvas.getOrigin().getX(), dy - canvas.getOrigin().getY());
			break;
		case 1:
			drawHorizontal(gc, dx, dy ,cx);
			break;
		case 2:
			drawVertical(gc, dx, dy, cy);
			break;
		case 3:
			drawHV(gc, dx, dy, cx, cy);
			break;
		case 4:
			drawHorizontal(gc, dx, dy, cx);
			break;
		case 5:
			drawVertical(gc, dx, dy, cy);
			break;
		}
	}
}
