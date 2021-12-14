package main.java.ulibs.net.message;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.common.utils.exceptions.ByteException;
import main.java.ulibs.net.NetworkH;
import main.java.ulibs.net.exceptions.NetworkException;
import main.java.ulibs.net.exceptions.NetworkException.Reason;
import main.java.ulibs.net.message.data.MessageData;
import main.java.ulibs.net.utils.Connection;

public abstract class MessageH<T extends Message> {
	public final void processMessage(Connection con, byte[] datas) {
		List<Byte> bytes = new ArrayList<Byte>();
		for (byte b : datas) {
			bytes.add(b);
		}
		
		List<MessageData<?>> dataList = new ArrayList<MessageData<?>>();
		for (int i = 0; i < bytes.size(); i++) {
			try {
				byte[] tempBytes = new byte[2];
				for (int j = 0; j < 2; j++) {
					tempBytes[j] = bytes.get(i + j);
				}
				
				short id = ByteH.getShort(tempBytes);
				
				Class<? extends MessageData<?>> msgClazz = NetworkH.getDataTypeFromID(id);
				if (msgClazz == null) {
					Console.print(WarningType.Warning, "Recieved Unknown Message Data Type!", new NetworkException(Reason.unknown_message_data));
					return;
				}
				
				try {
					MessageData<?> obj = (MessageData<?>) msgClazz.getConstructors()[0].newInstance((Object) null);
					
					boolean hasSize = obj.defaultSize() == 0;
					short size = 0;
					if (hasSize) {
						tempBytes = new byte[2];
						for (int j = 0; j < 2; j++) {
							tempBytes[j] = bytes.get(i + j + 2);
						}
						size = ByteH.getShort(tempBytes);
					} else {
						size = obj.defaultSize();
					}
					
					tempBytes = new byte[size];
					for (int j = 0; j < size; j++) {
						tempBytes[j] = bytes.get(i + j + (hasSize ? 4 : 2));
					}
					
					obj.fromBytes(tempBytes);
					dataList.add(obj);
					i += size + (hasSize ? 3 : 1);
				} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
					e.printStackTrace();
				}
			} catch (ByteException e) {
				Console.print(WarningType.Warning, e.getMessage(), e);
			}
		}
		
		handleMessage(con, dataList);
	}
	
	protected abstract void handleMessage(Connection con, List<MessageData<?>> datas);
	
	public abstract Class<T> getMessageClazz();
}
