package com.yian.jwt_authflow.dtos;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.Schema.RequiredMode;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonNaming(value = PropertyNamingStrategy.UpperCamelCaseStrategy.class)
@Schema(title = "UserCreateRequest", accessMode = Schema.AccessMode.WRITE_ONLY)
public class UserCreateRequestDTO {
    @NotBlank(message = "first-name must not be empty")
    @Schema(requiredMode = RequiredMode.REQUIRED, description = "first-name of user", example = "Yian")

    private String firstName;

    @Schema(requiredMode = RequiredMode.NOT_REQUIRED, description = "last-name of user", example = "Choi")
    private String lastName;

    @NotBlank(message = "emailId must not be empty")
    @Email(message = "email must be in a valid format")
    @Schema(requiredMode = RequiredMode.REQUIRED, description = "emailId of user", example = "yian.choi05@gmail.com")
    private String emailId;

    @NotBlank(message = "password must not be empty")
    @Schema(requiredMode = RequiredMode.REQUIRED, description = "secure password to enable user login", example = "Passw0rd123!")
    private String password;
}
