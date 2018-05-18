package com.pb.springbootlesson3.controller;

import com.pb.springbootlesson3.domain.User;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.*;

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
