package com.tranquangduy.firebasewithspring.authority;

import com.tranquangduy.firebasewithspring.model.User;
import com.tranquangduy.firebasewithspring.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Service
public class MyUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(MyUserDetailsService.class);
    @Autowired
    UserService userService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            User user = userService.getUser(username);
            if(user != null){
                return new MyUserDetail(user);
            }else {
                log.info("User not found with username: " + username);
                throw new UsernameNotFoundException("User not found with username: " + username);
            }
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        throw new UsernameNotFoundException("User not found with username: " + username);
    }
}
