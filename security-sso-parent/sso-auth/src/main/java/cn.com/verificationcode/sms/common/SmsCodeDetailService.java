package cn.com.verificationcode.sms.common;

import cn.com.entities.MyUser;
import cn.com.verificationcode.sms.raw.SmsCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author wyl
 * @create 2020-07-18 19:31
 */
@Component
public class SmsCodeDetailService implements UserDetailsService {
    @Autowired
    private SmsCodeService smsCodeService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        MyUser myUser = new MyUser();
        SmsCode smsCode = smsCodeService.get(username);
        if (smsCode == null)
            return null;

        List<GrantedAuthority> authority;

        if ("admin".equalsIgnoreCase(username))
            authority = AuthorityUtils.commaSeparatedStringToAuthorityList("admin");
        else
            authority = AuthorityUtils.commaSeparatedStringToAuthorityList("test");

        return new User(username,"",myUser.isEnabled(),
                myUser.isAccountNonExpired(),myUser.isCredentialsNonExpired(), myUser.isAccountNonLocked(),
                authority);
    }
}
