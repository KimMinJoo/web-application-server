package model;

import http.HttpRequest;
import http.HttpResponse;

@FunctionalInterface
public interface Method {
	public void execute(HttpRequest request, HttpResponse response);
}
