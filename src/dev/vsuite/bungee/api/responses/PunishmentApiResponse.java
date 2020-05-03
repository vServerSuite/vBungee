package codes.benh.velocitymc.api.responses;

import javax.annotation.Nullable;

import codes.benh.velocitymc.api.responses.base.BaseApiResponse;
import codes.benh.velocitymc.models.punishments.PunishmentType;

public class PunishmentApiResponse extends BaseApiResponse {

    public boolean success;

    public PunishmentType type;

    public PunishmentApiResponse(int statusCode, String responseMessage, boolean succeeded, PunishmentType punishmentType, @Nullable String errorMessage) {
        super(statusCode, responseMessage, errorMessage);

        success = succeeded;
        type = punishmentType;
    }
}