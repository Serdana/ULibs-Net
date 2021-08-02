package main.java.ulibs.net.message.data;

import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.math.Vec2i;
import main.java.ulibs.common.utils.exceptions.ByteException;

public class MsgDataVec2i extends MessageData<Vec2i> {
	@Override
	protected byte[] returnNewCache() {
		return ByteH.combineBytes(ByteH.getBytes(data.getX()), ByteH.getBytes(data.getY()));
	}
	
	@Override
	public void fromBytes(byte[] data) {
		byte[] f1 = new byte[4], f2 = new byte[4];
		
		for (int i = 0; i < 4; i++) {
			f1[i] = data[i];
			f2[i] = data[i + 4];
		}
		
		try {
			this.data = new Vec2i(ByteH.getInt(f1), ByteH.getInt(f2));
		} catch (ByteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public short defaultSize() {
		return 8;
	}
}
