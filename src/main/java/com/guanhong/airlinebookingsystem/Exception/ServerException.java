package com.guanhong.airlinebookingsystem.Exception;

import org.springframework.http.HttpStatus;

public class ServerException extends Exception{

    private HttpStatus httpStatus;

    public ServerException(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus = httpStatus;
    }

    public String getMessageWHttpStatus(){
        return "HttpStauts: " + this.httpStatus + " , Message: " + this.getMessage();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
