package main.java.ulibs.net.utils;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;

public final class Connection {
	private final ChannelHandlerContext ctx;
	public final String ip;
	
	public Connection(ChannelHandlerContext ctx) {
		this.ctx = ctx;
		this.ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		Console.print(WarningType.Debug, "Connection was made with IP '" + ip + "'!");
	}
	
	public void send(ByteBuf buf) {
		ctx.writeAndFlush(buf);
	}
	
	public boolean isActive() {
		return ctx.channel().isActive();
	}
}
