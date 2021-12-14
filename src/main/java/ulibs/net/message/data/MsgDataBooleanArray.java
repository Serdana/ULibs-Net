package main.java.ulibs.net.message.data;

import java.nio.ByteBuffer;

import main.java.ulibs.net.message.data.MsgDataBooleanArray.ArrayWrap;

public class MsgDataBooleanArray extends MessageData<ArrayWrap> {
	public MsgDataBooleanArray(boolean[] data) {
		super(new ArrayWrap(data));
	}
	
	@Override
	protected byte[] returnNewCache() {
		ByteBuffer buf = ByteBuffer.allocate(data.array.length * 2);
		for (boolean f : data.array) {
			buf.put((byte) (f ? 1 : 0));
		}
		
		return buf.array();
	}
	
	@Override
	public void fromBytes(byte[] data) {
		boolean[] booleans = new boolean[data.length];
		
		for (int i = 0; i < data.length; i++) {
			booleans[i] = data[i] == 1;
		}
		
		this.data = new ArrayWrap(booleans);
	}
	
	@Override
	public short defaultSize() {
		return 0;
	}
	
	static class ArrayWrap {
		private final boolean[] array;
		
		private ArrayWrap(boolean[] array) {
			this.array = array;
		}
	}
}
