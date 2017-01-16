package Controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.Action;
import model.User;

/**
 * Created by kim.minjoo on 2017-01-16.
 */
public class LoginController extends AbstractController {

    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        Action.LOGIN.getMethod().execute(request, response);
    }

    @Override
    void doPost(HttpRequest request, HttpResponse response) {
        Action.LOGIN.getMethod().execute(request, response);
    }
}
