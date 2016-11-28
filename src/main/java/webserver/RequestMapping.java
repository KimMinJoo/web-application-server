package webserver;

import Controller.Controller;
import http.HttpMethod;
import model.Action;
import model.Method;
import model.Url;

public enum RequestMapping {
	MAIN(Url.MAIN, Controller.MAIN),
	MEMBER_INSERT(Url.MEMBER_INSERT, Controller.MEMBER_INSERT),
	LOGIN(Url.LOGIN, Controller.LOGIN),
	MEMBER_LIST(Url.MEMBER_LIST, Controller.MEMBER_LIST);
	
	String url;
	Controller controller;
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Controller getController() {
		return controller;
	}

	public void setController(Controller controller) {
		this.controller = controller;
	}

	RequestMapping(String url,Controller controller) {
		this.url = url;
		this.controller = controller;
	}
	
	public static RequestMapping getInstance(String url) {
		for (RequestMapping value : values()) {
			if (value.is(url)) {
				return value;
			}
		}
		return MAIN;
	}
	
	public boolean is(String url) {
		return this.url.equals(url);
	}

}
