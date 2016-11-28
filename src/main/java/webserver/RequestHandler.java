package webserver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;

public class RequestHandler extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

	private Socket connection;

	public RequestHandler(Socket connectionSocket) {
		this.connection = connectionSocket;
	}

	public void run() {
		LOGGER.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
				connection.getPort());

		try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()) {
			HttpRequest request = new HttpRequest(in);
            HttpResponse response = new HttpResponse(out);
            HttpMethod httpMethod = request.getHttpMethod();
			RequestMapping requestMapping = RequestMapping.getInstance(request.getPath());
			requestMapping.getController().service(httpMethod).execute(request, response);

		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

}
