package main.java.ulibs.net.message;

import java.util.ArrayList;
import java.util.List;

import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.net.NetworkHandler;
import main.java.ulibs.net.exceptions.NetworkException;
import main.java.ulibs.net.exceptions.NetworkException.Reason;
import main.java.ulibs.net.message.data.MessageData;

public abstract class Message {
	private final List<MessageData<?>> list = new ArrayList<MessageData<?>>();
	
	public final void finalizeData() {
		setData(list);
	}
	
	protected abstract void setData(List<MessageData<?>> list);
	
	public final byte[] toBytes() {
		List<Byte> bytes = new ArrayList<Byte>();
		
		for (MessageData<?> data : list) {
			byte[] dataBytes = data.getBytes();
			
			if (dataBytes.length > Short.MAX_VALUE) {
				Console.print(WarningType.FatalError, "Tried to convert a message that was too big!", new NetworkException(Reason.too_big));
				return new byte[0];
			}
			
			byte[] forBytes = data.defaultSize() == 0 ?
					ByteH.combineBytes(ByteH.getBytes(NetworkHandler.getDataTypeID(data.getClass())), ByteH.getBytes((short) dataBytes.length), dataBytes) :
					ByteH.combineBytes(ByteH.getBytes(NetworkHandler.getDataTypeID(data.getClass())), dataBytes);
			
			for (byte b : forBytes) {
				bytes.add(b);
			}
		}
		
		byte[] arr = new byte[bytes.size()];
		for (int i = 0; i < bytes.size(); i++) {
			arr[i] = bytes.get(i);
		}
		
		return arr;
	}
}
