package wvs.wz.property;

import java.io.IOException;

import wvs.wz.WzImg;
import wvs.wz.WzInputStream;
import wvs.wz.WzObject;

public class WzSound extends WzObject {

	private int playTime;
	private byte[] data;
	
	public WzSound(String name, WzObject parent, WzImg parentImg) {
		super(name, parent, parentImg);
		// TODO Auto-generated constructor stub
	}

	public void parse(WzInputStream reader, long offset) {
		try {
			reader.skip(1);
			int len = reader.readCompressedInt();
			playTime = reader.readCompressedInt();
			reader.skip(82);
			data = new byte[len];
			reader.read(data);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public int getPlayTime() {
		return playTime;
	}

	public byte[] getData() {
		return data;
	}
	
}
