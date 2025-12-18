package org.lukawska.trainsmart.security.auth;

import lombok.RequiredArgsConstructor;
import org.lukawska.trainsmart.sharedpersistence.domain.entities.User;
import org.lukawska.trainsmart.sharedpersistence.domain.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User foundUser = userRepository.findByUsername(username).orElseThrow(
                () -> new UsernameNotFoundException("Username not found: " + username));

        return new org.springframework.security.core.userdetails.User(
                foundUser.getUsername(), foundUser.getPassword(), List.of(foundUser.getRole()));
    }
}
