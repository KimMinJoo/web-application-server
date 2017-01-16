package Controller;

import http.HttpRequest;
import http.HttpResponse;

public interface ControllerInterface {
	void service(HttpRequest request, HttpResponse response);
}
