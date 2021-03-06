package main.java.ulibs.net.message.data;

import main.java.ulibs.net.NetworkH;

public abstract class MessageData<T> {
	public final short key;
	protected T data;
	private byte[] cache;
	
	public MessageData(T data) {
		this.key = NetworkH.getDataTypeID(getClass());
		this.data = data;
	}
	
	//@formatter:off
	public abstract void fromBytes(byte[] data);
	protected abstract byte[] returnNewCache();
	public abstract short defaultSize();
	//@formatter:on
	
	public final short getKey() {
		return key;
	}
	
	public final byte[] getBytes() {
		if (cache == null) {
			cache = returnNewCache();
		}
		
		return cache;
	}
	
	public final int size() {
		return getBytes().length;
	}
	
	public T getData() {
		return data;
	}
}
