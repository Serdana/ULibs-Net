package main.java.ulibs.net.message.data;

import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.utils.exceptions.ByteException;

public class MsgDataDouble extends MessageData<Double> {
	public MsgDataDouble(double data) {
		super(data);
	}
	
	@Override
	protected byte[] returnNewCache() {
		return ByteH.getBytes(data);
	}
	
	@Override
	public void fromBytes(byte[] data) {
		try {
			this.data = ByteH.getDouble(data);
		} catch (ByteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public short defaultSize() {
		return 8;
	}
}
