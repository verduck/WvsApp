package wvs.wz.property;

import wvs.wz.WzObject;

public class WzVariant<T> extends WzObject {

	private T value;
	
	protected WzVariant(String name, WzObject parent, T value) {
		super(name, parent);
		// TODO Auto-generated constructor stub
		this.value = value;
	}

	public T getValue() {
		return value;
	}
	
}
