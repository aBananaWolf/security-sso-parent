package cn.com.el;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author wyl
 * @create 2020-07-14 18:14
 */
@Component("X")
public class ElPermissionConfig {

    /**
     * @param permitAuthority 被允许的授权信息
     */
    public boolean hasAuthority(String... permitAuthority) {
        // 待授权的信息
        List<String> authorities =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getAuthorities()
                        .stream()
                        .map(authoritiesDetail -> authoritiesDetail.getAuthority())
                        .collect(toList());

        // 放行超管
        return authorities.contains("admin") ||
                // 对两个集合的元素进行动态匹配
                Arrays.stream(permitAuthority).anyMatch(permitAuthorityDetail ->
                        authorities.stream().anyMatch(authority -> authority.equals(permitAuthorityDetail))
                );
    }
}
