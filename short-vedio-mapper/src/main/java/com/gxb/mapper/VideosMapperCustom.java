package com.gxb.mapper;


import com.gxb.pojo.Videos;
import com.gxb.pojo.vo.VideosVO;
import com.gxb.utils.MyMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VideosMapperCustom extends MyMapper<Videos> {

    public List<VideosVO> queryAllVideos(@Param("videoDesc")String videoDesc,@Param("userId") String userId);

    /**
     * 对视频喜欢的数量进行累加
     * @param videoId
     */
    public void addVideoLikeCount(String videoId);

    /**
*    * 对视频喜欢的数量进行累减
     * @param videoId
     */
    public void reduceVideoLikeCount(String videoId);

    /**
     * 查询点赞视频
     * @param userId
     * @return
     */
    public List<VideosVO> queryMyLikeVideos(@Param("userId") String userId);

    /**
     * 查询关注的视频
     * @param userId
     * @return
     */
    public List<VideosVO> queryMyFollowVideos(String userId);
}