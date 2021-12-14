package main.java.ulibs.net.message.data;

public class MsgDataBoolean extends MessageData<Boolean> {
	public MsgDataBoolean(boolean data) {
		super(data);
	}
	
	@Override
	protected byte[] returnNewCache() {
		return new byte[] { (byte) (data ? 1 : 0) };
	}
	
	@Override
	public void fromBytes(byte[] data) {
		this.data = data[0] == 1;
	}
	
	@Override
	public short defaultSize() {
		return 1;
	}
}
