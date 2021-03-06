package com.hariansyah.templatewithjwt.controller;

import com.hariansyah.templatewithjwt.configs.jwt.JwtToken;
import com.hariansyah.templatewithjwt.entities.User;
import com.hariansyah.templatewithjwt.entities.UserDetails;
import com.hariansyah.templatewithjwt.exceptions.EntityNotFoundException;
import com.hariansyah.templatewithjwt.exceptions.InvalidPermissionsException;
import com.hariansyah.templatewithjwt.models.ResponseMessage;
import com.hariansyah.templatewithjwt.models.entitymodels.request.UserRequest;
import com.hariansyah.templatewithjwt.models.entitymodels.request.UserWithUserDetailsRequest;
import com.hariansyah.templatewithjwt.models.entitymodels.response.UserDetailsResponse;
import com.hariansyah.templatewithjwt.models.entitymodels.response.UserResponse;
import com.hariansyah.templatewithjwt.repositories.UserRepository;
import com.hariansyah.templatewithjwt.services.UserDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;

import static com.hariansyah.templatewithjwt.enums.RoleEnum.*;


@RequestMapping("/user")
@RestController
public class UserController {

    @Autowired
    private UserRepository repository;

    @Autowired
    private UserDetailsService service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtToken jwtTokenUtil;

    @PostMapping("/register")
    public ResponseMessage<UserDetailsResponse> addWithUser(
            @RequestBody @Valid UserWithUserDetailsRequest model
    ) {
        if (repository.existsByUsername(model.getUser().getUsername())) {
            throw new ValidationException("Username already existed");
        }

        UserDetails userDetails = modelMapper.map(model.getUserDetails(), UserDetails.class);
        userDetails = service.save(userDetails);

        User user = new User();
        user.setUsername(model.getUser().getUsername());
        user.setUserDetails(userDetails);
        user.setEmail(model.getUser().getEmail());
        String hashPassword = new BCryptPasswordEncoder().encode(model.getUser().getPassword());
        user.setPassword(hashPassword);
        user.setRole(CUSTOMER);

        repository.save(user);

        UserDetailsResponse data = modelMapper.map(userDetails, UserDetailsResponse.class);
        return ResponseMessage.success(data);
    }

    @GetMapping("/{username}/make-customer")
    public ResponseMessage<UserResponse> makeCustomer(
            @PathVariable String username,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            User account = repository.findByUsername(username);
            token = token.substring(7);
            String loggedInUsername = jwtTokenUtil.getUsernameFromToken(token);
            User loggedInUser = repository.findByUsername(loggedInUsername);

            if (loggedInUser.getUsername().equals(account.getUsername()) || loggedInUser.getRole().equals(CUSTOMER))
                throw new InvalidPermissionsException();

            account.setRole(CUSTOMER);
            repository.save(account);

            UserResponse data = modelMapper.map(account, UserResponse.class);
            return ResponseMessage.success(data);
        }
        throw new InvalidPermissionsException();
    }

    @GetMapping("/{username}/make-courier")
    public ResponseMessage<UserResponse> makeCourier(
            @PathVariable String username,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            User account = repository.findByUsername(username);
            token = token.substring(7);
            String loggedInUsername = jwtTokenUtil.getUsernameFromToken(token);
            User loggedInUser = repository.findByUsername(loggedInUsername);

            if (loggedInUser.getUsername().equals(account.getUsername()) || loggedInUser.getRole().equals(CUSTOMER) || loggedInUser.getRole().equals(COURIER))
                throw new InvalidPermissionsException();

            account.setRole(COURIER);
            repository.save(account);

            UserResponse data = modelMapper.map(account, UserResponse.class);
            return ResponseMessage.success(data);
        }
        throw new InvalidPermissionsException();
    }

    @GetMapping("/{username}/make-admin")
    public ResponseMessage<UserResponse> makeAdmin(
            @PathVariable String username,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            User account = repository.findByUsername(username);
            token = token.substring(7);
            String loggedInUsername = jwtTokenUtil.getUsernameFromToken(token);
            User loggedInUser = repository.findByUsername(loggedInUsername);

            if (!loggedInUser.getRole().equals(ADMIN)) {
                throw new InvalidPermissionsException();
            }
            account.setRole(ADMIN);
            repository.save(account);

            UserResponse data = modelMapper.map(account, UserResponse.class);
            return ResponseMessage.success(data);
        }
        throw new InvalidPermissionsException();
    }

    @PutMapping("/edit-account")
    public Boolean edit(@RequestBody UserRequest model) {
        User account = repository.findByUsername(model.getUsername());
        String password = model.getPassword();
        String encodedPassword = new BCryptPasswordEncoder().encode(password);

        account.setPassword(encodedPassword);
        repository.save(account);
        return true;
    }

    @GetMapping("/me")
    public ResponseMessage<UserResponse> findById(
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);

            String username = jwtTokenUtil.getUsernameFromToken(token);

            User account = repository.findByUsername(username);

            if(account != null) {
                UserResponse data = modelMapper.map(account, UserResponse.class);
                return ResponseMessage.success(data);
            }
        }
        throw new EntityNotFoundException();
    }
}
