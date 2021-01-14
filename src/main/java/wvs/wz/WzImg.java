package wvs.wz;

import java.io.IOException;

import wvs.wz.property.WzProperty;

public class WzImg extends WzObject {

	private WzInputStream reader;
	private int size;
	private int checkSum;
	private long offset;
	private WzProperty prop;
	
	WzImg(WzInputStream reader, String name, WzObject parent) {
		this(reader, name, parent, 0, 0, 0);
	}
	
	WzImg(WzInputStream reader, String name, WzObject parent, int size, int checkSum, long offset) {
		super(name, parent);
		// TODO Auto-generated constructor stub
		this.reader = reader;
		this.size = size;
		this.checkSum = checkSum;
		this.offset = offset;
	}
	
	public void parse() {
		try {
			reader.getChannel().position(offset);
			prop = (WzProperty) parseExtendedProperty(reader, name, parent, this, offset);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public WzProperty getProp() {
		if (prop == null) {
			parse();
		}
		return prop;
	}
	
	public void delProp() {
		prop = null;
	}

	public int getSize() {
		return size;
	}

	public int getCheckSum() {
		return checkSum;
	}

}
