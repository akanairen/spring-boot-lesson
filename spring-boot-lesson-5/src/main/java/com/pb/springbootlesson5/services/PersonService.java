package com.pb.springbootlesson5.services;

import com.pb.springbootlesson5.entity.Person;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

public interface PersonService {

    @Cacheable(cacheManager = "redisCache", cacheNames = "cache-redis")
    Person find(Integer id);

    Person save(Person person);

    @CacheEvict(cacheManager = "redisCache", cacheNames = "cache-redis")
    void delete(Integer id);
}
