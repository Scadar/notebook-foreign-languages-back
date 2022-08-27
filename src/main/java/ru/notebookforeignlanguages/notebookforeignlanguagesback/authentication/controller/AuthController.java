package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.models.AppUser;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.models.ERole;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.models.RefreshToken;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.payload.request.LogOutRequest;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.payload.request.LoginRequest;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.payload.request.SignupRequest;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.payload.request.TokenRefreshRequest;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.payload.response.JwtResponse;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.payload.response.MessageResponse;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.payload.response.TokenRefreshResponse;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository.AppRoleRepository;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository.AppUserRepository;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.security.jwt.JwtUtils;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.security.service.RefreshTokenService;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.security.service.UserDetailsImpl;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final AppUserRepository userRepository;
    private final AppRoleRepository roleRepository;
    private final PasswordEncoder encoder;
    private final JwtUtils jwtUtils;
    private final RefreshTokenService refreshTokenService;

    public AuthController(
            AuthenticationManager authenticationManager,
            AppUserRepository userRepository,
            AppRoleRepository roleRepository,
            PasswordEncoder encoder,
            JwtUtils jwtUtils,
            RefreshTokenService refreshTokenService
    ) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.encoder = encoder;
        this.jwtUtils = jwtUtils;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/signin")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {

        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        String jwt = jwtUtils.generateJwtToken(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(
                Collectors.toList());

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());

        return ResponseEntity.ok(new JwtResponse(
                jwt,
                refreshToken.getToken(),
                userDetails.getId(),
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        ));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> registerUser(@RequestBody SignupRequest signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())) {
            return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
        }

        // Create new user's account
        AppUser user = new AppUser();
        user.setUsername(signUpRequest.getUsername());
        user.setEmail(signUpRequest.getEmail());
        user.setPassword(encoder.encode(signUpRequest.getPassword()));
        user.setRoles(
                Set.of(roleRepository
                               .findByName(ERole.ROLE_USER)
                               .orElseThrow(() -> new RuntimeException("Error: Role is not found."))
                )
        );

        userRepository.save(user);

        return ResponseEntity.ok(new MessageResponse("User registered successfully!"));
    }

    @PostMapping("/refreshtoken")
    public ResponseEntity<?> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();

        return refreshTokenService.findByToken(requestRefreshToken).map(refreshTokenService::verifyExpiration).map(
                RefreshToken::getUser).map(user -> {
            String token = jwtUtils.generateTokenFromUsername(user.getUsername());
            return ResponseEntity.ok(new TokenRefreshResponse(token, requestRefreshToken));
        }).orElseThrow(() -> new AccessDeniedException("Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser(@Valid @RequestBody LogOutRequest logOutRequest) {
        refreshTokenService.deleteByUserId(logOutRequest.getUserId());
        return ResponseEntity.ok(new MessageResponse("Log out successful!"));
    }
}
