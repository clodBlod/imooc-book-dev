package com.imooc.repository;

import com.imooc.mo.MessageMO;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * @description: 操作MongoDB的数据层接口
 * Repository ：数据层注解
 * MongoRepository<T, ID>
 * T: 操作实体类的类名
 * TD: 操作实体类的主键类型
 */
@Repository
public interface MessageRepository extends MongoRepository<MessageMO,String> {

}
