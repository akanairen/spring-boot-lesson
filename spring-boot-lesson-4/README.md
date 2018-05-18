## Spring boot MyBatis 和使用 PageHelper 分页插件

#### 引入 ```POM``` 依赖

```XML
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>1.3.2</version>
</dependency>

<!-- MySQL -->
<dependency>
    <groupId>mysql</groupId>
    <artifactId>mysql-connector-java</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- 分页插件 -->
<dependency>
    <groupId>com.github.pagehelper</groupId>
    <artifactId>pagehelper-spring-boot-starter</artifactId>
    <version>1.2.3</version>
</dependency>
```

#### 配置 ```properties```

```properties
server.port=80
# DataSource 配置
spring.datasource.driver-class-name=com.mysql.jdbc.Driver
spring.datasource.url=jdbc:mysql://127.0.0.1:3306/boot_lesson?useUnicode=true&characterEncoding=utf8&allowMultiQueries=true&useSSL=false
spring.datasource.username=root
spring.datasource.password=123456
# MyBatis
mybatis.mapper-locations=classpath:/mybatis/mapping/*.xml
mybatis.type-aliases-package=com.pb.springbootlesson4.entity
# PageHelper 分页组件
## 方言
pagehelper.helper-dialect=mysql
pagehelper.reasonable=true
pagehelper.support-methods-arguments=true
pagehelper.params=count=countSql
# thymeleaf
spring.thymeleaf.cache=false
# 打印 SQL 语句
logging.level.com.pb.springbootlesson4.dao=debug
```

> [参考 MyBatis PageHelper 官方使用说明](https://github.com/pagehelper/Mybatis-PageHelper/blob/master/wikis/zh/HowToUse.md)
>
> 在没有配置连接池的情况下，默认使用 ```HikariDataSource```

#### 编写 ```Mapper```

```java
@Mapper
public interface BookMapper {

    List<Book> find();
}
```

**BookMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd" >
<mapper namespace="com.pb.springbootlesson4.dao.BookMapper">

    <sql id="columns">
        id, name, author, publisher, price
    </sql>

    <resultMap id="BookMap" type="com.pb.springbootlesson4.entity.Book">
        <id property="id" column="id"/>
        <result property="name" column="name"/>
        <result property="author" column="author"/>
        <result property="publisher" column="publisher"/>
        <result property="price" column="price" javaType="DECIMAL"/>
    </resultMap>

    <select id="find" resultMap="BookMap">
        SELECT
        <include refid="columns"/>
        FROM book
    </select>
    
</mapper>
```



#### 编写 ```Controller``` 使用

```java
@Controller
@RequestMapping("/book/")
public class BookController {

    @Autowired
    private BookMapper bookMapper;

    @RequestMapping("")
    public String list(@RequestParam(required = false, defaultValue = "1") Integer page, ModelMap modelMap) {
        PageHelper.startPage(page, 10);
        // 紧接着 startPage 方法
        List<Book> books = bookMapper.find();
        PageInfo<Book> pageInfo = new PageInfo<>(books);

        modelMap.put("pageInfo", pageInfo);
        return "books";
    }
}
```

#### 编写 ```Thymeleaf``` 展示页面

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8"/>
    <title>Books</title>
    <style type="text/css">
        .overflow {
            white-space: nowrap;
            text-overflow: ellipsis;
            overflow: hidden;
        }
    </style>
</head>
<body>
<table border="1" cellpadding="10" cellspacing="2" align="center" style="table-layout: fixed;" width="1100">
    <caption style="font-size: 26px">京东热销书籍排行榜</caption>
    <colgroup>
        <col width="53"/>
        <col width="400"/>
        <col width="200"/>
        <col width="180"/>
        <col width="80"/>
    </colgroup>
    <thead>
    <tr style="background-color: darkseagreen;">
        <th>序号</th>
        <th>书名</th>
        <th>作者</th>
        <th>出版社</th>
        <th>价格</th>
    </tr>
    </thead>
    <tbody>
    <tr th:each="book, bookSta : ${pageInfo.list}" th:style="${bookSta.even} ? 'background-color:aquamarine;' : ''">
        <td th:text="${bookSta.index + pageInfo.startRow}"></td>
        <td th:text="${book.name}" th:title="${book.name}" class="overflow"></td>
        <td th:text="${book.author}" th:title="${book.author}" class="overflow"></td>
        <td th:text="${book.publisher}" th:title="${book.publisher}" class="overflow"></td>
        <td th:text="${'￥' + #numbers.formatDecimal(book.price, 0, 'COMMA', 2, 'POINT')}"
            style="color: red;font-weight: bold;"></td>
    </tr>
    </tbody>
    <tfoot>
    <tr>
        <td colspan="5">
            <a th:href="@{'?page=' + 1}">首页</a>&nbsp;
            <a th:if="${!pageInfo.isFirstPage}" th:href="@{'?page=' + ${pageInfo.prePage}}">上一页</a>
            <a th:if="${pageInfo.isFirstPage}">上一页</a>&nbsp;
            <a th:if="${pageInfo.isLastPage}">下一页</a>
            <a th:if="${!pageInfo.isLastPage}" th:href="@{'?page=' + ${pageInfo.nextPage}}">下一页</a>&nbsp;
            <a th:href="@{'?page=' + ${pageInfo.pages}}">末页</a>&nbsp;
            <span style="float: right">
                共&nbsp;<span style="color: red;" th:text="${pageInfo.pages}"></span>&nbsp;页,&nbsp;
                当前第(&nbsp;<span style="color: red;" th:text="${pageInfo.pageNum}"></span>&nbsp;)页
            </span>
        </td>
    </tr>
    </tfoot>
</table>
</body>
</html>
```