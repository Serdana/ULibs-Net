package main.java.ulibs.net.message.data;

import java.nio.ByteBuffer;

import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.utils.ArrayWrap;
import main.java.ulibs.common.utils.exceptions.ByteException;

public class MsgDataDoubleArray extends MessageData<ArrayWrap.Double> {
	public MsgDataDoubleArray(double[] data) {
		super(new ArrayWrap.Double(data));
	}
	
	@Override
	protected byte[] returnNewCache() {
		ByteBuffer buf = ByteBuffer.allocate(data.array.length * 8);
		for (double f : data.array) {
			buf.putDouble(f);
		}
		
		return buf.array();
	}
	
	@Override
	public void fromBytes(byte[] data) {
		double[] doubles = new double[data.length / 8];
		
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
				doubles[i] = ByteH.getDouble(tempBytes);
			} catch (ByteException e) {
				e.printStackTrace();
			}
		}
		
		this.data = new ArrayWrap.Double(doubles);
	}
	
	@Override
	public short defaultSize() {
		return 0;
	}
}
