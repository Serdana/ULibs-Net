package main.java.ulibs.net.message.data;

import java.nio.ByteBuffer;

import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.utils.exceptions.ByteException;
import main.java.ulibs.net.message.data.MsgDataLongArray.ArrayWrap;

public class MsgDataLongArray extends MessageData<ArrayWrap> {
	public MsgDataLongArray(long[] data) {
		super(new ArrayWrap(data));
	}
	
	@Override
	protected byte[] returnNewCache() {
		ByteBuffer buf = ByteBuffer.allocate(data.array.length * 8);
		for (long f : data.array) {
			buf.putLong(f);
		}
		
		return buf.array();
	}
	
	@Override
	public void fromBytes(byte[] data) {
		long[] longs = new long[data.length / 8];
		
		for (int i = 0; i < data.length / 8; i++) {
			byte[] tempBytes = new byte[8];
			tempBytes[0] = data[i * 8 + 0];
			tempBytes[1] = data[i * 8 + 1];
			tempBytes[2] = data[i * 8 + 2];
			tempBytes[3] = data[i * 8 + 3];
			tempBytes[4] = data[i * 8 + 4];
			tempBytes[5] = data[i * 8 + 5];
			tempBytes[6] = data[i * 8 + 6];
			tempBytes[7] = data[i * 8 + 7];
			
			try {
				longs[i] = ByteH.getLong(tempBytes);
			} catch (ByteException e) {
				e.printStackTrace();
			}
		}
		
		this.data = new ArrayWrap(longs);
	}
	
	@Override
	public short defaultSize() {
		return 0;
	}
	
	static class ArrayWrap {
		private final long[] array;
		
		private ArrayWrap(long[] array) {
			this.array = array;
		}
	}
}
