package dev.vsuite.bungee.api.responses.base;

public class BaseApiResponse {

    public int status;

    public String message;

    public String error;

    public BaseApiResponse(int statusCode, String responseMessage) {
        status = statusCode;
        message = responseMessage;
    }

    public BaseApiResponse(int statusCode, String responseMessage, String errorMessage) {
        status = statusCode;
        message = responseMessage;
        if (errorMessage != null) {
            error = errorMessage;
        }
    }
}