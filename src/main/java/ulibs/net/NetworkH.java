package main.java.ulibs.net;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import main.java.ulibs.common.helpers.ByteH;
import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.common.utils.exceptions.ByteException;
import main.java.ulibs.net.exceptions.NetworkException;
import main.java.ulibs.net.exceptions.NetworkException.Reason;
import main.java.ulibs.net.message.Message;
import main.java.ulibs.net.message.MessageH;
import main.java.ulibs.net.message.data.MessageData;
import main.java.ulibs.net.message.data.MsgDataBoolean;
import main.java.ulibs.net.message.data.MsgDataBooleanArray;
import main.java.ulibs.net.message.data.MsgDataByte;
import main.java.ulibs.net.message.data.MsgDataByteArray;
import main.java.ulibs.net.message.data.MsgDataDouble;
import main.java.ulibs.net.message.data.MsgDataDoubleArray;
import main.java.ulibs.net.message.data.MsgDataEnum;
import main.java.ulibs.net.message.data.MsgDataFloat;
import main.java.ulibs.net.message.data.MsgDataFloatArray;
import main.java.ulibs.net.message.data.MsgDataInteger;
import main.java.ulibs.net.message.data.MsgDataIntegerArray;
import main.java.ulibs.net.message.data.MsgDataLong;
import main.java.ulibs.net.message.data.MsgDataLongArray;
import main.java.ulibs.net.message.data.MsgDataShort;
import main.java.ulibs.net.message.data.MsgDataShortArray;
import main.java.ulibs.net.message.data.MsgDataString;
import main.java.ulibs.net.message.data.MsgDataVec2d;
import main.java.ulibs.net.message.data.MsgDataVec2f;
import main.java.ulibs.net.message.data.MsgDataVec2i;
import main.java.ulibs.net.utils.Connection;

public class NetworkH {
	private static final Map<Short, Class<? extends Message>> MESSAGE_MAP = new HashMap<Short, Class<? extends Message>>();
	private static final Map<Class<? extends Message>, Short> MESSAGE_MAP_REV = new HashMap<Class<? extends Message>, Short>();
	private static final Map<Class<? extends Message>, MessageH<?>> HANDLER_MAP = new HashMap<Class<? extends Message>, MessageH<?>>();
	private static final Map<Class<? extends MessageData<?>>, Short> DATA_TYPE_MAP = new HashMap<Class<? extends MessageData<?>>, Short>();
	private static final Map<Short, Class<? extends MessageData<?>>> DATA_TYPE_MAP_REV = new HashMap<Short, Class<? extends MessageData<?>>>();
	
	static {
		registerMessageData(MsgDataFloat.class);
		registerMessageData(MsgDataDouble.class);
		registerMessageData(MsgDataByte.class);
		registerMessageData(MsgDataShort.class);
		registerMessageData(MsgDataInteger.class);
		registerMessageData(MsgDataLong.class);
		
		registerMessageData(MsgDataString.class);
		registerMessageData(MsgDataBoolean.class);
		registerMessageData(MsgDataEnum.class);
		
		registerMessageData(MsgDataFloatArray.class);
		registerMessageData(MsgDataDoubleArray.class);
		registerMessageData(MsgDataByteArray.class);
		registerMessageData(MsgDataShortArray.class);
		registerMessageData(MsgDataIntegerArray.class);
		registerMessageData(MsgDataLongArray.class);
		
		registerMessageData(MsgDataBooleanArray.class);
		
		registerMessageData(MsgDataVec2i.class);
		registerMessageData(MsgDataVec2f.class);
		registerMessageData(MsgDataVec2d.class);
	}
	
	public static final void registerMessageData(Class<? extends MessageData<?>> msg) {
		if (DATA_TYPE_MAP.size() > Short.MAX_VALUE) {
			Console.print(WarningType.FatalError, "Registered too many message types!", new NetworkException(Reason.other));
			return;
		}
		
		short n = (short) DATA_TYPE_MAP.size();
		DATA_TYPE_MAP.put(msg, n);
		DATA_TYPE_MAP_REV.put(n, msg);
	}
	
