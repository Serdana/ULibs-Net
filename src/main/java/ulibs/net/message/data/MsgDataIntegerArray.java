package main.java.ulibs.net.message.data;

import java.nio.ByteBuffer;

import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.utils.exceptions.ByteException;
import main.java.ulibs.net.message.data.MsgDataIntegerArray.ArrayWrap;

public class MsgDataIntegerArray extends MessageData<ArrayWrap> {
	public MsgDataIntegerArray(int[] data) {
		super(new ArrayWrap(data));
	}
	
	@Override
	protected byte[] returnNewCache() {
		ByteBuffer buf = ByteBuffer.allocate(data.array.length * 4);
		for (int f : data.array) {
			buf.putInt(f);
		}
		
		return buf.array();
	}
	
	@Override
	public void fromBytes(byte[] data) {
		int[] ints = new int[data.length / 4];
		
		for (int i = 0; i < data.length / 4; i++) {
			byte[] tempBytes = new byte[4];
			tempBytes[0] = data[i * 4 + 0];
			tempBytes[1] = data[i * 4 + 1];
			tempBytes[2] = data[i * 4 + 2];
			tempBytes[3] = data[i * 4 + 3];
			
			try {
				ints[i] = ByteH.getInt(tempBytes);
			} catch (ByteException e) {
				e.printStackTrace();
			}
		}
		
		this.data = new ArrayWrap(ints);
	}
	
	@Override
	public short defaultSize() {
		return 0;
	}
	
	static class ArrayWrap {
		private final int[] array;
		
		private ArrayWrap(int[] array) {
			this.array = array;
		}
	}
}
