package com.aurora.service;

import com.aurora.model.dto.AboutDTO;
import com.aurora.model.dto.AuroraAdminInfoDTO;
import com.aurora.model.dto.AuroraHomeInfoDTO;
import com.aurora.model.dto.WebsiteConfigDTO;
import com.aurora.model.vo.AboutVO;
import com.aurora.model.vo.WebsiteConfigVO;

public interface AuroraInfoService {

    /**
     * 上报访客信息，在redis 中存储
     * UNIQUE_VISITOR 用户ip 浏览器 操作系统生成唯一标识
     * VISITOR_AREA 用户地域信息
     * BLOG_VIEWS_COUNT 博客访问量
     */
    void report();

    /**
     * 获取前台blog首页展示需要展示的信息
     */
    AuroraHomeInfoDTO getAuroraHomeInfo();

    /**
     * 获取 Aurora 后台管理员页面所需展示信息。
     */
    AuroraAdminInfoDTO getAuroraAdminInfo();

    /**
     * 更新网站配置。
     * @param websiteConfigVO 网站配置的视图对象，包含要更新的配置信息。
     */
    void updateWebsiteConfig(WebsiteConfigVO websiteConfigVO);

    /**
     * 获取网站配置。
     */
    WebsiteConfigDTO getWebsiteConfig();

    /**
     * 更新网站about信息。
     * @param aboutVO 关于信息的视图对象，包含要更新的关于信息。
     */
    void updateAbout(AboutVO aboutVO);

    /**
     * 获取网站about信息。
     */
    AboutDTO getAbout();

}
