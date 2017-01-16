package Controller;

import db.DataBase;
import http.HttpMethod;
import model.Action;
import model.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import http.HttpRequest;
import http.HttpResponse;
import model.User;

public class CreateUserController extends AbstractController {
	private static final Logger LOG = LoggerFactory.getLogger(CreateUserController.class);

	@Override
	protected void doGet(HttpRequest request, HttpResponse response) {
		Action.MEMBER_INSERT.getMethod().execute(request, response);
	}

	@Override
	void doPost(HttpRequest request, HttpResponse response) {
		Action.MEMBER_INSERT.getMethod().execute(request, response);
	}

}
