package main.java.ulibs.net.message.data;

import main.java.ulibs.common.helpers.ByteH;

public class MsgDataString extends MessageData<String> {
	@Override
	protected byte[] returnNewCache() {
		return ByteH.getBytes(data);
	}
	
	@Override
	public void fromBytes(byte[] data) {
		this.data = ByteH.getString(data);
	}
	
	@Override
	public short defaultSize() {
		return 0;
	}
}
