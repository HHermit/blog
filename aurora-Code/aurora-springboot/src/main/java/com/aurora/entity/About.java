package com.aurora.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 关于信息的数据类，映射到数据库中的t_about表。
 * 使用Lombok简化对象构建和数据访问。
 */
@Data      // @Data 提供所有属性的getter和setter方法，以及toString、hashCode和equals方法。
@Builder   // 允许使用builder模式来创建About对象的实例
@NoArgsConstructor // 生成一个无参数的构造函数
@AllArgsConstructor // 生成一个带所有属性的构造函数
@TableName("t_about") //将该类映射到数据库中的t_about表。
public class About {

    @TableId(type = IdType.AUTO) // 指定id属性为主键，并使用自动增长的方式生成主键值。
    private Integer id;

    private String content;

    @TableField(fill = FieldFill.INSERT) // 指定createTime属性在插入记录时自动生成并填充。
    private LocalDateTime createTime;

    @TableField(fill = FieldFill.UPDATE) // 指定updateTime属性在更新记录时自动生成并填充。
    private LocalDateTime updateTime;
}

