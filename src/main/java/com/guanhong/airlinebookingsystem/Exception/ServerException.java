package com.guanhong.airlinebookingsystem.Exception;

import org.springframework.http.HttpStatus;

public class ServerException extends Exception{
    private String message;
    private HttpStatus httpStatus;

    public ServerException(String message, HttpStatus httpStatus){
        super();
        this.message = message;
        this.httpStatus = httpStatus;
    }

    public String getMessage(){
        return "HttpStauts: " + this.httpStatus + " , Message: " + this.message;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
