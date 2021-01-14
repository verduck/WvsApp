package wvs.map;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;

import javafx.event.EventHandler;
import javafx.scene.ParallelCamera;
import javafx.scene.SubScene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import wvs.WvsApp;
import wvs.WzManager;
import wvs.net.PacketWriter;
import wvs.wz.WzObject;
import wvs.wz.property.WzProperty;
import wvs.wz.property.WzVariant;

public class WvsMap extends SubScene {
	private WvsApp app;
	private StackPane root;
	private Canvas canvas;
	private ParallelCamera camera;
	private double cameraX = 0;
	private double cameraY = 0;
	private Player player;
	private boolean isLogin;
	
	private MiniMap miniMap;
	
	private int velocityIterations = 6;
	private int positionIterations = 2;
	
	private WzProperty mapProperty;
	
	private LinkedList<Background> backs = new LinkedList<>();
	private LinkedList<FootHold> footholds = new LinkedList<>();
	private LinkedList<LinkedList<MapObject>> objs = new LinkedList<>();
	private LinkedList<LinkedList<Tile>> tiles = new LinkedList<>();
	private LinkedList<Portal> portals = new LinkedList<>();
	private LinkedList<Player> players = new LinkedList<>();
	
	public WvsMap(WvsApp app, Player player) {
		super(new StackPane(), 0, 0);
		this.app = app;
		root = (StackPane) getRoot();
		camera = new ParallelCamera();
		setCamera(camera);
		canvas = new Canvas();
		root.getChildren().add(canvas);
		miniMap = new MiniMap();
		this.player = player;
		root.setStyle("-fx-background-color: black");
		
		
		setOnKeyPressed(new EventHandler<>() {
			@Override
			public void handle(KeyEvent e) {
				PacketWriter packet = new PacketWriter();
				packet.writeShort(10);
				packet.writeInt(e.getCode().getCode());
				app.sendPacket(packet.getPacket());
			}
		
		});
		
		setOnKeyReleased(new EventHandler<>() {

			@Override
			public void handle(KeyEvent e) {
				PacketWriter packet = new PacketWriter();
				packet.writeShort(11);
				packet.writeInt(e.getCode().getCode());
				app.sendPacket(packet.getPacket());
			}
			
		});
		
		isLogin = false;
	}
	
	private void loadMapByWzPath(String wzPath) {
		mapProperty = (WzProperty) WzManager.getInstance().getObject(wzPath);
		try {
			miniMap.loadMiniMap(mapProperty.getObject("miniMap"));
		} catch (NullPointerException e) {
			System.out.println("미니맵이 존재하지 않은 맵입니다.");
		}
		setWidth(miniMap.getWidth());
		setHeight(miniMap.getHeight());
		root.setPrefWidth(miniMap.getWidth());
		root.setPrefHeight(miniMap.getHeight());
		canvas.setWidth(miniMap.getWidth());
		canvas.setHeight(miniMap.getHeight());
		loadBacks();
		loadFootholds();
		loadTileAndObject();
		GraphicsContext gc = canvas.getGraphicsContext2D();
	}
	
	private void loadMap(int mapId) {
		isLogin = false;
		String wzPath = "Data/Map/Map/" + String.format("Map%d/%09d.img", mapId / 100000000, mapId);
		loadMapByWzPath(wzPath);
	}
	
	private void unloadMap() {
		setWidth(0);
		setHeight(0);
		root.setPrefWidth(0);
		root.setPrefHeight(0);
		canvas.setWidth(0);
		canvas.setHeight(0);
		backs.clear();
		footholds.clear();
		tiles.clear();
		objs.clear();
	}
	
	public void clear() {
		unloadMap();
	}
	
	public void changeMap(int mapId) {
		app.pause();
		unloadMap();
		loadMap(mapId);
		app.resume();
	}
	
	public void changeLoginStep(int step) {
		camera.setTranslateY(1850 - (600 * step));
	}
	
	public void changeLoginMap() {
		isLogin = true;
		unloadMap();
		loadMapByWzPath("Data/UI/MapLogin.img");
		camera.setTranslateX(-45);
	}
	
	private void loadBacks() {
		WzObject backProp = mapProperty.getObject("back");
		
		if (backProp instanceof WzProperty) {
			for (WzObject obj : ((WzProperty) backProp).getProps()) {
				backs.add(new Background(obj, miniMap));
			}
		}
	}
	
	private void loadFootholds() {
		WzObject fhProp = mapProperty.getObject("foothold");
		if (fhProp instanceof WzProperty) {
			for (WzObject f : ((WzProperty) fhProp).getProps()) {
				for (WzObject m : ((WzProperty) f).getProps()) {
					for (WzObject l : ((WzProperty) m).getProps()) {
						FootHold fh = new FootHold(l, miniMap.getCenterX(), miniMap.getCenterY());
						footholds.add(fh);
					}
				}
			}
		}
	}
	
