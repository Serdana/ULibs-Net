package main.java.ulibs.net.message.data;

public class MsgDataEnum extends MessageData<Byte> {
	public <T extends Enum<T>> MsgDataEnum(Enum<T> data) {
		super((byte) data.ordinal());
	}
	
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
}
