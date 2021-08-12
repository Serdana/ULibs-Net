package main.java.ulibs.net;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import main.java.ulibs.common.utils.Console;
import main.java.ulibs.common.utils.Console.WarningType;
import main.java.ulibs.net.utils.Connection;
import main.java.ulibs.net.utils.ConnectionWrap;

public abstract class ServerNetwork<T extends ConnectionWrap<T, ?>> implements Runnable { //TODO make this class not require netty!
	private List<T> connections = new ArrayList<T>();
	private List<Connection> unsetConnections = new ArrayList<Connection>();
	
	public void setConnectionData(T con) {
		connections.add(con);
		unsetConnections.remove(con.con);
		printConnectionDebug(con);
	}
	
	protected void printConnectionDebug(T con) {
		Console.print(WarningType.Debug, "Connection added with IP '" + con.con.ip + "'!");
	}
	
	private void removeConnection(ChannelHandlerContext ctx) {
		T con0 = getConnection(ctx);
		if (con0 != null) {
			connections.remove(con0);
		}
		
		Connection con1 = getUnsetConnection(ctx);
		if (con1 != null) {
			unsetConnections.remove(con1);
		}
	}
	
	public List<T> getConnectionsExceptSelf(T self) {
		List<T> cons = new ArrayList<T>();
		for (T con : connections) {
			if (!con.is(self)) {
				cons.add(con);
			}
		}
		
		return cons;
	}
	
	public T getConnectionWrap(Connection con) {
		for (T t : connections) {
			if (t.con.ip.equals(con.ip)) {
				return t;
			}
		}
		
		return null;
	}
	
	private Connection getFirstConnection(ChannelHandlerContext ctx) {
		T t = getConnection(ctx);
		return t != null ? t.con : getUnsetConnection(ctx);
	}
	
	private T getConnection(ChannelHandlerContext ctx) {
		String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		for (T con : connections) {
			if (con.con.ip.equals(ip)) {
				return con;
			}
		}
		
		return null;
	}
	
	private Connection getUnsetConnection(ChannelHandlerContext ctx) {
		String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
		for (Connection con : unsetConnections) {
			if (con.ip.equals(ip)) {
				return con;
			}
		}
		
		return null;
	}
	
	public List<T> getConnections() {
		return connections;
	}
	
	//@formatter:off
	protected abstract void onConnect();
	protected abstract void onDisconnect();
	//@formatter:on
	
	@Override
	public void run() {
		Console.print(WarningType.Info, "Started Server Network Handler!");
		
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup);
			b.channel(NioServerSocketChannel.class);
			b.childHandler(new ChannelInitializer<SocketChannel>() {
				@Override
				public void initChannel(SocketChannel ch) throws Exception {
					ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
						@Override
						public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
							unsetConnections.add(new Connection(ctx));
							onConnect();
						}
						
						@Override
						public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
							String ip = ((InetSocketAddress) ctx.channel().remoteAddress()).getAddress().getHostAddress();
							Console.print(WarningType.Debug, ip + " lost connection!");
							
							removeConnection(ctx);
							onDisconnect();
						}
						
						@Override
						public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
							if (cause instanceof IOException) {
								Console.print(WarningType.Error, cause.getLocalizedMessage());
							} else {
								cause.printStackTrace();
							}
						}
						
						@Override
						public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
							NetworkH.readMessage(getFirstConnection(ctx), (ByteBuf) msg);
						}
					});
				}
			}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			
			try {
				ChannelFuture f = b.bind(7777).sync();
				f.channel().closeFuture().sync();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		} finally {
			workerGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
