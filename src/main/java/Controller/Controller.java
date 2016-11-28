package Controller;

import http.HttpMethod;
import model.Action;
import model.Method;
import model.Url;

public enum Controller {
	MAIN(Action.MAIN.getMethod(), Action.DEFAULT.getMethod()),
	MEMBER_INSERT(Action.DEFAULT.getMethod(), Action.MEMBER_INSERT.getMethod()),
	LOGIN(Action.DEFAULT.getMethod(), Action.LOGIN.getMethod()),
	MEMBER_LIST(Action.DEFAULT.getMethod(), Action.MEMBER_LIST.getMethod());
	
	Method doGet;
	Method doPost;
	
	Controller(Method doGet, Method doPost) {
		this.doGet = doGet;
		this.doPost = doPost;
	}
	
	public Method service(HttpMethod httpMethod) {
		Method method;
		if (httpMethod.isPost()) {
			method = doPost;
		} else {
			method = doGet;
		}
		
		if (method == null) {
			method = Action.DEFAULT.getMethod();
		}
		
		return method;
	}
}
