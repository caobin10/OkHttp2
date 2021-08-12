package org.chuck.http;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.KeyStore;

import javax.net.ssl.SSLContext;

import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.scheme.LayeredSocketFactory;
import org.apache.http.params.HttpParams;

public class SSLLayeredSocketFactory implements LayeredSocketFactory{
	private SSLContext sslContext;
	
	public SSLLayeredSocketFactory() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public Socket connectSocket(Socket arg0, String arg1, int arg2,
			InetAddress arg3, int arg4, HttpParams arg5) throws IOException,
			UnknownHostException, ConnectTimeoutException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Socket createSocket() throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isSecure(Socket arg0) throws IllegalArgumentException {
		return true;
	}

	@Override
	public Socket createSocket(Socket arg0, String arg1, int arg2, boolean arg3)
			throws IOException, UnknownHostException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	
	
	
	
	
	public KeyStore createKeyStore(URL url,String password){
		return null;
	}
	
	
	public SSLContext getSSLContext(){
		if(sslContext==null){
			sslContext=createSSLContext();
		}
		return sslContext;
	}
	
	
	
	private SSLContext createSSLContext(){
		return null;
	}
}
