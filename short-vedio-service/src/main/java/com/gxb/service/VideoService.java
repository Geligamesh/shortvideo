package com.gxb.service;

import com.gxb.pojo.Comments;
import com.gxb.pojo.Videos;
import com.gxb.utils.PagedResult;

import java.util.List;

public interface VideoService {

    /**
     * 保存视频
     * @param video
     */
    public void saveVideo(Videos video);

    /**
     * 修改视频的封面
     * @param videoId
     * @param coverPath
     * @return
     */
    public void updateVideo(String videoId,String coverPath);

    /**
     * 分页查询视频列表
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult getAllVideos(Videos video ,Integer isSaveRecode,Integer page,Integer pageSize);

    /**
     * 获取热搜词
     * @return
     */
    public List<String> getHotWords();

    /**
     * 用户喜欢视频/点赞
     * @param userId
     * @param videoId
     * @param videoCreatorId
     */
    public void userLikeVideo(String userId,String videoId,String videoCreatorId);

    /**
     * 用户不喜欢视频/取消点赞
     * @param userId
     * @param videoId
     * @param videoCreatorId
     */
    public void userUnlikeVideo(String userId,String videoId,String videoCreatorId);

    /**
     * 查询用户喜欢的视频
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyLikeVideos(String userId,Integer page,Integer pageSize);


    /**
     * 查詢用戶关注的视频
     * @param userId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult queryMyFollowVideos(String userId,Integer page,Integer pageSize);

    /**
     * 添加评价
     * @param comment
     */
    public void saveComment(Comments comment);

    /**
     * 留言分页
     * @param videoId
     * @param page
     * @param pageSize
     * @return
     */
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
}
