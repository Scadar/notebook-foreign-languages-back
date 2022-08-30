package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.TokenRefreshException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.UserLoginException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.UserRegistrationException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.CustomUserDetails;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload.*;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.token.RefreshToken;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.security.JwtTokenProvider;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.service.AuthService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = Logger.getLogger(AuthController.class);
    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;

    @Autowired
    public AuthController(
            AuthService authService,
            JwtTokenProvider tokenProvider
    ) {
        this.authService = authService;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("/checkEmailInUse")
    public ResponseEntity<ApiResponse> checkEmailInUse(@RequestParam("email") String email) {
        Boolean emailExists = authService.emailAlreadyExists(email);
        return ResponseEntity.ok(new ApiResponse(true, emailExists.toString()));
    }

    @GetMapping("/checkUsernameInUse")
    public ResponseEntity<ApiResponse> checkUsernameInUse(@RequestParam("username") String username) {
        Boolean usernameExists = authService.usernameAlreadyExists(username);
        return ResponseEntity.ok(new ApiResponse(true, usernameExists.toString()));
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authService.authenticateUser(loginRequest)
                                                   .orElseThrow(() -> new UserLoginException("Couldn't login user [" + loginRequest + "]"));

        CustomUserDetails customUserDetails = (CustomUserDetails) authentication.getPrincipal();
        logger.info("Logged in User returned [API]: " + customUserDetails.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authService.createAndPersistRefreshTokenForDevice(authentication, loginRequest)
                          .map(RefreshToken::getToken)
                          .map(refreshToken -> {
                              String jwtToken = authService.generateToken(customUserDetails);
                              return ResponseEntity.ok(new JwtAuthenticationResponse(
                                      jwtToken,
                                      refreshToken,
                                      tokenProvider.getExpiryDuration()
                              ));
                          })
                          .orElseThrow(() -> new UserLoginException("Couldn't create refresh token for: [" + loginRequest + "]"));
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse> registerUser(@Valid @RequestBody RegistrationRequest registrationRequest) {

        return authService.registerUser(registrationRequest)
                          .map(user -> {
                              logger.info("Registered User returned [API[: " + user);
                              return ResponseEntity.ok(new ApiResponse(
                                      true,
                                      "User registered successfully. Check your email for verification"
                              ));
                          })
                          .orElseThrow(() -> new UserRegistrationException(
                                  registrationRequest.getEmail(),
                                  "Missing user object in database"
                          ));
    }

    @PostMapping("/refresh")
    public ResponseEntity<JwtAuthenticationResponse> refreshJwtToken(@Valid @RequestBody TokenRefreshRequest tokenRefreshRequest) {

        return authService.refreshJwtToken(tokenRefreshRequest)
                          .map(updatedToken -> {
                              String refreshToken = tokenRefreshRequest.getRefreshToken();
                              logger.info("Created new Jwt Auth token: " + updatedToken);
                              return ResponseEntity.ok(new JwtAuthenticationResponse(
                                      updatedToken,
                                      refreshToken,
                                      tokenProvider.getExpiryDuration()
                              ));
                          })
                          .orElseThrow(() -> new TokenRefreshException(
                                  tokenRefreshRequest.getRefreshToken(),
                                  "Unexpected error during token refresh. Please logout and login again."
                          ));
    }
}
