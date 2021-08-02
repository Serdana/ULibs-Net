package main.java.ulibs.net.message.data;

public class MsgDataEnum extends MessageData<Byte> {
	@Override
	protected byte[] returnNewCache() {
		return new byte[] { data };
	}
	
	@Override
	public void fromBytes(byte[] data) {
		this.data = data[0];
	}
	
	public <T extends Enum<T>> T returnAsEnum(Class<T> clazz) {
		return clazz.getEnumConstants()[data];
	}
	
	public <T extends Enum<T>> MsgDataEnum setEnum(T e) {
		this.data = (byte) e.ordinal();
		return this;
	}
	
	@Override
	public short defaultSize() {
		return 1;
	}
	
	/** Use {@link MsgDataEnum#returnAsEnum} instead! */
	@Deprecated
	@Override
	public Byte getData() {
		return super.getData();
	}
	
	/** Use {@link MsgDataEnum#setEnum} instead! */
	@Deprecated
	@Override
	public MessageData<Byte> setData(Byte data) {
		return super.setData(data);
	}
}
