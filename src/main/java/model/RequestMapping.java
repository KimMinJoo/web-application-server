package model;

import webserver.Request;

public enum RequestMapping {
	MAIN(Action.MAIN, "", RequestExecuteMethod.MAIN.getRequest()),
	MEMBER_INSERT(Action.MEMBER_INSERT, "POST", RequestExecuteMethod.MEMBER_INSERT.getRequest()),
	MEMBER_INSERT_GET(Action.MEMBER_INSERT, "GET", RequestExecuteMethod.MEMBER_INSERT.getRequest()),
	LOGIN(Action.LOGIN, "POST", RequestExecuteMethod.LOGIN.getRequest()),
	MEMBER_LIST(Action.MEMBER_LIST, "POST", RequestExecuteMethod.MEMBER_LIST.getRequest());
	
	String url;
	String method;
	Request request;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	RequestMapping(String url, String method, Request request) {
		this.url = url;
		this.method = method;
		this.request = request;
	}
	
	public static RequestMapping getInstance(String url) {
		for (RequestMapping value : values()) {
			if (value.is(url)) {
				return value;
			}
		}
		return MAIN;
	}
	
	public static RequestMapping getInstance(String url, String method) {
		for (RequestMapping value : values()) {
			if (value.is(url, method)) {
				return value;
			}
		}
		return MAIN;
	}
	
	public boolean is(String url) {
		return this.url.equals(url);
	}
	
	public boolean is(String url, String method) {
		if (isPost(method)) {
			return this.url.equals(url) && this.method.equals(method);
		}
		if (isGet(method)) {
			return this.url.startsWith(url) && this.method.equals(method);
		}
		return false;
	}

	private boolean isPost(String method) {
		// TODO Auto-generated method stub
		return "POST".equals(method);
	}
	
	private boolean isGet(String method) {
		return "GET".equals(method);
	}

}
