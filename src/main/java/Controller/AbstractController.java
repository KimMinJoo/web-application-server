package Controller;

import http.HttpMethod;
import http.HttpRequest;
import http.HttpResponse;

/**
 * Created by kim.minjoo on 2017-01-16.
 */
public abstract class AbstractController implements ControllerInterface {
    @Override
    public void service(HttpRequest request, HttpResponse response) {
        HttpMethod httpMethod = request.getHttpMethod();
        if (httpMethod.isPost()) {
            doPost(request, response);
        } else {
            doGet(request, response);
        }
    }

    abstract protected void doGet(HttpRequest request, HttpResponse response);

    abstract void doPost(HttpRequest request, HttpResponse response);
}
