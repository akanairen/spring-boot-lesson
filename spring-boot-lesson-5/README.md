# Spring Boot 集成 Cache、Redis、MongoDB

使用 JPA 将数据持久到 MongoDB 中，在 Service 层添加```内存 Cache ``` 和 ```Redis Cache``` 。

#### 引入 ```POM``` 依赖

```xml
<!-- Cache 相关 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-cache</artifactId>
</dependency>

<!-- MongoDB 支持 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-mongodb</artifactId>
</dependency>

<!-- Redis 支持 -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
```

#### 配置 ```properties``` 

```properties
# Redis
spring.redis.host=192.168.2.72
spring.redis.port=6379
# MongoDB
# 格式：	协议://用户名:密码@主机:端口/数据库
spring.data.mongodb.uri=mongodb://localhost:27017/persons
```

#### 创建 ```Configuration``` 配置 ```CacheManager```

```java
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.Collections;

@Configuration
// 启用缓存配置
@EnableCaching
public class CacheConfiguration {

    @Bean(name = "simpleCache")
    public CacheManager simpleCacheManager() {
        SimpleCacheManager simpleCacheManager = new SimpleCacheManager();
        // 配置缓存名
        ConcurrentMapCache cache = new ConcurrentMapCache("cache-simple");
        simpleCacheManager.setCaches(Collections.singleton(cache));

        return simpleCacheManager;
    }

    // 多个 CacheManager 情况下，需要设置一个主的 CacheManager
    @Primary
    @Bean(name = "redisCache")
    public CacheManager redisCacheManager(RedisTemplate redisTemplate) {
        RedisCacheManager redisCacheManager = new RedisCacheManager(redisTemplate);
        redisCacheManager.setCacheNames(Collections.singleton("cache-redis"));
        // 应用启动时候就把远程缓存加载进来
        redisCacheManager.setLoadRemoteCachesOnStartup(true);

        return redisCacheManager;
    }
}
```

#### 创建 ```Entity```

```java
// 指定集合名称
@Document(collection = "persons")
public class Person implements Serializable {

    private static final long serialVersionUID = 7904444527044732291L;

    // 指定 ID 主键
    @Id
    private Integer id;

    private String name;

    private Integer age;

    /*  get, set, toString .... */
}
```

#### 创建持久到 ```Redis Repository``` 

```java
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

/** ========================== */

@Repository
public class PersonRepositoryImpl implements PersonRepository {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Person findPerson(Integer id) {
        System.out.println("find persion: " + id);
        return (Person) redisTemplate.opsForValue().get(id);
    }

    @Override
    public boolean savePerson(Person person) {
        System.out.println("save Person: " + person);
        redisTemplate.opsForValue().set(person.getId(), person);
        return true;
    }

    @Override
    public boolean deletePerson(Integer id) {
        System.out.println("delete Person: " + id);
        redisTemplate.delete(id);
        return true;
    }
}
```

#### 创建持久到 ```MongoDB Repository```

```java
@Repository
public interface PersonMongoRepository extends MongoRepository<Person, Integer> {
}
```

#### 在 Service 层实现缓存策略

```java
public interface PersonService {

    @Cacheable(cacheManager = "redisCache", cacheNames = "cache-redis")
    Person find(Integer id);

    Person save(Person person);

    @CacheEvict(cacheManager = "redisCache", cacheNames = "cache-redis")
    void delete(Integer id);
}
```

```java
@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonMongoRepository personMongoRepository;

    @Override
    public Person find(Integer id) {
        System.out.println("Find Person: " + id);
        return personMongoRepository.findOne(id);
    }

    @Override
    public Person save(Person person) {
        return personMongoRepository.save(person);
    }

    @Override
    public void delete(Integer id) {
        System.out.println("Delete Person: " + id);
        personMongoRepository.delete(id);
    }
}
```

> [Cache 注解说明](https://www.cnblogs.com/fashflying/p/6908028.html)

#### 创建 ```Controller```

```java
@RestController
@RequestMapping("/persons/")
public class PersonController {

    @Autowired
    private PersonRepository repository;

    @PostMapping("")
    public Person save(@RequestBody Person person) {
        repository.savePerson(person);
        return person;
    }

    @GetMapping("{id}")
    public Person get(@PathVariable("id") Integer id) {
        return repository.findPerson(id);
    }

    @DeleteMapping("{id}")
    public Person delete(@PathVariable("id") Integer id) {
        Person person = repository.findPerson(id);
        repository.deletePerson(id);
        return person;
    }

}
```

```java
@RestController
@RequestMapping("/mongo/persons/")
public class PersonMongoController {

    @Autowired
    private PersonService personService;

    @PostMapping("")
    public Person save(@RequestBody Person person) {
        personService.save(person);
        return person;
    }

    @GetMapping("{id}")
    public Person find(@PathVariable Integer id) {
        return personService.find(id);
    }

    @DeleteMapping("{id}")
    public Boolean delete(@PathVariable Integer id) {
        personService.delete(id);
        return true;
    }
}
```

#### 最后启动应用，使用 ```Postman``` 进行测试

> Redis 安装教程 [连接](https://redis.io/topics/quickstart)
>
> Redis 启动时指定 conf 配置文件
>
> ``` redis-server /etc/redis.conf ```
>
> Linux 下查看进程位置
>
> ```ls -al /proc/4170```
>
> 