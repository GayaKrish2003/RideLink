package com.ridelink.accountservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

// Only name is updatable for now — email changes are deliberately excluded
// here, since changing email often needs extra verification in real systems
@Getter
@Setter
public class UpdateProfileRequest {

    @NotBlank(message = "Name is required")
    private String name;
}