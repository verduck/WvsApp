package wvs.wz;

import java.io.FileNotFoundException;
import java.io.IOException;

public class WzFile extends WzObject {

	private WzInputStream reader;
	private WzDirectory root;
	
	public WzFile(String name) {
		super(name.split(".wz")[0], null);
		// TODO Auto-generated constructor stub
		try {
			reader = new WzInputStream(name);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void parse() {
		try {
			reader.readHeader();
			reader.setHash(1);
			root = new WzDirectory(reader, name, this);
			root.parse();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public WzObject get() {
		return root;
	}
	
}
