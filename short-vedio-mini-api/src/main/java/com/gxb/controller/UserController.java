package com.gxb.controller;

import com.gxb.mapper.UsersFansMapper;
import com.gxb.mapper.UsersLikeVideosMapper;
import com.gxb.mapper.UsersMapper;
import com.gxb.pojo.Users;
import com.gxb.pojo.UsersReport;
import com.gxb.pojo.vo.PublisherVideo;
import com.gxb.pojo.vo.UsersVO;
import com.gxb.service.UserService;
import com.gxb.utils.JsonResult;
import com.gxb.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RestController
@Api(value = "用户相关业务的接口",tags = {"用户相关业务的controller"})
@RequestMapping("/user")
public class UserController extends BasicController{

    @Autowired
    private UserService userService;

    @ApiOperation(value = "用户上传头像",notes = "用户上传头像的接口")
    @PostMapping("uploadFace")
    @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "query")
    public JsonResult uploadFace(String userId,@RequestParam("file") MultipartFile[] files) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return JsonResult.errorMsg("用户id不能为空");
        }

        //文件保存的命名空间
        String fileSpace = "E:/short_video_library";
        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/face";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if(files != null && files.length > 0) {

                String fileName = files[0].getOriginalFilename();
                if(StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    String finalFacePath = fileSpace + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalFacePath);
                    if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }
            }else {
                return JsonResult.errorMsg("上传出错...");
            }
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            if(fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);
        userService.updateUserInfo(user);
        return JsonResult.ok(uploadPathDB);
    }

    @ApiOperation(value = "查询用户信息",notes = "查询用户信息的接口")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "query"),
        @ApiImplicitParam(name = "fanId",value = "粉丝Id",required = true,dataType = "String",paramType = "query")
    })
    @PostMapping("query")
    public JsonResult query(String userId,String fanId) {
        if (StringUtils.isBlank(userId)) {
            return JsonResult.errorMsg("用户id不能为空");
        }
        Users userInfo = userService.queryUserInfo(userId);
        UsersVO usersVO = new UsersVO();
        BeanUtils.copyProperties(userInfo, usersVO);
        usersVO.setIfFollow(userService.queryIfFollow(userId, fanId));

        return JsonResult.ok(usersVO);
    }

    @PostMapping("queryPublisher")
    public JsonResult queryPublisher(String loginUserId,String videoId,String publishUserId) {

        System.out.println(publishUserId);
        if(StringUtils.isBlank(publishUserId)) {
            return JsonResult.errorMsg("");
        }
        //查询视频发布者的信息
        Users userInfo = userService.queryUserInfo(publishUserId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo, publisher);
        //查询当前登陆者和视频得到点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);
        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);

        return JsonResult.ok(bean);
    }

    @PostMapping("beyourfans")
    public JsonResult beyourfans(String userId,String fanId) {
        if (StringUtils.isBlank(userId) && StringUtils.isBlank(fanId) ) {
            return JsonResult.errorMsg("");
        }

        userService.saveUserFanRelation(userId, fanId);
        return JsonResult.ok("关注成功...");
    }

    @PostMapping("dontbeyourfans")
    public JsonResult dontbeyourfans(String userId,String fanId) {
        if (StringUtils.isBlank(userId) && StringUtils.isBlank(fanId) ) {
            return JsonResult.errorMsg("");
        }

        userService.deleteUserFanRelation(userId, fanId);
        return JsonResult.ok("取消关注成功...");
    }

    @PostMapping("reportUser")
    public JsonResult reportUser(@RequestBody UsersReport usersReport) {

        //保存举报信息
        userService.reportUser(usersReport);
        return JsonResult.errorMsg("举报成功...");
    }
}
