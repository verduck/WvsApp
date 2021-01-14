package wvs.wz.property;

import java.io.IOException;

import wvs.wz.WzInputStream;
import wvs.wz.WzObject;

public class WzUOL extends WzObject {

	private String uol;
	
	public WzUOL(String name, WzObject parent) {
		super(name, parent);
		// TODO Auto-generated constructor stub
	}
	
	public void parse(WzInputStream reader, long offset) {
		try {
			uol = reader.readStringBlock(offset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public String getUol() {
		return uol;
	}
	
	public WzObject get() {
		WzObject curObj = getParent();
		curObj = curObj.getObject(uol);
		return curObj;
	}

}
