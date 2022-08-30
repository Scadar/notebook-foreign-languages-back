package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.exception.TokenRefreshException;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.UserDevice;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.payload.DeviceInfo;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.model.token.RefreshToken;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository.UserDeviceRepository;

import java.util.Optional;

@Service
public class UserDeviceService {

    private final UserDeviceRepository userDeviceRepository;

    @Autowired
    public UserDeviceService(UserDeviceRepository userDeviceRepository) {
        this.userDeviceRepository = userDeviceRepository;
    }

    public Optional<UserDevice> findByUserId(Long userId) {
        return userDeviceRepository.findByUserId(userId);
    }

    public Optional<UserDevice> findByRefreshToken(RefreshToken refreshToken) {
        return userDeviceRepository.findByRefreshToken(refreshToken);
    }

    public UserDevice createUserDevice(DeviceInfo deviceInfo) {
        UserDevice userDevice = new UserDevice();
        userDevice.setDeviceId(deviceInfo.getDeviceId());
        userDevice.setDeviceType(deviceInfo.getDeviceType());
        userDevice.setNotificationToken(deviceInfo.getNotificationToken());
        userDevice.setRefreshActive(true);
        return userDevice;
    }

    void verifyRefreshAvailability(RefreshToken refreshToken) {
        UserDevice userDevice = findByRefreshToken(refreshToken)
                .orElseThrow(() -> new TokenRefreshException(
                        refreshToken.getToken(),
                        "No device found for the matching token. Please login again"
                ));

        if (!userDevice.getRefreshActive()) {
            throw new TokenRefreshException(
                    refreshToken.getToken(),
                    "Refresh blocked for the device. Please login through a different device"
            );
        }
    }
}
