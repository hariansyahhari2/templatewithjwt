package com.hariansyah.templatewithjwt.controller.auth;

import com.hariansyah.templatewithjwt.configs.jwt.JwtToken;
import com.hariansyah.templatewithjwt.exceptions.InvalidCredentialsException;
import com.hariansyah.templatewithjwt.models.ResponseMessage;
import com.hariansyah.templatewithjwt.models.jwt.JwtRequest;
import com.hariansyah.templatewithjwt.models.jwt.JwtResponse;
import com.hariansyah.templatewithjwt.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin
public class AuthController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtToken jwtToken;

    @Autowired
    private UserService service;


    @RequestMapping(value = "/authenticate", method = RequestMethod.POST)
    public ResponseMessage<JwtResponse> createAuthenticationToken(@RequestBody JwtRequest authenticationRequest) throws Exception {

        authenticate(authenticationRequest.getUsername(), authenticationRequest.getPassword());

        final UserDetails userDetails = service
                .loadUserByUsername(authenticationRequest.getUsername());

        final String token = jwtToken.generateToken(userDetails);

        return ResponseMessage.success(new JwtResponse(token));

    }

    private void authenticate(String username, String password) throws Exception {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(username, password));
        } catch (DisabledException | BadCredentialsException e) {
            throw new InvalidCredentialsException();
        }
    }
}