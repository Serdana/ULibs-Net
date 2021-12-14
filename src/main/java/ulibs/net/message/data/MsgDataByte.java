package main.java.ulibs.net.message.data;

import main.java.ulibs.common.helpers.ByteH;

public class MsgDataByte extends MessageData<Byte> {
	public MsgDataByte(byte data) {
		super(data);
	}
	
	@Override
	protected byte[] returnNewCache() {
		return ByteH.getBytes(data);
	}
	
	@Override
	public void fromBytes(byte[] data) {
		this.data = data[0];
	}
	
	@Override
	public short defaultSize() {
		return 1;
	}
}
