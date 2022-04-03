package main.java.ulibs.net.message.data;

import java.nio.ByteBuffer;

import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.utils.ArrayWrap;
import main.java.ulibs.common.utils.exceptions.ByteException;

public class MsgDataFloatArray extends MessageData<ArrayWrap.Float> {
	public MsgDataFloatArray(float[] data) {
		super(new ArrayWrap.Float(data));
	}
	
	@Override
	protected byte[] returnNewCache() {
		ByteBuffer buf = ByteBuffer.allocate(data.array.length * 4);
		for (float f : data.array) {
			buf.putFloat(f);
		}
		
		return buf.array();
	}
	
	@Override
	public void fromBytes(byte[] data) {
		float[] floats = new float[data.length / 4];
		
		for (int i = 0; i < data.length / 4; i++) {
			byte[] tempBytes = new byte[4];
			tempBytes[0] = data[i * 4 + 0];
			tempBytes[1] = data[i * 4 + 1];
			tempBytes[2] = data[i * 4 + 2];
			tempBytes[3] = data[i * 4 + 3];
			
			try {
				floats[i] = ByteH.getFloat(tempBytes);
			} catch (ByteException e) {
				e.printStackTrace();
			}
		}
		
		this.data = new ArrayWrap.Float(floats);
	}
	
	@Override
	public short defaultSize() {
		return 0;
	}
}
