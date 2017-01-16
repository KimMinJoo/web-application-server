package webserver;


import Controller.ControllerInterface;
import Controller.CreateUserController;
import Controller.LoginController;
import Controller.UserListController;
import model.Url;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by kim.minjoo on 2017-01-16.
 */
public class RequestMapplingMap {
    private static Map<String, ControllerInterface> controllers = new HashMap<String, ControllerInterface>();

    static {
        controllers.put(Url.MEMBER_INSERT, new CreateUserController());
        controllers.put(Url.LOGIN, new LoginController());
        controllers.put(Url.MEMBER_LIST, new UserListController());
    }

    public static ControllerInterface getController(String requestUrl) {
        return controllers.get(requestUrl);
    }
}
