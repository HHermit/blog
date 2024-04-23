package com.aurora.model.dto;

import com.aurora.constant.CommonConstant;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.beans.Transient;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailsDTO implements UserDetails {

    private Integer id;

    private Integer userInfoId;

    private String email;

    private Integer loginType;

    private String username;

    private String password;

    private List<String> roles;

    private String nickname;

    private String avatar;

    private String intro;

    private String website;

    private Integer isSubscribe;

    private String ipAddress;

    private String ipSource;

    private Integer isDisable;


    private String browser;

    private String os;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime expireTime;

    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime lastLoginTime;

    /**
     * 该函数的作用是将一个角色列表（roles）转换为一个具有相应角色的授权集合（Collection<? extends GrantedAuthority>）。
     * 具体实现步骤如下：
     * 使用stream()方法将roles列表转换为一个流；
     * 使用map()方法将每个角色字符串转换为一个SimpleGrantedAuthority对象，SimpleGrantedAuthority是一个实现了GrantedAuthority接口的类，用于表示一个角色；
     * 使用collect()方法将转换后的SimpleGrantedAuthority对象收集到一个Set集合中，以去除可能的重复角色；
     * 返回转换后的Set集合，该集合包含了输入角色列表中的所有角色，每个角色都以SimpleGrantedAuthority对象的形式表示。
     * 该函数通常用于将用户   角色列表  转换为安全框架所需的授权列表，以便进行权限控制。
     * @return 对应用户的角色集合
     */
    @Override
    @Transient
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isAccountNonLocked() {
        return this.isDisable.equals(CommonConstant.FALSE);
    }

    @Override
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @Transient
    public boolean isEnabled() {
        return true;
    }
}
