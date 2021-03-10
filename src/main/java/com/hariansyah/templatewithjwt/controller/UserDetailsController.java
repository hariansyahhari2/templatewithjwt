package com.hariansyah.templatewithjwt.controller;

import com.hariansyah.templatewithjwt.configs.jwt.JwtToken;
import com.hariansyah.templatewithjwt.entities.User;
import com.hariansyah.templatewithjwt.entities.UserDetails;
import com.hariansyah.templatewithjwt.exceptions.InvalidPermissionsException;
import com.hariansyah.templatewithjwt.models.ResponseMessage;
import com.hariansyah.templatewithjwt.models.entitymodels.request.UserDetailsRequest;
import com.hariansyah.templatewithjwt.models.entitymodels.response.UserDetailsResponse;
import com.hariansyah.templatewithjwt.repositories.UserRepository;
import com.hariansyah.templatewithjwt.services.UserDetailsService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

@RequestMapping("/user-details")
@RestController
public class UserDetailsController {

    @Autowired
    private UserDetailsService service;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private UserRepository accountRepository;

    @Autowired
    private JwtToken jwtTokenUtil;

    @GetMapping("/{id}")
    public ResponseMessage<UserDetailsResponse> findById(
            @PathVariable String id, HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");

        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            User account = accountRepository.findByUsername(username);
            UserDetails entity = service.findById(id);
            if(entity != null) {
                if (entity.getUser().getId().equals(account.getId())) {
                    UserDetailsResponse data = modelMapper.map(entity, UserDetailsResponse.class);
                    return ResponseMessage.success(data);
                }
            }
        }
        throw new InvalidPermissionsException();
    }

    @PostMapping("/add")
    public ResponseMessage<UserDetailsResponse> add(
            @RequestBody @Valid UserDetailsRequest model,
            HttpServletRequest request
    ) {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            User account = accountRepository.findByUsername(username);

            UserDetails entity = modelMapper.map(model, UserDetails.class);
            entity.setUser(account);
            entity.setDeleted(false);
            entity = service.save(entity);

            UserDetailsResponse data = modelMapper.map(entity, UserDetailsResponse.class);
            return ResponseMessage.success(data);
        }
        throw new InvalidPermissionsException();
    }

    @PutMapping("{id}")
    public ResponseMessage<UserDetailsResponse> edit(
            @PathVariable String id,
            @RequestBody @Valid UserDetailsRequest model,
            HttpServletRequest request
    ) {
        UserDetails entity = service.findById(id);

        String token = request.getHeader("Authorization");
        if (entity != null && token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
            String username = jwtTokenUtil.getUsernameFromToken(token);
            User account = accountRepository.findByUsername(username);
            if (entity.getUser().getId().equals(account.getId())) {
                modelMapper.map(model, entity);
                entity = service.save(entity);

                UserDetailsResponse data = modelMapper.map(entity, UserDetailsResponse.class);
                return ResponseMessage.success(data);
            }
        }
        throw new InvalidPermissionsException();
    }
}