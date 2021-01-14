package wvs.map;

import java.util.Comparator;

import javafx.scene.canvas.GraphicsContext;

public abstract class MapEntity implements Comparable<MapEntity> {
	protected int id;
	protected double x;
	protected double y;
	protected int z;
	protected int zM;
	
	public abstract void update(double delta);
	public abstract void render(GraphicsContext gc);
	
	@Override
	public int compareTo(MapEntity other) {
		return Integer.compare(z, other.z);
	}
	
	
}
