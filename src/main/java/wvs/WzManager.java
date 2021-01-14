package wvs;

import wvs.wz.WzFile;
import wvs.wz.WzObject;

public class WzManager {
	private static WzManager instance;
	private WzFile wz;
	
	public WzManager() {
		wz = new WzFile("Data.wz");
		wz.parse();
	}
	
	public static WzManager getInstance() {
		if (instance == null) {
			instance = new WzManager();
		}
		return instance;
	}
	
	public WzFile getWz() {
		return wz;
	}
	
	public WzObject getObject(String path) {
		return wz.getObject(path);
	}
}
