package org.chuck.http;

import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;

public class HttpResponseHandle implements ResponseHandler<String>{

	@Override
	public String handleResponse(HttpResponse arg0) throws ClientProtocolException, IOException {
		int statusCode=arg0.getStatusLine().getStatusCode();
		switch(statusCode){
		case HttpStatus.SC_OK:
			break;		
		case HttpStatus.SC_MOVED_PERMANENTLY://永久性的重定向
		case HttpStatus.SC_MOVED_TEMPORARILY://暂时性的重定向
			//从HTTP Header中取出重定向地址
			
			break;
		case HttpStatus.SC_NOT_FOUND:
			break;
			default:
		}
		return null;
	}

}
