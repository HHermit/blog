package com.aurora.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 *  @description:自定义的MetaObjectHandler实现类，用于在数据库插入和更新操作时自动填充特定字段的值
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
    * @Description:自动填充创建时间(createTime)字段的当前时间。
     * 有这个就自动填充，没有就不填充
    * @Param: [metaObject]
    * @return: void
    */
    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("start insert fill ....");
        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now());
    }


    /**
    * @Description:自动填充更新时间(updateTime)字段的当前时间。
    * @Param: [metaObject]
    * @return: void
    */
    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("start update fill ....");
        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}
