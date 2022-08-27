package ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.models.AppUser;
import ru.notebookforeignlanguages.notebookforeignlanguagesback.authentication.repository.AppUserRepository;

import javax.transaction.Transactional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final AppUserRepository userRepository;

    public UserDetailsServiceImpl(AppUserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username)
                                     .orElseThrow(() -> new UsernameNotFoundException("User Not Found with username: " + username));

        return UserDetailsImpl.build(user);
    }
}
