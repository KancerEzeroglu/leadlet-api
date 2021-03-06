package com.leadlet.web.rest.vm;

import com.leadlet.service.dto.AuthorityDTO;
import com.leadlet.service.dto.UserDTO;

import javax.validation.constraints.Size;

import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * View Model extending the UserDTO, which is meant to be used in the user management UI.
 */
public class ManagedUserVM extends UserDTO {

    public static final int PASSWORD_MIN_LENGTH = 4;

    public static final int PASSWORD_MAX_LENGTH = 100;

    @Size(min = PASSWORD_MIN_LENGTH, max = PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserVM() {
        // Empty constructor needed for Jackson.
    }

    public ManagedUserVM(Long id, String login, String password, String firstName, String lastName,
                         boolean activated, String imageUrl, String langKey,
                         String createdBy, Instant createdDate, String lastModifiedBy, Instant lastModifiedDate,
                         Set<String> authorities, String phone) {

        super(id, login, firstName, lastName, activated, imageUrl, langKey,
            createdBy, createdDate, lastModifiedBy, lastModifiedDate, authorities.stream().map(authority -> (new AuthorityDTO(authority))).collect(Collectors.toSet()), phone);

        this.password = password;
    }


    public String getPassword() {
        return password;
    }

    @Override
    public String toString() {
        return "ManagedUserVM{" +
            "} " + super.toString();
    }
}
