
package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.ResourceAlreadyInUseException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.TokenRefreshException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.UpdatePasswordException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.CustomUserDetails;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.User;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.UserDevice;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload.LoginRequest;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload.RegistrationRequest;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload.TokenRefreshRequest;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload.UpdatePasswordRequest;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.token.RefreshToken;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.security.JwtTokenProvider;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = Logger.getLogger(AuthService.class);
    private final UserService userService;
    private final JwtTokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDeviceService userDeviceService;

    @Autowired
    public AuthService(
            UserService userService,
            JwtTokenProvider tokenProvider,
            RefreshTokenService refreshTokenService,
            PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager,
            UserDeviceService userDeviceService
    ) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
        this.refreshTokenService = refreshTokenService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.userDeviceService = userDeviceService;
    }

    public Optional<User> registerUser(RegistrationRequest newRegistrationRequest) {
        String newRegistrationRequestEmail = newRegistrationRequest.getEmail();
        if (emailAlreadyExists(newRegistrationRequestEmail)) {
            logger.error("Email already exists: " + newRegistrationRequestEmail);
            throw new ResourceAlreadyInUseException("Email", "Address", newRegistrationRequestEmail);
        }
        logger.info("Trying to register new user [" + newRegistrationRequestEmail + "]");
        User newUser = userService.createUser(newRegistrationRequest);
        User registeredNewUser = userService.save(newUser);
        return Optional.ofNullable(registeredNewUser);
    }

    public Boolean emailAlreadyExists(String email) {
        return userService.existsByEmail(email);
    }

    public Boolean usernameAlreadyExists(String username) {
        return userService.existsByUsername(username);
    }

    public Optional<Authentication> authenticateUser(LoginRequest loginRequest) {
        return Optional.ofNullable(authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        )));
    }

    private Boolean currentPasswordMatches(
            User currentUser,
            String password
    ) {
        return passwordEncoder.matches(password, currentUser.getPassword());
    }

    public Optional<User> updatePassword(
            CustomUserDetails customUserDetails,
            UpdatePasswordRequest updatePasswordRequest
    ) {
        String email = customUserDetails.getEmail();
        User currentUser = userService.findByEmail(email)
                                      .orElseThrow(() -> new UpdatePasswordException(email, "No matching user found"));

        if (!currentPasswordMatches(currentUser, updatePasswordRequest.getOldPassword())) {
            logger.info("Current password is invalid for [" + currentUser.getPassword() + "]");
            throw new UpdatePasswordException(currentUser.getEmail(), "Invalid current password");
        }
        String newPassword = passwordEncoder.encode(updatePasswordRequest.getNewPassword());
        currentUser.setPassword(newPassword);
        userService.save(currentUser);
        return Optional.of(currentUser);
    }

    public String generateToken(CustomUserDetails customUserDetails) {
        return tokenProvider.generateToken(customUserDetails);
    }

    public Optional<RefreshToken> createAndPersistRefreshTokenForDevice(
            Authentication authentication,
            LoginRequest loginRequest
    ) {
        User currentUser = (User) authentication.getPrincipal();
        userDeviceService.findByUserId(currentUser.getId())
                         .map(UserDevice::getRefreshToken)
                         .map(RefreshToken::getId)
                         .ifPresent(refreshTokenService::deleteById);

        UserDevice userDevice = userDeviceService.createUserDevice(loginRequest.getDeviceInfo());
        RefreshToken refreshToken = refreshTokenService.createRefreshToken();
        userDevice.setUser(currentUser);
        userDevice.setRefreshToken(refreshToken);
        refreshToken.setUserDevice(userDevice);
        refreshToken = refreshTokenService.save(refreshToken);
        return Optional.ofNullable(refreshToken);
    }

    public Optional<String> refreshJwtToken(TokenRefreshRequest tokenRefreshRequest) {
        String requestRefreshToken = tokenRefreshRequest.getRefreshToken();

        return Optional.of(refreshTokenService.findByToken(requestRefreshToken)
                                              .map(refreshToken -> {
                                                  refreshTokenService.verifyExpiration(refreshToken);
                                                  userDeviceService.verifyRefreshAvailability(refreshToken);
                                                  refreshTokenService.increaseCount(refreshToken);
                                                  return refreshToken;
                                              })
                                              .map(RefreshToken::getUserDevice)
                                              .map(UserDevice::getUser)
                                              .map(CustomUserDetails::new)
                                              .map(this::generateToken))
                       .orElseThrow(() -> new TokenRefreshException(
                               requestRefreshToken,
                               "Missing refresh token in database.Please login again"
                       ));
    }
}
