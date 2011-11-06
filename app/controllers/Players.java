package controllers;

import play.mvc.With;

/**
 *
 * @author gehef
 */
@With(Secure.class)
public class Players extends CRUD {
}
