package main.java.ulibs.net;

import java.io.IOException;
import java.net.ConnectException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import main.java.ulibs.common.helpers.CollectionsH;
import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.net.exceptions.NetworkException;
import main.java.ulibs.net.exceptions.NetworkException.Reason;
import main.java.ulibs.net.message.Message;
import main.java.ulibs.net.utils.Connection;

public class ClientNetwork implements Runnable {
	private final List<Message> sendQueue = new ArrayList<Message>();
	private volatile boolean shouldConnect, isConnected;
	
	public final String ip;
	public final int port;
	private final Supplier<Boolean> isRunning;
	
	private volatile Connection connectionToServer;
	
	public ClientNetwork(String ip, int port, Supplier<Boolean> isRunning) {
		this.ip = ip;
		this.port = port;
		this.isRunning = isRunning;
	}
	
	public void sendMessage(Message msg) {
		sendQueue.add(msg);
	}
	
	public void tryConnecting() {
		if (isConnected) {
			Console.print(WarningType.FatalError, "Tried connecting to a Server while already connected to one!", new NetworkException(Reason.already_connected));
			return;
		}
		
		shouldConnect = true;
	}
	
	public void stopConnection() {
		shouldConnect = false;
		isConnected = false;
	}
	
	@Override
	public void run() {
		Console.print(WarningType.Info, "Started Client Network Handler!");
		
		while (isRunning.get()) {
			if (!shouldConnect) {
				continue;
			}
			
			EventLoopGroup workerGroup = new NioEventLoopGroup();
			try {
				Bootstrap b = new Bootstrap();
				b.group(workerGroup);
				b.channel(NioSocketChannel.class);
				b.option(ChannelOption.SO_KEEPALIVE, true);
				
				AdapterHandler obj = new AdapterHandler();
				b.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(obj);
					}
				});
				
				try {
					ChannelFuture f = b.connect(ip, port).sync();
					isConnected = true;
					while (isConnected) {
						if (connectionToServer == null) {
							continue;
						}
						
						for (Message msg : CollectionsH.copyList(sendQueue)) {
							if (msg != null) {
								obj.sendMessage(msg);
								sendQueue.remove(msg);
							} else {
								Console.print(WarningType.Warning, "Msg == null? Could be a problem?");
							}
						}
					}
					
					f.channel().closeFuture();
				} catch (Exception e) {
					if (e instanceof ConnectException) { //Grrr why can't i catch this?
						shouldConnect = false;
						isConnected = false;
						Console.print(WarningType.Error, e.getLocalizedMessage());
					} else {
						e.printStackTrace();
					}
				}
			} finally {
				workerGroup.shutdownGracefully();
			}
		}
	}
	
	private class AdapterHandler extends ChannelInboundHandlerAdapter {
		private void sendMessage(Message msg) {
			NetworkH.sendMessage(connectionToServer, msg);
		}
		
		@Override
		public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
			NetworkH.readMessage(connectionToServer, (ByteBuf) msg);
		}
		
		@Override
		public void channelActive(ChannelHandlerContext ctx) throws Exception {
			connectionToServer = new Connection(ctx);
		}
		
		@Override
		public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
			if (cause instanceof IOException) {
				Console.print(WarningType.Error, cause.getLocalizedMessage());
			} else {
				cause.printStackTrace();
			}
			
			ctx.close();
		}
	}
}
