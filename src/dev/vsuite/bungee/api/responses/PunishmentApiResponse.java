package dev.vsuite.bungee.api.responses;

import javax.annotation.Nullable;

import dev.vsuite.bungee.api.responses.base.BaseApiResponse;
import dev.vsuite.bungee.models.punishments.PunishmentType;

public class PunishmentApiResponse extends BaseApiResponse {

    public boolean success;

    public PunishmentType type;

    public PunishmentApiResponse(int statusCode, String responseMessage, boolean succeeded, PunishmentType punishmentType, @Nullable String errorMessage) {
        super(statusCode, responseMessage, errorMessage);

        success = succeeded;
        type = punishmentType;
    }
}