package webserver;

import java.io.BufferedReader;
import java.io.OutputStream;

import model.RequestParam;

@FunctionalInterface
public interface Request {
	public void executeRequest(RequestParam requestParam);
}
