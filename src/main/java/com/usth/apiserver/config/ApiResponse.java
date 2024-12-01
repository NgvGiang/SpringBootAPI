package com.usth.apiserver.config;

import org.springframework.http.HttpStatus;

import java.io.Serializable;



public class ApiResponse implements UseCase.OutputValue, Serializable {
    private String result;
    private Object content;
    private String message;
    private String jwt;
    private String refreshToken;
    private HttpStatus status;

    public ApiResponse() {
    }

    public ApiResponse(String result, Object content, String message, String jwt, String refreshToken, HttpStatus status) {
        this.result = result;
        this.content = content;
        this.message = message;
        this.jwt = jwt;
        this.refreshToken = refreshToken;
        this.status = status;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Object getContent() {
        return content;
    }

    public void setContent(Object content) {
        this.content = content;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getJwt() {
        return jwt;
    }

    public void setJwt(String jwt) {
        this.jwt = jwt;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public void setStatus(HttpStatus status) {
        this.status = status;
    }

    public ApiResponse(String result, Object content, HttpStatus status) {
        this.result = result;
        this.content = content;
        this.status = status;
    }

    public ApiResponse(String result, Object content, String message) {
        this.result = result;
        this.content = content;
        this.message = message;
    }

    public ApiResponse(String result, Object content) {
        this.result = result;
        this.content = content;
    }
}