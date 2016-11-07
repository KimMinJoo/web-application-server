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
import model.RequestMapping;
import model.RequestParam;
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
			Map<String, String> cookies = null;
			boolean logined = false;
			while (isNotFinish(line)) {
				line = bufferedReader.readLine();
				LOGGER.debug("header : {}", line);

				if (isContentLengthLine(line)) {
					contentLength = getContentLength(line);
				}
				if (isCookieLine(line)) {
					cookies = HttpRequestUtils.parseCookies(getHeaderTokens(line)[1]);
				}
			}
			RequestMapping requestMapping = RequestMapping.getInstance(url);

			RequestParam requestParam = new RequestParam(out, bufferedReader, contentLength, url, cookies);
			requestMapping.getRequest().executeRequest(requestParam);

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	private boolean isCookieLine(String line) {
		return line.contains("Cookie");
	}

	private boolean isContentLengthLine(String line) {
		return line.contains("Content-Length");
	}

	private int getContentLength(String line) {
		String[] headerTokens = getHeaderTokens(line);
		return Integer.parseInt(headerTokens[1].trim());
	}

	private String[] getHeaderTokens(String line) {
		String[] headerTokens = line.split(":");
		return headerTokens;
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

}
