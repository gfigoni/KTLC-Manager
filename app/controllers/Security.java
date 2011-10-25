package controllers;

/**
 *
 * @author gehef
 */
public class Security extends Secure.Security {

    static boolean authenticate(String username, String password) {
        return ("admin".equalsIgnoreCase(username) && "iSFL3Gsmct9RCgOQ1zjL".equals(password));
    }
}
