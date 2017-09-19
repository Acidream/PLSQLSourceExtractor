package org.home.settings;

/**
 * Created by oleg on 2017-09-19.
 */
public class ShowAndExitException extends Exception {


    public ShowAndExitException(String message) {
        super(message);
    }

    public ShowAndExitException(String message, Throwable cause) {
        super(message, cause);
    }

    public void printMessage() {
        System.out.println(getMessage());
    }

    ;
}
