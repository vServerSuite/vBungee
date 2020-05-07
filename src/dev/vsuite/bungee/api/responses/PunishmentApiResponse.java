package dev.vsuite.bungee.api.responses;

import javax.annotation.Nullable;

import dev.vsuite.bungee.api.responses.base.BaseApiResponse;
import dev.vsuite.bungee.models.punishments.PunishmentType;

public class PunishmentApiResponse extends BaseApiResponse {

    /**
     * The result of whether the Punishment was handed out or not
     */
    public boolean success;

    /**
     * The type of punishment that was created
     */
    public PunishmentType type;

    /**
     * Creates a response for when punishments are sent
     *
     * @param statusCode      - The status of the API request
     * @param responseMessage - The message to return from the API request
     * @param succeeded       - Whether the punishment was handed out
     * @param punishmentType  - The type of punishment that was being handed out
     * @param errorMessage    - The error message that was produced
     */
    public PunishmentApiResponse(int statusCode, String responseMessage, boolean succeeded, PunishmentType punishmentType, @Nullable String errorMessage) {
        super(statusCode, responseMessage, errorMessage);

        success = succeeded;
        type = punishmentType;
    }
}