package main.java.ulibs.net.message.data;

import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.utils.exceptions.ByteException;

public class MsgDataFloat extends MessageData<Float> {
	public MsgDataFloat(float data) {
		super(data);
	}
	
	@Override
	protected byte[] returnNewCache() {
		return ByteH.getBytes(data);
	}
	
	@Override
	public void fromBytes(byte[] data) {
		try {
			this.data = ByteH.getFloat(data);
		} catch (ByteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public short defaultSize() {
		return 4;
	}
}
