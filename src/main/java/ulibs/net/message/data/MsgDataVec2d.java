package main.java.ulibs.net.message.data;

import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.math.Vec2d;
import main.java.ulibs.common.utils.exceptions.ByteException;

public class MsgDataVec2d extends MessageData<Vec2d> {
	@Override
	protected byte[] returnNewCache() {
		return ByteH.combineBytes(ByteH.getBytes(data.getX()), ByteH.getBytes(data.getY()));
	}
	
	@Override
	public void fromBytes(byte[] data) {
		byte[] f1 = new byte[8], f2 = new byte[8];
		
		for (int i = 0; i < 8; i++) {
			f1[i] = data[i];
			f2[i] = data[i + 8];
		}
		
		try {
			this.data = new Vec2d(ByteH.getDouble(f1), ByteH.getDouble(f2));
		} catch (ByteException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public short defaultSize() {
		return 16;
	}
}
