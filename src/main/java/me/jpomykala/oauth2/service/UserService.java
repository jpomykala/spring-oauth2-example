package me.jpomykala.oauth2.service;

import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.oauth2.provider.ClientDetailsService;

/**
 * Created by Evelan on 16/12/2016.
 */
public interface UserService extends ClientDetailsService, UserDetailsService {
}
