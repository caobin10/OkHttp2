package org.chuck.http;

import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.HttpHostConnectException;

import android.text.TextUtils;

public class HttpException extends Exception{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8557692003973400931L;
	private String msg=null;
	public HttpException(Exception e) {
		super();
		try{
			if(e instanceof HttpHostConnectException){
				msg="连接远程地址失败！";
			}else 
			if(e instanceof ConnectException){
				msg="无法连接到网络！";
			}else 
			if(e instanceof ConnectTimeoutException){
				msg="连接超时，请重试！";
			}else 
			if(e instanceof UnknownHostException){
				msg="连接远程地址失败，请检查地址是否正确！";
			}else 
			if(e instanceof SocketException){
				msg="网络连接出错！";
			}else 
			if(e instanceof SocketTimeoutException){
				msg="连接超时，请重试！";
			}else 
			if(e instanceof ClientProtocolException){
				msg="HTTP请求参数错误！";
			}else 
			if(e instanceof NullPointerException){
				msg="远程服务出错！";
			}else 
			if(e instanceof IllegalArgumentException){
				msg="服务端地址非法！";
			}
			else{
				if(e==null||TextUtils.isEmpty(e.getMessage())){
					msg="";
				}else{
					msg=e.getMessage();
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	
	
	public HttpException(String message){
		super(message);
		this.msg=message;
	}
	public HttpException(int statusCode){
		super(String.valueOf(statusCode));
		switch(statusCode){
		case HttpStatus.SC_NOT_FOUND:
			this.msg="未找到服务！";
			break;
		}		
	}
	
	public String getMessage(){
		return msg;
	}
}
