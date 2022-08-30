package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.annotation.CurrentUser;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.ResourceNotFoundException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.UpdatePasswordException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.CustomUserDetails;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.User;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload.*;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.service.AuthService;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.service.UserService;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private static final Logger logger = Logger.getLogger(UserController.class);

    private final AuthService authService;

    private final UserService userService;


    @Autowired
    public UserController(
            AuthService authService,
            UserService userService
    ) {
        this.authService = authService;
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<MeResponse> getUserProfile(@CurrentUser CustomUserDetails customUserDetails) {
        return ResponseEntity.ok(
                MeResponse
                        .builder()
                        .id(customUserDetails.getId())
                        .email(customUserDetails.getEmail())
                        .active(customUserDetails.getActive())
                        .username(customUserDetails.getUsername())
                        .roles(customUserDetails.getRoles())
                        .firstName(customUserDetails.getFirstName())
                        .lastName(customUserDetails.getLastName())
                        .build()
        );
    }

    @GetMapping("/admins")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllAdmins() {
        logger.info("Inside secured resource with admin");
        return ResponseEntity.ok("Hello. This is about admins");
    }

    @PostMapping("/password/update")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponse> updateUserPassword(
            @CurrentUser CustomUserDetails customUserDetails,
            @Valid @RequestBody UpdatePasswordRequest updatePasswordRequest
    ) {

        return authService.updatePassword(customUserDetails, updatePasswordRequest)
                          .map(updatedUser -> ResponseEntity.ok(new ApiResponse(true, "Password changed successfully")))
                          .orElseThrow(() -> new UpdatePasswordException("--Empty--", "No such user present."));
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logoutUser(
            @CurrentUser CustomUserDetails customUserDetails,
            @Valid @RequestBody LogOutRequest logOutRequest
    ) {
        userService.logoutUser(customUserDetails, logOutRequest);
        return ResponseEntity.ok(new ApiResponse(true, "Log out successful"));
    }

    @PutMapping("/first-name")
    public ResponseEntity<User> updateFirstName(
            @CurrentUser CustomUserDetails customUserDetails,
            @RequestBody UpdateFirstNameRequest updateFirstNameRequest
    ) throws ResourceNotFoundException {
        return userService
                .updateFirstName(customUserDetails, updateFirstNameRequest.getFirstName())
                .map(user -> {
                    logger.info("Update firstName with user id=" + user.getId() + " is successful");
                    return ResponseEntity.ok(user);
                })
                .orElseThrow(() -> new RuntimeException("Update firstName exception"));
    }

    @PutMapping("/last-name")
    public ResponseEntity<User> updateLastName(
            @CurrentUser CustomUserDetails customUserDetails,
            @RequestBody UpdateLastNameRequest updateLastNameRequest
    ) throws ResourceNotFoundException {
        return userService
                .updateLastName(customUserDetails, updateLastNameRequest.getLastName())
                .map(user -> {
                    logger.info("Update lastName with user id=" + user.getId() + " is successful");
                    return ResponseEntity.ok(user);
                })
                .orElseThrow(() -> new RuntimeException("Update lastName exception"));
    }
}
