package website.actions;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * User: Oleksiy Pylypenko
 * Date: 8/12/12
 * Time: 11:07 PM
 */
@Controller
@RequestMapping(value = "/app")
public class ApplicationActions {

    @RequestMapping(method = RequestMethod.GET, value = "/main")
    public void showMain() {

    }
}
