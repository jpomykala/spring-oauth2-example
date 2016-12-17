package me.jpomykala.oauth2.service;

import me.jpomykala.oauth2.model.user.QUser;
import me.jpomykala.oauth2.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;

/**
 * Created by Evelan on 16/12/2016.
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public ClientDetails loadClientByClientId(String s) throws ClientRegistrationException {
        return userRepository.findOne(QUser.user.clientId.eq(s));
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findOne(QUser.user.email.eq(s));
    }
}
