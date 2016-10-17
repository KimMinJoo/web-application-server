package webserver;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import db.DataBase;
import model.User;
import util.HttpRequestUtils;
import util.IOUtils;

public class RequestHandler extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
	private static final String READER_FINISH = "";
	private static final String REQUEST_SEPERATOR = " ";
	private static final int REQUEST_URL_INDEX = 1;
	private static final String DEFAULT_PATH = "/index.html";
	private static final String MEMBERSHIP_URL = "/user/create";
	private static final String LOGIN_URL = "/user/login.html";
	private static final String URL_VALUE_SEPERATOR = "?";
	private static final String USER_ID_KEY = "userId";
	private static final String USER_PASSWORD_KEY = "password";
	private static final String USER_NAME_KEY = "name";
	private static final String USER_EMAIL_KEY = "email";

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		LOGGER.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			// TODO 사용자 요청에 대한 처리는 이 곳에 구현하면 된다.
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));

			String line = bufferedReader.readLine();

			if (line == null) {
				LOGGER.info("InputStream is null");
				return;
			}
			String url = getUrlByRequest(line);

			int contentLength = 0;
			boolean logined = false;
			while (isNotFinish(line)) {
				line = bufferedReader.readLine();
				LOGGER.debug("header : {}", line);

				if (isContentLengthLine(line)) {
					contentLength = getContentLength(line);
				}
				if (isCookieLine(line)) {

				}
			}

			if (isMembershipUrl(url)) {
				// excuteMembershipGET(out, url);

				String body = getBodyByRequest(bufferedReader, contentLength);
				excuteMembership(body);

				DataOutputStream dos = new DataOutputStream(out);
				response302Header(dos);
				return;
			}
			if (isLoginUrl(url)) {
				String body = getBodyByRequest(bufferedReader, contentLength);
				Map<String, String> paramMap = convertParamMap(body);
				String userId = paramMap.get(USER_ID_KEY);
				User user = DataBase.findUserById(userId);
				DataOutputStream dos = new DataOutputStream(out); 
				if (successLogin()) {
					responseLoginSuccessHeader(dos);
				} else {

				}
			} else {

				responseResource(out, url);
			}
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	private boolean successLogin() {
		// TODO Auto-generated method stub
		return false;
	}

	private boolean isCookieLine(String line) {
		return line.contains("Cookie");
	}

	private boolean isContentLengthLine(String line) {
		return line.contains("Content-Length");
	}

	private String getBodyByRequest(BufferedReader bufferedReader, int contentLength) throws IOException {
		return IOUtils.readData(bufferedReader, contentLength);
	}

	private void excuteMembership(String body) {
		Map<String, String> paramMap = convertParamMap(body);
		User user = createUserByParmaMap(paramMap);
		DataBase.addUser(user);
	}

	private int getContentLength(String line) {
		String[] headerTokens = line.split(":");
		return Integer.parseInt(headerTokens[1].trim());
	}

	// GET방식일떄
	private void excuteMembershipGET(OutputStream out, String url) {
		String path = gegPathByUrl(url);
		String params = getParmaByUrl(url);
		excuteMembership(params);
	}

	private void responseResource(OutputStream out, String url) throws IOException {
		DataOutputStream dos = new DataOutputStream(out);
		LOGGER.debug("url : " + url);
		byte[] body = getBodyFromUrl(url);
		response200Header(dos, body.length);
		responseBody(dos, body);
	}

	private User createUserByParmaMap(Map<String, String> paramMap) {
		// TODO Auto-generated method stub
		User user = new User(paramMap.get(USER_ID_KEY), paramMap.get(USER_PASSWORD_KEY), paramMap.get(USER_NAME_KEY),
				paramMap.get(USER_EMAIL_KEY));

		return user;
	}

	private Map<String, String> convertParamMap(String params) {
		return HttpRequestUtils.parseQueryString(params);
	}

	private String getParmaByUrl(String url) {
		int index = url.indexOf(URL_VALUE_SEPERATOR);
		if (index == -1) {
			return null;
		}
		return url.substring(index + 1);
	}

	private String gegPathByUrl(String url) {
		int index = url.indexOf(URL_VALUE_SEPERATOR);
		if (index == -1) {
			return DEFAULT_PATH;
		}
		return url.substring(0, index);
	}

	private boolean isMembershipUrl(String url) {
		return url.startsWith(MEMBERSHIP_URL);
	}

	private boolean isLoginUrl(String url) {
		return LOGIN_URL.equals(url);
	}

	private byte[] getBodyFromUrl(String url) throws IOException {
		if (isEmptyUrl(url)) {
			url = DEFAULT_PATH;
		}
		return Files.readAllBytes(new File("./webapp" + url).toPath());
	}

	private boolean isEmptyUrl(String url) {
		return url.isEmpty() || url.length() == 1;
	}

	private String getUrlByRequest(String line) {
		String[] request = line.split(REQUEST_SEPERATOR);

		if (request.length < 2) {
			return null;
		}

		return request[REQUEST_URL_INDEX];
	}

	private boolean isNotFinish(String line) {
		return READER_FINISH.equals(line) == false;
	}

	private void responseLoginSuccessHeader(DataOutputStream dos) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html\r\n");
			dos.writeBytes("Set-Cookie: logined= true\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

	}

	private void response302Header(DataOutputStream dos) {
		try {
			dos.writeBytes("HTTP/1.1 302 Redirect \r\n");
			dos.writeBytes("Location: /index.html \r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}

	}

	private void response200Header(DataOutputStream dos, int lengthOfBodyContent) {
		try {
			dos.writeBytes("HTTP/1.1 200 OK \r\n");
			dos.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
			dos.writeBytes("Content-Length: " + lengthOfBodyContent + "\r\n");
			dos.writeBytes("\r\n");
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	private void responseBody(DataOutputStream dos, byte[] body) {
		try {
			dos.write(body, 0, body.length);
			dos.flush();
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}
}
