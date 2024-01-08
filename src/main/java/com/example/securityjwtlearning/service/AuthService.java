package com.example.securityjwtlearning.service;

import com.example.securityjwtlearning.dto.JwtRequest;
import com.example.securityjwtlearning.dto.JwtResponse;
import com.example.securityjwtlearning.dto.RegistrationUserDto;
import com.example.securityjwtlearning.exception.AppErrorDto;
import com.example.securityjwtlearning.util.JwtTokenUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;

    private final JwtTokenUtils jwtTokenUtils;

    private final AuthenticationManager authenticationManager;

    public ResponseEntity<?> createAuthToken(@RequestBody JwtRequest jwtRequest) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    jwtRequest.getUsername(), jwtRequest.getPassword()));
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(new AppErrorDto(
                    HttpStatus.UNAUTHORIZED.value(), "Wrong login or password"), HttpStatus.UNAUTHORIZED);
        }
        UserDetails userDetails = userService.loadUserByUsername(jwtRequest.getUsername());
        String token = jwtTokenUtils.generateToken(userDetails);
        return ResponseEntity.ok(new JwtResponse(token));
    }

    public ResponseEntity<?> register(@RequestBody RegistrationUserDto userDto) {
        if (!userDto.getPassword().equals(userDto.getConfirmPassword())) {
            return new ResponseEntity<>(new AppErrorDto(
                    HttpStatus.BAD_REQUEST.value(), "passwords don't match"), HttpStatus.BAD_REQUEST);
        }
        if (userService.isExistsUser(userDto.getUsername())) {
            return new ResponseEntity<>(new AppErrorDto(
                    HttpStatus.BAD_REQUEST.value(), String.format("user with name: %s already exist", userDto.getUsername())
            ), HttpStatus.BAD_REQUEST);
        }
        userService.createNewUser(userDto);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
