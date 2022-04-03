package main.java.ulibs.net.message.data;

import java.nio.ByteBuffer;

import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.utils.ArrayWrap;
import main.java.ulibs.common.utils.exceptions.ByteException;

public class MsgDataShortArray extends MessageData<ArrayWrap.Short> {
	public MsgDataShortArray(short[] data) {
		super(new ArrayWrap.Short(data));
	}
	
	@Override
	protected byte[] returnNewCache() {
		ByteBuffer buf = ByteBuffer.allocate(data.array.length * 2);
		for (short f : data.array) {
			buf.putShort(f);
		}
		
		return buf.array();
	}
	
	@Override
	public void fromBytes(byte[] data) {
		short[] shorts = new short[data.length / 2];
		
		for (int i = 0; i < data.length / 2; i++) {
			byte[] tempBytes = new byte[2];
			tempBytes[0] = data[i * 2 + 0];
			tempBytes[1] = data[i * 2 + 1];
			
			try {
				shorts[i] = ByteH.getShort(tempBytes);
			} catch (ByteException e) {
				e.printStackTrace();
			}
		}
		
		this.data = new ArrayWrap.Short(shorts);
	}
	
	@Override
	public short defaultSize() {
		return 0;
	}
}
