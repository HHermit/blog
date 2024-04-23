package com.aurora.constant;

public interface AuthConstant {

    int TWENTY_MINUTES = 20;

    /**
     * 到期时间 7天
     */
    int EXPIRE_TIME = 7 * 24 * 60 * 60;

    String TOKEN_HEADER = "Authorization";

    String TOKEN_PREFIX = "Bearer ";

}
