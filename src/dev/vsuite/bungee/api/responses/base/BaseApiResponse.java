package dev.vsuite.bungee.api.responses.base;

public class BaseApiResponse {

    /**
     * Status of the API Response (200, 401, 404 etc)
     */
    public int status;

    /**
     * Message to be returned in the API response
     */
    public String message;

    /**
     * The error that has been produced (If applicable)
     */
    public String error;

    /**
     * Generates a basic APi Response without an error message
     *
     * @param statusCode      - The status of the API request
     * @param responseMessage - The message to return from the API request
     */
    public BaseApiResponse(int statusCode, String responseMessage) {
        status = statusCode;
        message = responseMessage;
    }

    /**
     * Generates a basic APi Response with an error message
     *
     * @param statusCode      - The status of the API request
     * @param responseMessage - The message to return from the API request
     * @param errorMessage    - The error message that was produced
     */
    public BaseApiResponse(int statusCode, String responseMessage, String errorMessage) {
        status = statusCode;
        message = responseMessage;
        if (errorMessage != null) {
            error = errorMessage;
        }
    }
}