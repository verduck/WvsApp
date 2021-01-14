package wvs.wz;

import java.io.IOException;

import wvs.wz.property.WzCanvas;
import wvs.wz.property.WzConvex2D;
import wvs.wz.property.WzProperty;
import wvs.wz.property.WzSound;
import wvs.wz.property.WzUOL;
import wvs.wz.property.WzVector2D;

public abstract class WzObject {
	protected String name;
	protected WzObject parent;
	protected WzImg parentImg;
	
	protected WzObject(String name, WzObject parent, WzImg parentImg) {
		this.name = name;
		this.parent = parent;
		this.parentImg = parentImg;
	}
	
	protected WzObject(String name, WzObject parent) {
		this(name, parent, null);
	}
	
	public String getName() {
		return name;
	}
	
	public WzObject getParent() {
		return parent;
	}
	
	public WzImg getParentImg() {
		return parentImg;
	}
	
	public String getFullPath() {
		String path = "";
		
		WzObject curObj = this;
		while (curObj != null) {
			path += curObj.getName();
			path += "/";
			curObj = curObj.getParent();
		}
		
		return path;
	}
	
	public WzObject getObject(String path) {
		String[] seperatedPath = path.split("/");
		WzObject curObj = this;
		for (int i = 0; i < seperatedPath.length; i++) {
			if (curObj == null) {
				break;
			}
			if (seperatedPath[i].equals(".."))
	        {
	            curObj = curObj.parent;
	            continue;
	        }
			if (curObj instanceof WzFile) {
				curObj = ((WzFile) curObj).get();
				continue;
			} else if (curObj instanceof WzDirectory) {
				curObj = ((WzDirectory) curObj).get(seperatedPath[i]);
				continue;
			} else if (curObj instanceof WzProperty) {
				curObj = ((WzProperty) curObj).get(seperatedPath[i]);
				continue;
			} else if (curObj instanceof WzCanvas) {
				curObj = ((WzCanvas) curObj).get(seperatedPath[i]);
				continue;
			} else if (curObj instanceof WzUOL) {
				curObj = ((WzUOL) curObj).get();
				i--;
				continue;
			}
		}
		if (curObj instanceof WzUOL) {
			return ((WzUOL) curObj).get();
		}
		return curObj;
	}
	
	protected WzObject parseExtendedProperty(WzInputStream reader, String name, WzObject parent, WzImg img, long offset) {
		try {
			String typeName = reader.readStringBlock(offset);
			if (typeName.equals("Property")) {
				WzProperty prop = new WzProperty(name, parent, img);
				prop.parse(reader, offset);
				return (WzObject) prop;
			} else if (typeName.equals("Canvas")) {
				WzCanvas prop = new WzCanvas(name, parent, img);
				prop.parse(reader, offset);
				return (WzObject) prop;
			} else if (typeName.equals("Shape2D#Vector2D")) {
				WzVector2D prop = new WzVector2D(name, parent, img);
				prop.parse(reader);
				return (WzObject) prop;
			} else if (typeName.equals("Shape2D#Convex2D")) {
				WzConvex2D prop = new WzConvex2D(name, parent, img);
				prop.parse(reader, offset);
				return (WzObject) prop;
			} else if (typeName.equals("Sound_DX8")) {
				WzSound prop = new WzSound(name, parent, img);
				prop.parse(reader, offset);
				return (WzObject) prop;
			} else if (typeName.equals("UOL")) {
				WzUOL prop = new WzUOL(name, parent);
				prop.parse(reader, offset);
				return (WzObject) prop;
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
