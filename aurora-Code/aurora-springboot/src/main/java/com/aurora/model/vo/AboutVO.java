package com.aurora.model.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AboutVO {

    /**
     * 这个函数是用于给API中的"About内容"字段添加说明的。使用了Swagger的@ApiModelProperty注解，
     * 通过name属性指定了字段名，value属性指定了字段的描述，required属性指定了字段是否必须，dataType属性指定了字段的数据类型。
     * 这个函数的作用是在生成API文档时，
     * 提供关于"About内容"字段的详细信息，帮助开发者更好地理解和使用该字段。
     */
    @ApiModelProperty(name = "About内容", value = "content", required = true, dataType = "String")
    private String content;
}
