package com.pb.springbootlesson5.repository;

import com.pb.springbootlesson5.entity.Person;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface PersonRepository {

    // 与之对应的有 @CachePut ，二者区别：
    // @Cacheable 缓存命中后将不调用代理方法
    // @CachePut  缓存命中后会调用代理方法
    @Cacheable(cacheManager = "simpleCache", cacheNames = "cache-simple")
    Person findPerson(Integer id);

    boolean savePerson(Person person);

    @CacheEvict(cacheManager = "redisCache", cacheNames = "cache-simple")
    boolean deletePerson(Integer id);

}