	private void loadTileAndObject() {
		ArrayList<WzProperty> otProps = new ArrayList<WzProperty>();
		for (WzObject obj : mapProperty.getProps()) {
			try {
				Integer.parseInt(obj.getName());
				otProps.add((WzProperty) obj);
			} catch (NumberFormatException e) {}
		}
		
		for (WzObject obj : otProps) {
			String info = "";
			try {
				info = ((WzVariant<String>) obj.getObject("info/tS")).getValue();
			} catch (Exception e) {}
			objs.add(loadObject((WzProperty) obj.getObject("obj"), info));
			tiles.add(loadTiles((WzProperty) obj.getObject("tile"), info));
		}		
		for (int i = 0; i < objs.size(); i++) {
			Collections.sort(objs.get(i));
			Collections.sort(tiles.get(i));
		}
	}
	
	private LinkedList<MapObject> loadObject(WzProperty objectProp, String info) {
		HashMap<Integer, MapObject> temp = new HashMap<>();
		LinkedList<MapObject> objs = new LinkedList<>();
		for (WzObject obj : objectProp.getProps()) {
			objs.add(new MapObject(Integer.parseInt(obj.getName()),obj, miniMap.getCenterX(), miniMap.getCenterY()));
		}
		return objs;
	}
	
	private LinkedList<Tile> loadTiles(WzProperty tileProp, String info) {
		LinkedList<Tile> tiles = new LinkedList<>();
		for (WzObject obj : tileProp.getProps()) {
			tiles.add(new Tile(obj, info, miniMap.getCenterX(), miniMap.getCenterY()));
		}
		return tiles;
	}
	
	public void update(double delta) {
		for (Background b : backs) {
			b.update(delta, camera);
		}
		for (int i = 0; i < objs.size(); i++) {
			for (MapObject o : objs.get(i)) {
				o.update(delta);
			}
			for (Tile t : tiles.get(i)) {
				t.update(delta);
			}
		}
		for (Portal p : portals) {
			p.update(delta);
		}
		playerUpdate(delta);
		for (Player p : players) {
			p.update(delta);
		}
		cameraUpdate(delta);
	}
	
	private void playerUpdate(double delta) {
		player.update(delta);
	}
	
	public void setPlayers(LinkedList<Player> players) {
		this.players = players;
	}
	
	public void setPortals(LinkedList<Portal> portals) {
		this.portals = portals;
	}
	
	public double ex(double y, double v, double t) {
		double g = 9.81;
		return y + v * t + (1 / 2) * g * Math.pow(t, 2);
	}
	
	public void setPlayerX(double x) {
		player.setX(x);
		cameraX = 0;
	}
	
	public void setPlayerY(double y) {
		player.setX(y);
		cameraY = 0;
	}
	
	private void cameraUpdate(double delta) {
		if (isLogin) {
			cameraX = lerp(cameraX, player.getX(), 0.08f);
			cameraY = lerp(cameraY, player.getY(), 0.08f);
		} else {
			if (cameraX >= 0 && cameraX <= miniMap.getWidth() - 800) {
				double temp = cameraX;
				cameraX = lerp(cameraX, player.getX() - 800 / 2, 0.05f);
				if (cameraX < 0 || cameraX > miniMap.getWidth() - 800) {
					cameraX = temp;
				}
			}
			if (cameraY >= 0 && cameraY <= miniMap.getHeight() - 600) {
				double temp = cameraY;
				cameraY = lerp(cameraY, player.getY() - 600 / 2, 0.05f);
				if (cameraY < 0 || cameraY > miniMap.getHeight() - 600) {
					cameraY = temp;
				}
			}
		}
		camera.setTranslateX(cameraX);
		camera.setTranslateY(cameraY);
	}
	
	private double lerp(double v0, double v1, double t) {
		return v0 + t * (v1 - v0);
	}
	
	public void render() {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		gc.strokeRect(0, 0, canvas.getWidth(), canvas.getHeight());
		
		for (Background b : backs) {
			b.render(gc);
		}

		for (int i = 0; i < objs.size(); i++) {
			for (MapObject o : objs.get(i)) {
				o.render(gc);
			}
			for (Tile t : tiles.get(i)) {
				t.render(gc);
			}
			if (i == 4) {
				for (Portal p : portals) {
					p.render(gc);
				}
				player.render(gc);
				for (Player p : players) {
					p.render(gc);
				}
			}
		}
	}
}
