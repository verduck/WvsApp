package wvs.wz.property;

import java.io.IOException;

import wvs.wz.WzImg;
import wvs.wz.WzInputStream;
import wvs.wz.WzObject;

public class WzVector2D extends WzObject {

	private int x;
	private int y;
	
	public WzVector2D(WzObject parent, int x, int y) {
		super("origin", parent);
		this.x = x;
		this.y = y;
	}
	
	public WzVector2D(String name, WzObject parent, WzImg parentImg) {
		super(name, parent, parentImg);
		// TODO Auto-generated constructor stub
	}
	
	public void parse(WzInputStream reader) {
		try {
			x = reader.readCompressedInt();
			y = reader.readCompressedInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}

}
