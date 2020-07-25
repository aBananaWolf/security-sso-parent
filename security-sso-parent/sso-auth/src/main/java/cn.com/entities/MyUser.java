package cn.com.entities;

import lombok.Data;

import java.io.Serializable;

/**
 * @author wyl
 * @create 2020-07-11 16:08
 */
@Data
public class MyUser implements Serializable {
    private static final long serialVersionUID = 3497935890426858541L;

    private String userName;

    private String password;

    private boolean accountNonExpired = true;

    private boolean accountNonLocked= true;

    private boolean credentialsNonExpired= true;

    private boolean enabled= true;
}