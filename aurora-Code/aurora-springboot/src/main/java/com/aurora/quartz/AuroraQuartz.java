package com.aurora.quartz;

import cn.hutool.core.date.LocalDateTimeUtil;
import com.alibaba.fastjson.JSON;
import com.aurora.model.dto.ArticleSearchDTO;
import com.aurora.model.dto.UserAreaDTO;
import com.aurora.entity.*;
import com.aurora.mapper.ElasticsearchMapper;
import com.aurora.mapper.UniqueViewMapper;
import com.aurora.mapper.UserAuthMapper;
import com.aurora.service.*;
import com.aurora.util.BeanCopyUtil;
import com.aurora.util.IpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

import static com.aurora.constant.CommonConstant.UNKNOWN;
import static com.aurora.constant.RedisConstant.*;

/**
 *  @description: 本类主要是提供了一些定时任务调用的api接口，也就是定时任务要调用的一些方法的具体逻辑实现类
 *
*/
@Slf4j
/**
 * 将当前类标记为一个Spring Bean，并将其命名为"auroraQuartz"，这样在Spring容器中就可以通过名字获取到该对象。
 */
@Component("auroraQuartz")
public class AuroraQuartz {

    @Autowired
    private RedisService redisService;

    @Autowired
    private ArticleService articleService;

    @Autowired
    private JobLogService jobLogService;

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private RoleResourceService roleResourceService;

    @Autowired
    private UniqueViewMapper uniqueViewMapper;

    @Autowired
    private UserAuthMapper userAuthMapper;

    /**
     * 。RestTemplate用于发送HTTP请求并获取响应，简化了与RESTful API的交互过程。这
     */
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ElasticsearchMapper elasticsearchMapper;


    @Value("${website.url}")
    private String websiteUrl;

    /**
     * 保存唯一的视图数据到数据库。该方法会统计当前唯一访客的数量，并将统计数据保存到数据库的历史记录中。
     */
    public void saveUniqueView() {
        // 获取当前唯一访客数
        Long count = redisService.sSize(UNIQUE_VISITOR);
        // 构建唯一视图对象并插入数据库
        UniqueView uniqueView = UniqueView.builder()
                .createTime(LocalDateTimeUtil.offset(LocalDateTime.now(), -1, ChronoUnit.DAYS))
                .viewsCount(Optional.of(count.intValue()).orElse(0))
                .build();
        uniqueViewMapper.insert(uniqueView);
    }

    /**
     * 清理缓存数据。该方法会清除redis中存储的唯一访客和访客地区信息。
     */
    public void clear() {
        // 清除唯一访客和访客地区缓存
        redisService.del(UNIQUE_VISITOR);
        redisService.del(VISITOR_AREA);
    }

    /**
     * 统计用户地区信息。该方法会统计所有用户来源地区，并将结果存储到缓存中。
     */
    public void statisticalUserArea() {
        // 查询用户认证信息中的IP来源，并统计各地区用户数量
        Map<String, Long> userAreaMap = userAuthMapper.selectList(new LambdaQueryWrapper<UserAuth>().select(UserAuth::getIpSource))
                .stream()
                //map对流中的每个元素进行映射操作，表示为 item
                .map(item -> {
                    // 对IP来源进行处理，未知来源统一标记为UNKNOWN
                    if (Objects.nonNull(item) && StringUtils.isNotBlank(item.getIpSource())) {
                        return IpUtil.getIpProvince(item.getIpSource());
                    }
                    return UNKNOWN;
                })
                // 通过Collectors.groupingBy()方法对流中的元素进行分组，并进行计数
                .collect(Collectors.groupingBy(item -> item, Collectors.counting()));
        // 将地区统计结果转换为用户地区列表，并存储到缓存中
        List<UserAreaDTO> userAreaList = userAreaMap.entrySet().stream()
                //对userAreaMap中的每一对键值对进行处理，将其封装为UserAreaDTO
                .map(item -> UserAreaDTO.builder()
                        .name(item.getKey())
                        .value(item.getValue())
                        .build())
                .collect(Collectors.toList());
        redisService.set(USER_AREA, JSON.toJSONString(userAreaList));
    }

    /**
     * 对文章进行百度SEO处理。该方法会为所有文章生成百度提交链接，并模拟提交到百度。
     */
    public void baiduSeo() {
        // 获取所有文章ID，模拟提交到百度
        List<Integer> ids = articleService.list().stream().map(Article::getId).collect(Collectors.toList());
        HttpHeaders headers = new HttpHeaders();
        // 设置请求头部信息
        headers.add("Host", "data.zz.baidu.com");
        headers.add("User-Agent", "curl/7.12.1");
        headers.add("Content-Length", "83");
        headers.add("Content-Type", "text/plain");
        ids.forEach(item -> {
            // 构建文章提交链接，并发送POST请求
            String url = websiteUrl + "/articles/" + item;
            HttpEntity<String> entity = new HttpEntity<>(url, headers);
            restTemplate.postForObject("https://www.baidu.com", entity, String.class);
        });
    }

    /**
     * 清理任务日志。该方法会清除系统中的任务日志。
     */
    public void clearJobLogs() {
        // 调用任务日志服务，清除任务日志
        jobLogService.cleanJobLogs();
    }

    /**
     * 导入Swagger资源信息。该方法会重新导入Swagger定义的资源，并为所有资源设置默认的角色权限。
     */
    public void importSwagger() {
        // 导入Swagger资源
        resourceService.importSwagger();
        // 获取所有资源ID，为每个资源设置默认角色权限
        List<Integer> resourceIds = resourceService.list().stream().map(Resource::getId).collect(Collectors.toList());
        List<RoleResource> roleResources = new ArrayList<>();
        for (Integer resourceId : resourceIds) {
            roleResources.add(RoleResource.builder()
                    .roleId(1) // 设置默认角色ID
                    .resourceId(resourceId)
                    .build());
        }
        // 批量保存角色资源关系
        roleResourceService.saveBatch(roleResources);
    }

    /**
     * 将数据导入到Elasticsearch。该方法会清空Elasticsearch中的所有数据，并重新导入数据库中的所有文章数据。
     */
    public void importDataIntoES() {
        // 先清空Elasticsearch中的数据
        elasticsearchMapper.deleteAll();
        // 导入所有文章数据
        List<Article> articles = articleService.list();
        for (Article article : articles) {
            elasticsearchMapper.save(BeanCopyUtil.copyObject(article, ArticleSearchDTO.class));
        }
    }

}
