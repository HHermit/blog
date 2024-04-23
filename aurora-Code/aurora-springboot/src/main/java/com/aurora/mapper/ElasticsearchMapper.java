package com.aurora.mapper;

import com.aurora.model.dto.ArticleSearchDTO;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;


/**
 * elasticsearch
 * 该函数定义了一个名为ElasticsearchMapper的接口，该接口继承自ElasticsearchRepository接口，并且泛型参数为ArticleSearchDTO和Integer。
 * 其中，ArticleSearchDTO表示存储在Elasticsearch中的文档数据类型，Integer表示文档的主键类型。
 * 该接口继承自ElasticsearchRepository接口，因此可以使用ElasticsearchRepository中定义的方法来对Elasticsearch中的数据进行增删改查等操作。
 * 通过该接口，可以方便地将ArticleSearchDTO对象存储到Elasticsearch中，并且可以通过主键来检索文档。
 * 总结来说，该函数定义了一个用于操作Elasticsearch中ArticleSearchDTO类型数据的接口，提供了方便的数据存储和检索功能。
 */
@Repository
public interface ElasticsearchMapper extends ElasticsearchRepository<ArticleSearchDTO,Integer> {

}
