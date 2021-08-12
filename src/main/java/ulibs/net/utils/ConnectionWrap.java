package main.java.ulibs.net.utils;

public abstract class ConnectionWrap<T extends ConnectionWrap<T, D>, D extends IConnectionData> {
	public final Connection con;
	private D data;
	
	public ConnectionWrap(Connection con) {
		this.con = con;
	}
	
	public Connection getConnection() {
		return con;
	}
	
	public abstract boolean is(T con);
	
	@SuppressWarnings("unchecked")
	public final T setData(D data) {
		this.data = data;
		return (T) this;
	}
	
	public final D data() {
		return data;
	}
}
