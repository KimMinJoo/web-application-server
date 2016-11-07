package model;

import java.io.BufferedReader;
import java.io.OutputStream;
import java.util.Map;

public class RequestParam {
	private OutputStream out;
	private BufferedReader bufferedReader;
	private int contentLength;
	private String url;
	private Map<String, String> cookies;
	
	public RequestParam(){};
	
	public RequestParam(OutputStream out, BufferedReader bufferedReader, int contentLength) {
		this(out, bufferedReader, contentLength, null);
	}
	
	public RequestParam(OutputStream out, BufferedReader bufferedReader, int contentLength, String url) {
		this(out, bufferedReader, contentLength, url, null);
	}

	
	public RequestParam(OutputStream out, BufferedReader bufferedReader, int contentLength, String url, Map<String, String> cookies) {
		this.out = out;
		this.bufferedReader = bufferedReader;
		this.contentLength = contentLength;
		this.url = url;
		this.cookies = cookies;
	}
	
	public OutputStream getOut() {
		return out;
	}
	public void setOut(OutputStream out) {
		this.out = out;
	}
	public BufferedReader getBufferedReader() {
		return bufferedReader;
	}
	public void setBufferedReader(BufferedReader bufferedReader) {
		this.bufferedReader = bufferedReader;
	}
	public int getContentLength() {
		return contentLength;
	}
	public void setContentLength(int contentLength) {
		this.contentLength = contentLength;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Map<String, String> getCookies() {
		return cookies;
	}

	public void setCookies(Map<String, String> cookies) {
		this.cookies = cookies;
	}
	
	
}
