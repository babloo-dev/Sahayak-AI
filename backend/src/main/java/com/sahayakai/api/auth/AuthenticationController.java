package com.sahayakai.api.auth;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    @GetMapping("/me")
    public AuthenticatedUserResponse currentUser(
            @RequestAttribute(ApiAuthenticationFilter.USER_ATTRIBUTE) AuthenticatedUser user) {
        return new AuthenticatedUserResponse(user.role().name());
    }
}
