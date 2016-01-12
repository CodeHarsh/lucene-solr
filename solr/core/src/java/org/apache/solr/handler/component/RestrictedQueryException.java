package org.apache.solr.handler.component;

/**
 * Created by harshvardhan.s on 23/11/15.
 */
public class RestrictedQueryException extends Exception {

    public RestrictedQueryException(String message) {
        super(message);
    }

    public RestrictedQueryException(Exception exception) {
        super(exception);
    }

    public RestrictedQueryException(){
        super();
    }
}
