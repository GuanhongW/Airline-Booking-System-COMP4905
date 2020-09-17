package com.guanhong.airlinebookingsystem.Exception;

import org.springframework.http.HttpStatus;
public class ClientException extends Exception {

    private HttpStatus httpStatus;

    public ClientException(String message, HttpStatus httpStatus){
        super(message);
        this.httpStatus = httpStatus;
    }

    public String getMessageWHttpStatus(){
        return "HttpStauts: " + this.httpStatus + " , Message: " + this.getMessage();
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }

    public ClientException(String message) {
        super(message);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }
}