	public static final void registerMessage(Class<? extends Message> msg) {
		if (MESSAGE_MAP.size() > Short.MAX_VALUE) {
			Console.print(WarningType.FatalError, "Registered too many message types!", new NetworkException(Reason.other));
			return;
		}
		
		short n = (short) MESSAGE_MAP.size();
		MESSAGE_MAP.put(n, msg);
		MESSAGE_MAP_REV.put(msg, n);
	}
	
	public static final void registerHandler(MessageH<?> handler) {
		Console.print(WarningType.RegisterDebug,
				"Registered handler '" + handler.getClass().getSimpleName() + "' with the message '" + handler.getMessageClazz().getSimpleName() + "'");
		HANDLER_MAP.put(handler.getMessageClazz(), handler);
	}
	
	public static final short getDataTypeID(@SuppressWarnings("rawtypes") Class<? extends MessageData> clazz) {
		return DATA_TYPE_MAP.get(clazz);
	}
	
	public static final Class<? extends MessageData<?>> getDataTypeFromID(short id) {
		return DATA_TYPE_MAP_REV.get(id);
	}
	
	public static final void sendMessage(Connection con, Message msg) {
		if (msg == null) {
			Console.print(WarningType.Error, "Tried to send an invalid Message!", new NetworkException(Reason.invalid_message));
			return;
		}
		
		msg.finalizeData();
		
		byte[] body = msg.toBytes();
		if (body.length <= 0) {
			Console.print(WarningType.Error, "Tried to send an empty Message!", new NetworkException(Reason.empty_message));
			return;
		} else if (body.length > Short.MAX_VALUE) {
			Console.print(WarningType.Error, "Tried to send too big of a Message!", new NetworkException(Reason.too_big));
			return;
		}
		
		ByteBuffer buf = ByteBuffer.wrap(new byte[body.length + 4]);
		
		buf.putShort((short) body.length);
		buf.putShort(MESSAGE_MAP_REV.get(msg.getClass()));
		buf.put(body);
		buf.flip();
		
		con.send(Unpooled.wrappedBuffer(buf.array()));
	}
	
	static final void readMessage(Connection con, ByteBuf in) {
		List<Byte> bytes = new ArrayList<Byte>();
		while (in.isReadable()) {
			bytes.add(in.readByte());
		}
		in.release();
		
		for (int i = 0; i < bytes.size(); i++) {
			try {
				byte[] tempBytes = new byte[2];
				for (int j = 0; j < 2; j++) {
					tempBytes[j] = bytes.get(i + j);
				}
				
				short size = ByteH.getShort(tempBytes);
				if (size <= 0) {
					Console.print(WarningType.Error, "Tried to read an invalid Message! (" + i + "/" + bytes.size() + ")", new NetworkException(Reason.empty_message));
					return;
				}
				
				tempBytes = new byte[2];
				for (int j = 0; j < 2; j++) {
					tempBytes[j] = bytes.get(i + j + 2);
				}
				
				short id = ByteH.getShort(tempBytes);
				Class<? extends Message> m = MESSAGE_MAP.get(id);
				if (m == null) {
					Console.print(WarningType.Error, "Recieved an unknown message!", new NetworkException(Reason.unknown_message));
					i += size + 3;
					continue;
				}
				
				tempBytes = new byte[size];
				for (int j = 0; j < size; j++) {
					tempBytes[j] = bytes.get(i + j + 4);
				}
				
				i += size + 3;
				HANDLER_MAP.get(m).processMessage(con, tempBytes);
			} catch (ByteException e) {
				Console.print(WarningType.Warning, e.getMessage(), e);
			}
		}
	}
	
	public static boolean isValidIP(String ip) {
		if (ip == null || ip.isEmpty()) {
			return false;
		}
		
		String[] parts = ip.split("\\.", -1);
		if (parts.length != 4) {
			return false;
		}
		
		try {
			for (String s : parts) {
				int i = Integer.parseInt(s);
				if (i < 0 || i > 255) {
					return false;
				}
			}
		} catch (NumberFormatException e) {
			return false;
		}
		
		return true;
	}
	
	public static boolean isValidPort(String port) {
		if (port == null || port.isEmpty()) {
			return false;
		}
		
		try {
			int i = Integer.parseInt(port);
			return i < 65535 && i > 0;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
