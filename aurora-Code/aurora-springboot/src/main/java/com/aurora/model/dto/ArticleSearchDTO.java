package com.aurora.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
/**
 * Document注解： 用Elasticsearch进行数据存储和检索时，
 * 这个注解可以将类映射为Elasticsearch中的文档类型，并且指定文档所属的索引  article  。
 * Field注解：设置字段类型和搜索策略（ik_max_word：使用ik分词器中的最大切分策略）
 */
@Document(indexName = "article")
public class ArticleSearchDTO {

    //es中的id
    @Id
    private Integer id;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String articleTitle;

    @Field(type = FieldType.Text, analyzer = "ik_max_word")
    private String articleContent;

    @Field(type = FieldType.Integer)
    private Integer isDelete;

    @Field(type = FieldType.Integer)
    private Integer status;

}
