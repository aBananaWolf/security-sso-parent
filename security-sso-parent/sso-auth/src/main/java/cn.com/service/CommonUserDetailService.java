package cn.com.service;

import cn.com.entities.MyUser;
import org.apache.catalina.security.SecurityUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wyl
 * @create 2020-07-11 16:54
 */
@Service
public class CommonUserDetailService implements UserDetailsService {
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    /**
     * @param username 我们可以看到这里只有username， 但是我们的表单是有传递密码的
     *                 表单密码会被spring security 自动处理
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser myUser = new MyUser();
        myUser.setUserName(username);
        myUser.setPassword(passwordEncoder.encode(username));
        List<GrantedAuthority> authority;

        if ("admin".equalsIgnoreCase(username))
            authority = AuthorityUtils.commaSeparatedStringToAuthorityList("admin");
        else
            authority = AuthorityUtils.commaSeparatedStringToAuthorityList("test");

        return new User(myUser.getUserName(),myUser.getPassword(),myUser.isEnabled(),
                myUser.isAccountNonExpired(),myUser.isCredentialsNonExpired(), myUser.isAccountNonLocked(),
                authority);
    }
}
