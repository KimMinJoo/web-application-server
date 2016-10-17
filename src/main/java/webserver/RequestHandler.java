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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RequestHandler extends Thread {
	private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);
	private static final String READER_FINISH = "";
	private static final String REQUEST_SEPERATOR = " ";
	private static final int REQUEST_VIEW_INDEX = 1;

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

			String view = getViewByRequest(line);
			line = bufferedReader.readLine();
			
			DataOutputStream dos = new DataOutputStream(out);
			byte[] body = Files.readAllBytes(new File("./webapp" + view).toPath());
			response200Header(dos, body.length);
			responseBody(dos, body);
		} catch (IOException e) {
			LOGGER.error(e.getMessage());
		}
	}

	private String getViewByRequest(String line) {
		// TODO Auto-generated method stub
		String[] request = line.split(REQUEST_SEPERATOR);
		
		if (request.length < 2) {
			return null;
		}

		return request[REQUEST_VIEW_INDEX];
	}

	private boolean isNotFinish(String line) {
		// TODO Auto-generated method stub
		return READER_FINISH.equals(line) == false;
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
