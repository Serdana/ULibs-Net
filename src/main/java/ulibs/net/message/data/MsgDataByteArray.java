package main.java.ulibs.net.message.data;

import main.java.ulibs.common.utils.ArrayWrap;

public class MsgDataByteArray extends MessageData<ArrayWrap.Byte> {
	public MsgDataByteArray(byte[] data) {
		super(new ArrayWrap.Byte(data));
	}
	
	@Override
	protected byte[] returnNewCache() {
		return data.array;
	}
	
	@Override
	public void fromBytes(byte[] data) {
		this.data = new ArrayWrap.Byte(data);
	}
	
	@Override
	public short defaultSize() {
		return 0;
	}
}
