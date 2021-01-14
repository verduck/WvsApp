package wvs.wz.property;

import java.io.IOException;
import java.util.ArrayList;

import wvs.wz.WzImg;
import wvs.wz.WzInputStream;
import wvs.wz.WzObject;

public class WzConvex2D extends WzObject {

	private ArrayList<WzObject> props = new ArrayList<>();
	
	public WzConvex2D(String name, WzObject parent, WzImg parentImg) {
		super(name, parent, parentImg);
		// TODO Auto-generated constructor stub
	}
	
	public void parse(WzInputStream reader, long offset) {
		int entriesNum = 0;
		try {
			entriesNum = reader.readCompressedInt();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (int i = 0; i < entriesNum; i++) {
			props.add(parseExtendedProperty(reader, name, this, parentImg, offset));
		}
	}

}
