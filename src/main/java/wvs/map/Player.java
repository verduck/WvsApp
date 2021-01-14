package wvs.map;

import javafx.scene.canvas.GraphicsContext;
import wvs.character.WvsAvatar;

public class Player {
	private int id;
	private String name;
	private int map;
	private int face;

	private double x;
	private double y;

	private WvsAvatar avatar;

	public Player() {
		this(9400000, 0, 0);
	}

	public Player(int id, String name, int map, int face) {
		this.id = id;
		this.name = name;
		this.map = map;
		this.face = face;

		avatar = new WvsAvatar(face);
	}

	public Player(int face, double x, double y) {
		this.id = 0;
		this.name = null;
		this.map = 0;
		this.face = face;
		this.x = x;
		this.y = y;
		this.avatar = new WvsAvatar(face);
	}

	public void update(double delta) {
		avatar.update(delta);
	}

	public boolean isCollision(FootHold foothold) {
		return foothold.getLine().intersects(x, y, 10, 10);
	}

	public void render(GraphicsContext gc) {
		if (avatar == null) {
			gc.fillRect(x, y, 10, 10);
		} else {
			avatar.render(gc, x, y);
		}
	}

	public void setHorizontally(boolean value) {
		avatar.setHorizontally(value);
	}

	public void set(int id, String name, int map, int face) {
		this.id = id;
		this.name = name;
		this.map = map;
		setFace(face);
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public int getMap() {
		return map;
	}

	public int getFace() {
		return face;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMap(int map) {
		this.map = map;
	}

	public void setFace(int face) {
		if (this.face != face) {
			this.face = face;
			avatar = new WvsAvatar(face);
		}
	}

	public void setX(double x) {
		this.x = x;
	}

	public void setY(double y) {
		this.y = y;
	}

}
