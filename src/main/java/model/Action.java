package model;

import java.util.Collection;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import util.HttpRequestUtils;

public enum Action {
	DEFAULT((HttpRequest request, HttpResponse response) -> {
        response.sendRedirect("/user/request_failed.html");
	}),
	MAIN((HttpRequest request, HttpResponse response) -> {
		String viewName = getViewName(request.getPath());
		response.forward(viewName);
	}),
	MEMBER_INSERT((HttpRequest request, HttpResponse response) -> {
		User user = new User(request.getParameter("userId"), request.getParameter("password"),
                 request.getParameter("name"), request.getParameter("email"));
         DataBase.addUser(user);
         response.sendRedirect("/index.html");
	}),
	LOGIN((HttpRequest request, HttpResponse response) -> {
        User user = DataBase.findUserById(request.getParameter("userId"));
        if (user != null) {
            if (user.login(request.getParameter("password"))) {
                response.addHeader("Set-Cookie", "logined=true");
                response.sendRedirect("/index.html");
            } else {
                response.sendRedirect("/user/login_failed.html");
            }
        } else {
            response.sendRedirect("/user/login_failed.html");
        }
	}),
	MEMBER_LIST((HttpRequest request, HttpResponse response) -> {
        if (!isLogined(request.getHeader("Cookie"))) {
            response.sendRedirect("/user/login.html");
            return;
        }

        Collection<User> users = DataBase.findAll();
        StringBuilder sb = new StringBuilder();
        sb.append("<table border='1'>");
        for (User user : users) {
            sb.append("<tr>");
            sb.append("<td>" + user.getUserId() + "</td>");
            sb.append("<td>" + user.getName() + "</td>");
            sb.append("<td>" + user.getEmail() + "</td>");
            sb.append("</tr>");
        }
        sb.append("</table>");
        response.forwardBody(sb.toString());
	});
	private static final Logger LOGGER = LoggerFactory.getLogger(Action.class);
	Method method;
	
	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	Action(Method action) {
		this.method = action;
	}

	private static String getViewName(String path) {
		return isEmptyPath(path) ? ViewName.MAIN : path;
	}

	private static boolean isEmptyPath(String path) {
		return path.isEmpty() || isCurrentPath(path);
	}

	private static boolean isCurrentPath(String path) {
		return "/".equals(path);
	}

	private static boolean isLogined(String cookieValue) {
		Map<String, String> cookies = HttpRequestUtils.parseCookies(cookieValue);
        String value = cookies.get("logined");
        if (value == null) {
            return false;
        }
        return Boolean.parseBoolean(value);
	}

}
