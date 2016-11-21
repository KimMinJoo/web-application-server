package model;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import util.HttpRequestUtils;
import util.IOUtils;
import webserver.Request;
import webserver.RequestHandler;

public enum RequestExecuteMethod {
	MAIN((RequestParam requestParam) -> {
		DataOutputStream dos = new DataOutputStream(requestParam.getOut());
		String viewName = requestParam.getUrl().isEmpty() ? ViewName.MAIN : requestParam.getUrl();
		byte[] body = getBodyFromUrl(viewName);
		String contentType = getType(viewName);
		response200Header(dos, body.length, contentType);
		responseBody(dos, body);
	}),
	MEMBER_INSERT((RequestParam requestParam) -> {
		String body = getBodyByRequest(requestParam.getBufferedReader(), requestParam.getContentLength());
		excuteMembership(body);

		DataOutputStream dos = new DataOutputStream(requestParam.getOut());
		response302Header(dos);
	}),
	LOGIN((RequestParam requestParam) -> {
		String body = getBodyByRequest(requestParam.getBufferedReader(), requestParam.getContentLength());
		Map<String, String> paramMap = convertParamMap(body);
		String userId = paramMap.get(UserParamKey.ID);
		User user = DataBase.findUserById(userId);
		DataOutputStream dos = new DataOutputStream(requestParam.getOut()); 
		if (successLogin(user)) {
			String viewName = ViewName.MAIN;
			String contentType = getType(viewName);
			responseLoginSuccessHeader(dos, viewName, contentType);
		} else {
			String viewName = ViewName.LOGIN_FAIL;
			String contentType = getType(viewName);
			responseLoginFailHeader(dos, viewName, contentType);
		}
	}),
	MEMBER_LIST((RequestParam requestParam) -> {
		boolean logined = isLogined(requestParam.getCookies());
		DataOutputStream dos = new DataOutputStream(requestParam.getOut()); 
		if (logined) {
			String viewName = ViewName.MEMBER_LIST;
			String contentType = getType(viewName);
			response302Header(dos, viewName, contentType);
		} else {
			requestParam.setUrl(ViewName.MAIN);
			MAIN.getRequest().executeRequest(requestParam);
		}
	});
	Request request;
	
	RequestExecuteMethod(Request request) {
		this.request = request;
	}

	private static String getType(String viewName) {
		// TODO Auto-generated method stub
		return viewName.endsWith("css") ? "css" : "html";
	}

	private static boolean isLogined(Map<String, String> cookies) {
		return Boolean.parseBoolean(cookies.get("logined"));
	}

	public Request getRequest() {
		return request;
	}

	public void setRequest(Request request) {
		this.request = request;
	}

	private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
	
	private static String getBodyByRequest(BufferedReader bufferedReader, int contentLength) {
		try {
			return IOUtils.readData(bufferedReader, contentLength);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private static void excuteMembership(String body) {
		Map<String, String> paramMap = convertParamMap(body);
		User user = createUserByParmaMap(paramMap);
		DataBase.addUser(user);
	}
	
	private static Map<String, String> convertParamMap(String params) {
		return HttpRequestUtils.parseQueryString(params);
	}
	
	private static User createUserByParmaMap(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		User user = new User(paramMap.get(UserParamKey.ID), paramMap.get(UserParamKey.PASSWORD), paramMap.get(UserParamKey.NAME),
				paramMap.get(UserParamKey.EMAIL));

		return user;
	}
	
	private static void response302Header(DataOutputStream dos) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: /index.html \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

	}
	
	private static void response302Header(DataOutputStream dos, String url, String contentType) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Content-Type: text/" + contentType + ";charset=utf-8\r\n");
			dos.writeBytes("Location: " + url + " \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

	}
	
	private static boolean successLogin(User user) {
		// TODO Auto-generated method stub
		return user != null;
	}
	
	private static void responseLoginSuccessHeader(DataOutputStream dos, String url, String contentType) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Content-Type: text/" + contentType + ";charset=utf-8\r\n");
			dos.writeBytes("Set-Cookie: logined= true\r\n");
			dos.writeBytes("Location: " + url + " \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	private static void responseLoginFailHeader(DataOutputStream dos, String url, String contentType) {
		// TODO Auto-generated method stub
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Content-Type: text/" + contentType + ";charset=utf-8\r\n");
			dos.writeBytes("Set-Cookie: logined= false\r\n");
			dos.writeBytes("Location: " + url + " \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

	}
	
	public static byte[] getBodyFromUrl(String url) {
		byte[] byteArr = new byte[0];
		if (isEmptyUrl(url)) {
			url = ViewName.MAIN;
		}
		try {
			File file = new File("./webapp" + url);
			if (Files.exists(file.toPath())) {
				return Files.readAllBytes(file.toPath());
			} 
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return byteArr;
	}
	
	private static boolean isEmptyUrl(String url) {
		return url.isEmpty() || url.length() == 1;
	}
	
	public static void response200Header(DataOutputStream dos, int lengthOfBodyContent, String contentType) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/" + contentType + ";charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}
	
	public static void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	private boolean isPost(String method) {
		// TODO Auto-generated method stub
		return "POST".equals(method);
	}
	
	private boolean isGet(String method) {
		return "GET".equals(method);
	}
}
