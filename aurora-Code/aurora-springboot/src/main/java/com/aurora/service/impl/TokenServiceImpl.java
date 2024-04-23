package com.aurora.service.impl;

import com.aurora.model.dto.UserDetailsDTO;
import com.aurora.service.RedisService;
import com.aurora.service.TokenService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;

import static com.aurora.constant.AuthConstant.*;
import static com.aurora.constant.RedisConstant.LOGIN_USER;


@Service
public class TokenServiceImpl implements TokenService {

    /**
     * 加载jwt.secret配置项的值，作为加密签名的密钥。
     */
    @Value("${jwt.secret}")
    private String secret;

    /**
     * 注入Redis服务，用于存储和读取用户登录信息。
     */
    @Autowired
    private RedisService redisService;

    /**
     * 根据用户详情创建并刷新令牌。
     *
     * @param userDetailsDTO 用户详情数据传输对象
     * @return 新生成的令牌字符串
     */
    @Override
    public String createToken(UserDetailsDTO userDetailsDTO) {
        // 刷新用户登录信息
        refreshToken(userDetailsDTO);
        String userId = userDetailsDTO.getId().toString();
        // 创建带有指定用户ID的令牌
        return createToken(userId);
    }

    /**
     * 根据给定的主题创建JWT令牌。
     *
     * @param subject 令牌的主题，通常是用户ID
     * @return 创建好的JWT令牌字符串
     */
    @Override
    public String createToken(String subject) {
        //设置JWT中签名部分的加密算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
        // 生成基于配置密钥的AES密钥
        SecretKey secretKey = generalKey();
        return Jwts.builder()
                // 设置唯一标识符
                .setId(getUuid())
                //设置面向的用户主体，内容是当前登录的用户id
                .setSubject(subject)
                // 设置签发者
                .setIssuer("aha14")
                // 使用密钥进行签名
                .signWith(signatureAlgorithm, secretKey)
                //将构建好的令牌以紧凑格式返回
                .compact();
    }

    /**
     * 更新用户登录信息并设置新的过期时间。
     * 防止用户一直在线 但是时间超过过期时间从而导致用户没有登出，反而需要重新登录
     * @param userDetailsDTO 用户详情数据传输对象
     */
    @Override
    public void refreshToken(UserDetailsDTO userDetailsDTO) {
        LocalDateTime currentTime = LocalDateTime.now();
        // 更新过期时间
        userDetailsDTO.setExpireTime(currentTime.plusSeconds(EXPIRE_TIME));
        String userId = userDetailsDTO.getId().toString();
        // 将更新后的信息存入Redis，并设置过期时间
        redisService.hSet(LOGIN_USER, userId, userDetailsDTO, EXPIRE_TIME);
    }

    /**
     * 检查并根据需要刷新令牌，确保其有效期在限定时间内。
     * 如果为空的，则设置过期时间
     * @param userDetailsDTO 用户详情数据传输对象
     */
    @Override
    public void renewToken(UserDetailsDTO userDetailsDTO) {
        LocalDateTime expireTime = userDetailsDTO.getExpireTime();
        LocalDateTime currentTime = LocalDateTime.now();
        // 判断JWT是否将在20分钟后过期
        if (Duration.between(currentTime, expireTime).toMinutes() <= TWENTY_MINUTES) {
            // 若令牌即将过期，则刷新令牌
            refreshToken(userDetailsDTO);
        }
    }

    /**
     * 解析并验证令牌，返回其中的声明信息。
     *
     * @param token 待解析的令牌字符串
     * @return 令牌中的声明信息
     */
    @Override
    public Claims parseToken(String token) {
        // 生成解密所需的密钥
        SecretKey secretKey = generalKey();
        return Jwts.parser()
                .setSigningKey(secretKey)
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 从HTTP请求头中提取并验证令牌，进而获取用户详情数据传输对象。
     *
     * @param request HTTP请求对象
     * @return 用户详情数据传输对象，若无法获取或验证失败则返回null
     */
    @Override
    public UserDetailsDTO getUserDetailDTO(HttpServletRequest request) {
        //首先，使用Optional.ofNullable()方法检查请求头中是否存在TOKEN_HEADER，如果存在则将其作为字符串返回，如果不存在则返回空字符串。
        //然后，使用replaceFirst()方法将TOKEN_PREFIX从token字符串中删除，并返回处理后的token字符串。
        // orElse（other）如果存在值，则返回该值，否则返回 other.
        String token = Optional.ofNullable(request.getHeader(TOKEN_HEADER)).orElse("").replaceFirst(TOKEN_PREFIX, "");
        if (StringUtils.hasText(token) && !token.equals("null")) {
            // 解析令牌
            Claims claims = parseToken(token);
            // 获取用户ID
            String userId = claims.getSubject();
            // 从Redis中获取用户详情
            return (UserDetailsDTO) redisService.hGet(LOGIN_USER, userId);
        }
        return null;
    }

    /**
     * 从Redis中删除指定用户的登录信息。
     *
     * @param userId 需要删除登录信息的用户ID
     */
    @Override
    public void delLoginUser(Integer userId) {
        redisService.hDel(LOGIN_USER, String.valueOf(userId)); // 删除Redis中对应用户ID的登录信息
    }

    /**
     * 生成一个无横杠的UUID字符串。
     *
     * @return 生成的UUID字符串
     */
    public String getUuid() {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }

    /**
     * 根据配置的密钥字符串生成SecretKey对象。
     *
     * @return 基于AES算法的SecretKey对象
     */
    public SecretKey generalKey() {
        //先用BASE64编码密钥字符串
        byte[] encodedKey = Base64.getDecoder().decode(secret);
        //再利用ASE生成SecretKey对象签名
        return new SecretKeySpec(encodedKey, 0, encodedKey.length, "AES");
    }

}

