package com.gxb.controller;

import com.gxb.enums.VideoStatusEnum;
import com.gxb.pojo.Bgm;
import com.gxb.pojo.Comments;
import com.gxb.pojo.Videos;
import com.gxb.service.BgmService;
import com.gxb.service.VideoService;
import com.gxb.utils.FFMpegUtils;
import com.gxb.utils.JsonResult;
import com.gxb.utils.PagedResult;
import io.swagger.annotations.*;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

@RestController
@RequestMapping("video")
@Api(value = "视频相关业务的接口",tags = "视频相关业务的controller")
public class VideoController extends BasicController{

    @Autowired
    private BgmService bgmService;
    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "上传视频",notes = "上传视频的接口")
    @PostMapping(value = "upload",headers = "content-type=multipart/form-data")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "form"),
        @ApiImplicitParam(name = "bgmId",value = "背景音乐Id",required = false,dataType = "String",paramType = "form"),
        @ApiImplicitParam(name = "videoSeconds",value = "背景音乐播放长度",required = true,dataType = "String",paramType = "form"),
        @ApiImplicitParam(name = "videoWidth",value = "视频宽度",required = true,dataType = "String",paramType = "form"),
        @ApiImplicitParam(name = "videoHeight",value = "视频高度",required = true,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "desc",value = "视频描述",required = false,dataType = "String",paramType = "form")
    })
    public JsonResult upload(String userId,
                             String bgmId,double videoSeconds,int videoWidth,int videoHeight, String desc,
                             @ApiParam(value = "短视频",required = true) MultipartFile file) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return JsonResult.errorMsg("用户id不能为空");
        }

        //文件保存的命名空间
        // String fileSpace = "E:/short_video_library";
        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        String coverPathDB = "/" + userId + "/video";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        //文件上传的最终保存路径
        String finalVideoPath = "";
        try {
            if(file != null) {

                String fileName = file.getOriginalFilename();
                String fileNamePrefix = fileName.split("\\.")[0];


                if(StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    finalVideoPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);
                    coverPathDB = coverPathDB + "/" + fileNamePrefix + ".jpg";

                    File outFile = new File(finalVideoPath);
                    if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
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
        //判断bgmId是否为空，如果不为空，那就查询bgm的信息
        //并且合并视频，产生新的视频
        if (StringUtils.isNotBlank(bgmId)) {
            Bgm bgm = bgmService.queryBgmById(bgmId);
            String mp3InputPath = FILE_SPACE + bgm.getPath();
            FFMpegUtils tool = new FFMpegUtils(FFMPNG_EXE);
            String videoInputPath = finalVideoPath;
            String videoOutputName = UUID.randomUUID().toString() + ".mp4";
            uploadPathDB = "/" + userId + "/video" + "/" + videoOutputName;
            finalVideoPath = FILE_SPACE + uploadPathDB;

            tool.convertor(videoInputPath, mp3InputPath, videoSeconds,finalVideoPath);
        }
        System.out.println("uploadPathDB=" + uploadPathDB);
        System.out.println("finalVideoPath=" + finalVideoPath);

        //对视频进行截图
        FFMpegUtils ffMpegUtils = new FFMpegUtils(FFMPNG_EXE);
        ffMpegUtils.getCover(finalVideoPath, FILE_SPACE + coverPathDB);

        //保存视频信息到数据库
        Videos video = new Videos();
        video.setAudioId(bgmId);
        video.setUserId(userId);
        video.setVideoSeconds((float) videoSeconds);
        video.setVideoHeight(videoHeight);
        video.setVideoWidth(videoWidth);
        video.setVideoDesc(desc);
        video.setVideoPath(uploadPathDB);
        video.setCoverPath(coverPathDB);
        video.setCreateTime(new Date());
        video.setStatus(VideoStatusEnum.SUCCESS.value);
        videoService.saveVideo(video);

        return JsonResult.ok(video.getId());
    }

    @ApiOperation(value = "上传封面",notes = "上传封面的接口")
    @PostMapping(value = "uploadCover",headers = "content-type=multipart/form-data")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "videoId",value = "视频主键Id",required = true,dataType = "String",paramType = "form"),
            @ApiImplicitParam(name = "userId",value = "用户Id",required = true,dataType = "String",paramType = "form")
    })
    public JsonResult uploadCover(String videoId,
                                  String userId
    ,@ApiParam(value = "视频封面",required = true) MultipartFile file) throws Exception {

        if (StringUtils.isBlank(videoId) || StringUtils.isBlank(userId)) {
            return JsonResult.errorMsg("视频主键和用户Id不能为空...");
        }

        //文件保存的命名空间
        // String fileSpace = "E:/short_video_library";
        //保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/video";
        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        //视频封面的最终保存路径
        String finalCoverPath = "";
        try {
            if(file != null) {

                String fileName = file.getOriginalFilename();
                if(StringUtils.isNotBlank(fileName)) {
                    //文件上传的最终保存路径
                    finalCoverPath = FILE_SPACE + uploadPathDB + "/" + fileName;
                    //设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalCoverPath);
                    if(outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        //创建文件夹
                        outFile.getParentFile().mkdirs();
                    }
                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = file.getInputStream();
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
        videoService.updateVideo(videoId, uploadPathDB);
        return JsonResult.ok();
    }

    @PostMapping(value = "showAll")
    public JsonResult showAll(@RequestBody Videos video, Integer isSaveRecord,Integer page,Integer pageSize) {
        if (page == null) {
            page = 1;
        }
        if (pageSize == null) {
            pageSize = PAGE_SIZE;
        }

        PagedResult result = videoService.getAllVideos(video,isSaveRecord,page, pageSize);
        return JsonResult.ok(result);
    }

    @PostMapping(value = "hot")
    public JsonResult hot() {

        return JsonResult.ok(videoService.getHotWords());
    }

    @PostMapping(value = "userLike")
    public JsonResult userLike(String userId,String videoId,String videoCreatorId) {
        videoService.userLikeVideo(userId,videoId,videoCreatorId);
        return JsonResult.ok(videoService.getHotWords());
    }

    @PostMapping(value = "userUnLike")
    public JsonResult userUnLike(String userId,String videoId,String videoCreatorId) {
        videoService.userUnlikeVideo(userId, videoId, videoCreatorId);
        return JsonResult.ok(videoService.getHotWords());
    }

    @PostMapping("showMyLike")
    public JsonResult showMyLike(String userId,Integer page,Integer pageSize) {

        if(StringUtils.isBlank(userId)) {
            return JsonResult.errorMsg("");
        }
        if(page == null) {
            page = 1;
        }
        if(pageSize == null) {
            pageSize = 6;
        }
        PagedResult videoList = videoService.queryMyLikeVideos(userId, page, pageSize);
        return JsonResult.ok(videoList);
    }

    @PostMapping("showMyFollow")
    public JsonResult showMyFollow(String userId,Integer page,Integer pageSize) {

        if(StringUtils.isBlank(userId)) {
            return JsonResult.errorMsg("");
        }
        if(page == null) {
            page = 1;
        }
        if(pageSize == null) {
            pageSize = 6;
        }
        PagedResult videoList = videoService.queryMyFollowVideos(userId, page, pageSize);
        return JsonResult.ok(videoList);
    }

    @PostMapping("saveComment")
    public JsonResult saveComment(@RequestBody Comments comment,String fatherCommentId,String toUserId) {

        if (StringUtils.isNotBlank(fatherCommentId) && StringUtils.isNotBlank(toUserId)) {
            comment.setFatherCommentId(fatherCommentId);
            comment.setToUserId(toUserId);
        }
        videoService.saveComment(comment);
        return JsonResult.ok();
    }

    @PostMapping("getVideoComment")
    public JsonResult getVideoComments(String videoId,Integer page,Integer pageSize) {
        if(StringUtils.isBlank(videoId)) {
            return JsonResult.errorMsg("");
        }

        //分页查询留言列表，按时间倒序排序
        if(page == null) {
            page = 1;
        }

        if(pageSize == null) {
            pageSize = 10;
        }

        PagedResult list = videoService.getAllComments(videoId,page,pageSize);
        return JsonResult.ok(list);
    }

}
