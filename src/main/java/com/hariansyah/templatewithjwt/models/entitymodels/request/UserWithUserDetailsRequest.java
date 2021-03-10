package com.hariansyah.templatewithjwt.models.entitymodels.request;

import javax.validation.constraints.NotNull;

public class UserWithUserDetailsRequest {

    @NotNull
    private UserRequest user;

    @NotNull
    private UserDetailsRequest userDetails;

    public UserRequest getUser() {
        return user;
    }

    public void setUser(UserRequest user) {
        this.user = user;
    }

    public UserDetailsRequest getUserDetails() {
        return userDetails;
    }

    public void setUserDetails(UserDetailsRequest userDetails) {
        this.userDetails = userDetails;
    }
}
