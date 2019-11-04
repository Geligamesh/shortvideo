package com.gxb.controller;

import com.gxb.pojo.Users;
import com.gxb.pojo.vo.UsersVO;
import com.gxb.service.UserService;
import com.gxb.utils.JsonResult;
import com.gxb.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RestController
@Api(value = "用户登录注册的接口",tags = {"注册和登录的controller"})
public class RegistLoginController extends BasicController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户注册",notes = "用户注册的接口")
    @PostMapping("regist")
    public JsonResult regist(@RequestBody  Users user) throws NoSuchAlgorithmException {


        //判断用户名和密码不能为空
        if(StringUtils.isEmpty(user.getUsername()) && StringUtils.isEmpty(user.getPassword())) {
            return JsonResult.errorMsg("用户名密码不能为空");
        }
        //判断用户名是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());
        //保存用户，注册信息
        if(!usernameIsExist) {
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setFollowCounts(0);
            user.setReceiveLikeCounts(0);
            userService.saveUser(user);
        }else {
            return JsonResult.errorMsg("用户名已经存在");
        }
        user.setPassword("");

        // String uniqueToken = UUID.randomUUID().toString();
        // redis.set(USER_REDIS_SESSION + ":" + user.getId(), uniqueToken, 1000 * 60 * 30);
        // UsersVO usersVO = new UsersVO();
        // BeanUtils.copyProperties(user, usersVO);
        // usersVO.setUserToken(uniqueToken);
        UsersVO usersVO = setUserRedisSessionToken(user);

        return JsonResult.ok(usersVO);
    }

    public UsersVO setUserRedisSessionToken(Users user) {
        String uniqueToken = UUID.randomUUID().toString();
        redis.set(USER_REDIS_SESSION + ":" + user.getId(), uniqueToken, 1000 * 60 * 30);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(user, usersVO);
        usersVO.setUserToken(uniqueToken);
        return usersVO;
    }

    @ApiOperation(value = "用户登录",notes = "用户登录的接口")
    @PostMapping("login")
    public JsonResult login(@RequestBody Users user) throws Exception {
        String username = user.getUsername();
        String password = user.getPassword();

        Thread.sleep(2000);

        //判断用户名或者密码不能为空
        if(StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
            return JsonResult.errorMsg("用户名或密码不能为空");
        }
        //判断用户名密码是否存在
        Users userResult = userService.queryUserForLogin(username,MD5Utils.getMD5Str(password));
        //返回结果
        if(userResult != null) {
            userResult.setPassword("");
            UsersVO usersVO = setUserRedisSessionToken(userResult);
            return JsonResult.ok(usersVO);
        }else {
            return JsonResult.errorMsg("用户名或密码错误");
        }
    }

    @ApiOperation(value = "用户注销",notes = "用户注销的接口")
    @PostMapping("logout")
    @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "query")
    public JsonResult logout(String userId) throws Exception {
        Thread.sleep(1000);
        redis.del(USER_REDIS_SESSION + ":" + userId);
        return JsonResult.ok();
    }
}
