package org.chuck.http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.GzipDecompressingEntity;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.http.util.EntityUtilsHC4;

public class HttpClientHolder {
	public static final int START=4;
	public static final int PROGRESS=5;
	public static final int RETRY=6;
	public static final int FINISHED=7;	
	public static final int SUCCESS=1;
	public static final int FAILURE=0;
	public static final int FAILURE_CONNECT=2;
	public static final int FAILURE_SERVICE=3;	
	public static final String CHARSET_DEFAULT="UTF-8";
	
	private HttpContext httpContext;
	private CloseableHttpClient httpClient;
	
	public HttpClientHolder() {
		httpContext=new BasicHttpContext();				
	}
	
	
	

    
    
    /**
	 * 传递的须是JSON或序列化后的JSON对象
	 * @return
	 * @throws Exception 
	 */
    public String doPost(String uri,List<NameValuePair> params) throws Exception {
    	String result="";
		CloseableHttpClient httpClient=getHttpClient();
		try{			
			HttpEntity httpEntity=new UrlEncodedFormEntity(params,"UTF-8");
			HttpPost httpPost=new HttpPost(uri);
			httpPost.getParams().setBooleanParameter(CoreProtocolPNames.USE_EXPECT_CONTINUE, false);
			httpPost.setEntity(httpEntity);
			httpPost.setHeader("Accept-Encoding", "gzip");
			CloseableHttpResponse httpResponse=httpClient.execute(httpPost);
			try {
			
			
				int statusCode=httpResponse.getStatusLine().getStatusCode();
				switch(statusCode){
//				case HttpStatus.SC_CONTINUE:
//				case HttpStatus.SC_SWITCHING_PROTOCOLS:
//				case HttpStatus.SC_PROCESSING:
//					break;
				case HttpStatus.SC_OK:
//				case HttpStatus.SC_CREATED:
//				case HttpStatus.SC_ACCEPTED:
//				case HttpStatus.SC_NON_AUTHORITATIVE_INFORMATION:
//				case HttpStatus.SC_NO_CONTENT:
//				case HttpStatus.SC_RESET_CONTENT:
//				case HttpStatus.SC_PARTIAL_CONTENT:
//				case HttpStatus.SC_MULTI_STATUS:
					
					HttpEntity responseEntity=httpResponse.getEntity();
					if (responseEntity != null) {
						Header header=responseEntity.getContentEncoding();
						if(header!=null){
							header.getValue().contains("gzip");
							responseEntity=new GzipDecompressingEntity(responseEntity);
						}
//						String charset=EntityUtils.getContentCharSet(responseEntity);
//						charset=charset==null?CHARSET_DEFAULT:charset;
//						result=new String(EntityUtils.toByteArray(httpEntity),charset);
						InputStream is = responseEntity.getContent();
						BufferedReader reader=new BufferedReader(new InputStreamReader(is));
						String line="";
						while((line=reader.readLine()) != null){
							result+=line;
						}						
						is.close();
						reader.close();
	                }
					EntityUtilsHC4.consumeQuietly(responseEntity);
					
					break;
//				case HttpStatus.SC_MULTIPLE_CHOICES:
				case HttpStatus.SC_MOVED_PERMANENTLY:
				case HttpStatus.SC_MOVED_TEMPORARILY:
//				case HttpStatus.SC_SEE_OTHER:
//				case HttpStatus.SC_NOT_MODIFIED:
//				case HttpStatus.SC_USE_PROXY:
//				case HttpStatus.SC_TEMPORARY_REDIRECT:
					break;
//				case HttpStatus.SC_BAD_REQUEST:
//				case HttpStatus.SC_UNAUTHORIZED:
//				case HttpStatus.SC_PAYMENT_REQUIRED:
//				case HttpStatus.SC_FORBIDDEN:
				case HttpStatus.SC_NOT_FOUND:
//				case HttpStatus.SC_METHOD_NOT_ALLOWED:
//				case HttpStatus.SC_NOT_ACCEPTABLE:
//				case HttpStatus.SC_PROXY_AUTHENTICATION_REQUIRED:
//				case HttpStatus.SC_REQUEST_TIMEOUT:
//				case HttpStatus.SC_CONFLICT:
//				case HttpStatus.SC_GONE:
//				case HttpStatus.SC_LENGTH_REQUIRED:
//				case HttpStatus.SC_PRECONDITION_FAILED:
//				case HttpStatus.SC_REQUEST_TOO_LONG:
//				case HttpStatus.SC_REQUEST_URI_TOO_LONG:
//				case HttpStatus.SC_UNSUPPORTED_MEDIA_TYPE:
//				case HttpStatus.SC_REQUESTED_RANGE_NOT_SATISFIABLE:
//				case HttpStatus.SC_EXPECTATION_FAILED:
//				case HttpStatus.SC_INSUFFICIENT_SPACE_ON_RESOURCE:
//				case HttpStatus.SC_METHOD_FAILURE:
//				case HttpStatus.SC_UNPROCESSABLE_ENTITY:
//				case HttpStatus.SC_LOCKED:
//				case HttpStatus.SC_FAILED_DEPENDENCY:
					doWhenFail(statusCode);
					break;
//				case HttpStatus.SC_INTERNAL_SERVER_ERROR:
//				case HttpStatus.SC_NOT_IMPLEMENTED:
//				case HttpStatus.SC_BAD_GATEWAY:
//				case HttpStatus.SC_SERVICE_UNAVAILABLE:
//				case HttpStatus.SC_GATEWAY_TIMEOUT:
//				case HttpStatus.SC_HTTP_VERSION_NOT_SUPPORTED:
//				case HttpStatus.SC_INSUFFICIENT_STORAGE:
//					break;
				}	
				
				
				
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			}finally{
				httpResponse.close();
			}
		}catch(Exception e){			
			e.printStackTrace();
			throw e;
		}finally{
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return result;
	}
    
	
	public CloseableHttpClient getHttpClient(){
//		if(httpClient==null){
			httpClient=createHttpClient();
//		}
		return httpClient;
	}
	
	
	public CloseableHttpClient createHttpClient(){
	    return HttpClientBuilder.create().setSSLSocketFactory(SSLConnectionSocketFactory.getSocketFactory()).build();
	}
	
	public void close(){
		if(httpClient!=null){
			try {
				httpClient.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	
	public void doWhenFail(int statusCode){};
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

}
