package main.java.ulibs.net.message.data;

import main.java.ulibs.net.message.data.MsgDataByteArray.ArrayWrap;

public class MsgDataByteArray extends MessageData<ArrayWrap> {
	public MsgDataByteArray(byte[] data) {
		super(new ArrayWrap(data));
	}
	
	@Override
	protected byte[] returnNewCache() {
		return data.array;
	}
	
	@Override
	public void fromBytes(byte[] data) {
		this.data = new ArrayWrap(data);
	}
	
	@Override
	public short defaultSize() {
		return 0;
	}
	
	static class ArrayWrap {
		private final byte[] array;
		
		private ArrayWrap(byte[] array) {
			this.array = array;
		}
	}
}
