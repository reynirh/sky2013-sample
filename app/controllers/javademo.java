package controllers;

import play.mvc.Controller;
import play.mvc.Result;
import views.html.index;

public class javademo extends Controller {

    // dæmi 1.c java based action í scala projecti

    public static Result index() {
        return ok(index.render("Java enabled",null));
    }
}
