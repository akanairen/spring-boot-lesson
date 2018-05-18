# Swagger 生成 API 及接口文档

导入依赖

```xml
<!-- swagger2核心依赖 -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
    <version>2.7.0</version>
    <scope>compile</scope>
</dependency>

<!-- swagger-ui 提供展示及测试页面 -->
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
    <version>2.7.0</version>
    <scope>compile</scope>
</dependency>
```

注册 Swagger2 配置类

```java
@Configuration
@EnableSwagger2
@ComponentScan(basePackages = {"com.pb.springbootlesson3.controller"})
public class Swagger2Config extends WebMvcConfigurerAdapter {

    /**
     * 这个地方要重新注入一下资源文件，不然不会注入资源的，也没有注入requestHandlerMappping,相当于xml配置的
     * <!--swagger资源配置-->
     * <mvc:resources location="classpath:/META-INF/resources/" mapping="swagger-ui.html"/>
     * <mvc:resources location="classpath:/META-INF/resources/webjars/" mapping="/webjars/**"/>
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars*")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }

    @Bean
    public Docket createDocketApi() {
        ApiInfo apiInfo = new ApiInfoBuilder()
                .title("Api 接口文档")
                .description("一个 Swagger 示例")
                .version("1.0")
                .build();

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo)
                .select()
                .paths(PathSelectors.any())
                .build();
    }
}
```

编写 Controller

```java
@Api("用户模块")
@RestController()
@RequestMapping("/users")
public class UserController {

    private List<User> users;

    @PostConstruct
    public void init() {
        users = new ArrayList<>(10);
        for (int i = 0; i < 10; i++) {
            User user = new User();
            user.setId(i);
            user.setName(Long.toString(System.currentTimeMillis(), Character.MAX_RADIX));
            user.setNickName(Long.toString(Long.reverse(System.currentTimeMillis()), Character.MAX_RADIX));
            user.setPhone(String.valueOf(new Random().nextLong()));
            user.setAddress("广东深圳");

            users.add(user);
        }
    }

    @ApiOperation(value = "获取用户列表", notes = "无参数")
    @GetMapping("")
    public List<User> list() {
        return users;
    }

    @ApiOperation(value = "新增用户", notes = "新创建一个用户")
    @ApiImplicitParam(name = "user", value = "用户对象", required = true, dataType = "User")
    @PostMapping("")
    public Map<String, Object> add(@RequestBody User user) {
        users.add(user);

        return new HashMap<String, Object>() {{
            put("result", "SUCCESS");
            put("code", "200");
        }};
    }

    @ApiOperation(value = "查询单个用户", notes = "根据 ID 查询")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true)
    @GetMapping("/{id}")
    public User get(@PathVariable(name = "id", required = false) Integer id) {
        if (id == null) {
            return users.get(0);
        }
        for (User user : users) {
            if (user.getId().intValue() == id) {
                return user;
            }
        }
        return new User();
    }
}
```

```
@Api：用在类上，说明该类的作用
@ApiOperation：用在方法上，说明方法的作用
@ApiImplicitParams：用在方法上包含一组参数说明
@ApiImplicitParam：用在 @ApiImplicitParams 注解中，指定一个请求参数的各个方面
    paramType：参数放在哪个地方
        · header --> 请求参数的获取：@RequestHeader
        · query -->请求参数的获取：@RequestParam
        · path（用于restful接口）--> 请求参数的获取：@PathVariable
        · body（不常用）
        · form（不常用）
    name：参数名
    dataType：参数类型
    required：参数是否必须传
    value：参数的意思
    defaultValue：参数的默认值
@ApiResponses：用于表示一组响应
@ApiResponse：用在@ApiResponses中，一般用于表达一个错误的响应信息
    code：数字，例如400
    message：信息，例如"请求参数没填好"
    response：抛出异常的类
@ApiModel：描述一个Model的信息（这种一般用在post创建的时候，使用@RequestBody这样的场景，请求参数无法使用@ApiImplicitParam注解进行描述的时候）
@ApiModelProperty：描述一个model的属性
```

> [参考](https://www.cnblogs.com/magicalSam/p/7197533.html)

接口文档，JSON 格式

> 地址栏输入：http://localhost/v2/api-docs

测试页面

> 地址栏输入：http://localhost/swagger-ui.html

待续

**权限控制**