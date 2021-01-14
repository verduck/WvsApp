package wvs.wz;

import java.io.IOException;
import java.util.ArrayList;

public class WzDirectory extends WzObject {

	private WzInputStream reader;
	private int size;
	private int checkSum;
	private long offset;
	private ArrayList<WzDirectory> dirs = new ArrayList<>();
	private ArrayList<WzImg> imgs = new ArrayList<>();
	
	WzDirectory(WzInputStream reader, String name, WzObject parent) {
		this(reader, name, parent, 0, 0, 0);
	}
	
	WzDirectory(WzInputStream reader, String name, WzObject parent, int size, int checkSum, long offset) {
		super(name, parent);
		// TODO Auto-generated constructor stub
		this.reader = reader;
		this.setSize(size);
		this.setCheckSum(checkSum);
		this.setOffset(offset);
	}

	
	public void parse() {
		try {
			int entriesNum = reader.readCompressedInt();
			for (int i = 0; i < entriesNum; i++) {
				byte type = reader.readByte();
				String name = null;
				int size;
				int checkSum;
				long offset;
				long rememberPos = 0;
				
				if (type == 1) {
					reader.readInt();
					reader.readShort();
					reader.readInt();
					continue;
				} else if (type == 2) {
					int stringOffset = reader.readInt();
					rememberPos = reader.getChannel().position();
					reader.getChannel().position(reader.getHeader().getContentPos() + stringOffset);
					type = reader.readByte();
					name = reader.readString();
				} else if (type == 3 || type == 4) {
					name = reader.readString();
					rememberPos = reader.getChannel().position();
				}
				reader.getChannel().position(rememberPos);
				size = reader.readCompressedInt();
				checkSum = reader.readCompressedInt();
				offset = reader.readOffset();
				if (type == 3) {
					dirs.add(new WzDirectory(reader, name, this, size, checkSum, offset));
				} else if (type == 4) {
					imgs.add(new WzImg(reader, name, this, size, checkSum, offset));
				}
			}
			
			for (WzDirectory dir : dirs) {
				reader.getChannel().position(dir.offset);
				dir.parse();
			}
			
			/*for (WzImg img : imgs) {
				img.parse();
			}*/
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public WzObject get(String name) {
		for (WzDirectory dir : dirs) {
			if (dir.name.equals(name)) {
				return dir;
			}
		}
		
		for (WzImg img : imgs) {
			if (img.name.equals(name)) {
				return img.getProp();
			}
		}
		return null;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public int getCheckSum() {
		return checkSum;
	}

	public void setCheckSum(int checkSum) {
		this.checkSum = checkSum;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}
}
