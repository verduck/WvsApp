package wvs.wz.property;

import java.io.IOException;
import java.util.ArrayList;

import wvs.wz.WzImg;
import wvs.wz.WzInputStream;
import wvs.wz.WzObject;


public class WzProperty extends WzObject {

	private ArrayList<WzObject> props = new ArrayList<>();
	
	public WzProperty(String name, WzObject parent, WzImg parentImg) {
		super(name, parent, parentImg);
		// TODO Auto-generated constructor stub
	}
	
	public void parse(WzInputStream reader, long offset) {
		try {
			reader.skip(2);
			int entriesNum = reader.readCompressedInt();
			for (int i = 0; i < entriesNum; i++) {
				String name = reader.readStringBlock(offset);
				byte type = reader.readByte();
				switch (type) {
				case 0:
		        {
		            props.add(new WzVariant<Object>(name, this, null));
		            break;
		        }
		        case 2:
		        case 0x0B:
		        {
		        	props.add(new WzVariant<Short>(name, this, reader.readShort()));
		            break;
		        }
		        case 3:
		        case 0x13:
		        {
		        	props.add(new WzVariant<Integer>(name, this, reader.readCompressedInt()));
		            break;
		        }
		        case 0x14:
		            // long
		            break;
		        case 4:
		        {
		            byte isZero = reader.readByte();
		            if (isZero == 0x80) {
		            	props.add(new WzVariant<Float>(name, this, reader.readFloat()));
		            } else {
		            	props.add(new WzVariant<Float>(name, this, 0.0f));
		            }
		        }
		            break;
		        case 5:
		        {
		        	props.add(new WzVariant<Double>(name, this, reader.readDouble()));
		            break;
		        }
		        case 8:
		        {
		        	props.add(new WzVariant<String>(name, this, reader.readStringBlock(offset)));
		            break;
		        }
		        case 9:
		        {
		            int size = reader.readInt();
		            long eob = reader.getChannel().position() + (int) size;
		            props.add(parseExtendedProperty(reader, name, this, parentImg, offset));
		            if (reader.getChannel().position() != eob) {
		            	reader.getChannel().position(eob);
		            }
		            break;
		        }
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ArrayList<WzObject> getProps() {
		return props;
	}
	
	public WzObject get(String name) {
		for (WzObject obj : props) {
			if (obj.getName().equals(name)) {
				return obj;
			}
		}
		return null;
	}

}
