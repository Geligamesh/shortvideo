package com.gxb.controller;

import com.gxb.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {

    @Autowired
    public RedisOperator redis;

    public static final String USER_REDIS_SESSION = "user-redis-session";
    //文件保存的命名空间
    public static final String  FILE_SPACE = "E:/short_video_library";

    public static final String  FFMPNG_EXE = "E:\\mySoftware\\ffmpeg\\ffmpeg-20180723-d134b8d-win64-static\\bin\\ffmpeg.exe";

    //每页分页的记录数
    public static final Integer PAGE_SIZE = 5;

}
