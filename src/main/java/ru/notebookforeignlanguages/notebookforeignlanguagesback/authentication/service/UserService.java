package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.service;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.annotation.CurrentUser;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.ResourceNotFoundException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.UserLogoutException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.*;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload.LogOutRequest;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload.RegistrationRequest;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository.UserRepository;

import java.util.Optional;

@Service
public class UserService {

    private static final Logger logger = Logger.getLogger(UserService.class);
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final RoleService roleService;
    private final UserDeviceService userDeviceService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public UserService(
            PasswordEncoder passwordEncoder,
            UserRepository userRepository,
            RoleService roleService,
            UserDeviceService userDeviceService,
            RefreshTokenService refreshTokenService
    ) {
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
        this.roleService = roleService;
        this.userDeviceService = userDeviceService;
        this.refreshTokenService = refreshTokenService;
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public Boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public Boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }


    public User createUser(RegistrationRequest registerRequest) {
        User newUser = new User();
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setUsername(registerRequest.getUsername());
        newUser.addRole(getRoleForNewUser());
        newUser.setActive(true);
        newUser.setIsEmailVerified(true);
        return newUser;
    }

    private Role getRoleForNewUser() {
        return roleService.findByRoleName(RoleName.ROLE_USER);
    }

    public void logoutUser(
            @CurrentUser CustomUserDetails currentUser,
            LogOutRequest logOutRequest
    ) {
        String deviceId = logOutRequest.getDeviceInfo().getDeviceId();
        UserDevice userDevice = userDeviceService.findByUserId(currentUser.getId())
                                                 .filter(device -> device.getDeviceId().equals(deviceId))
                                                 .orElseThrow(() -> new UserLogoutException(
                                                         logOutRequest
                                                                 .getDeviceInfo()
                                                                 .getDeviceId(),
                                                         "Invalid device Id supplied. No matching device found for the given user "
                                                 ));

        logger.info("Removing refresh token associated with device [" + userDevice + "]");
        refreshTokenService.deleteById(userDevice.getRefreshToken().getId());
    }

    public Optional<User> updateFirstName(
            CustomUserDetails currentUser,
            String newFirstName
    ) throws ResourceNotFoundException {
        User userFromDb = userRepository
                .findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUser.getEmail()));

        userFromDb.setFirstName(newFirstName);

        return Optional.of(userRepository.save(userFromDb));
    }

    public Optional<User> updateLastName(
            CustomUserDetails currentUser,
            String newLastName
    ) throws ResourceNotFoundException {
        User userFromDb = userRepository
                .findByEmail(currentUser.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", currentUser.getEmail()));

        userFromDb.setLastName(newLastName);

        return Optional.of(userRepository.save(userFromDb));
    }
}
