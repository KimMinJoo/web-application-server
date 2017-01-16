package Controller;

import db.DataBase;
import http.HttpRequest;
import http.HttpResponse;
import model.Action;
import model.User;
import util.HttpRequestUtils;

import java.util.Collection;
import java.util.Map;

/**
 * Created by kim.minjoo on 2017-01-16.
 */
public class UserListController extends AbstractController {
    @Override
    protected void doGet(HttpRequest request, HttpResponse response) {
        Action.MEMBER_LIST.getMethod().execute(request, response);
    }

    @Override
    void doPost(HttpRequest request, HttpResponse response) {
        Action.MEMBER_LIST.getMethod().execute(request, response);
    }
}
