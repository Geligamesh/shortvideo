package com.gxb.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.gxb.mapper.*;
import com.gxb.pojo.Comments;
import com.gxb.pojo.SearchRecords;
import com.gxb.pojo.UsersLikeVideos;
import com.gxb.pojo.Videos;
import com.gxb.pojo.vo.CommentsVO;
import com.gxb.pojo.vo.VideosVO;
import com.gxb.service.VideoService;
import com.gxb.utils.PagedResult;
import com.gxb.utils.TimeAgoUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {

    @Autowired
    private VideosMapper videosMapper;
    @Autowired
    private VideosMapperCustom videosMapperCustom;
    @Autowired
    private Sid sid;
    @Autowired
    private SearchRecordsMapper searchRecordsMapper;
    @Autowired
    private UsersLikeVideosMapper UsersLikeVideosMapper;
    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private CommentsMapper commentsMapper;
    @Autowired
    private CommentsMapperCustom commentsMapperCustom;

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveVideo(Videos video) {
        video.setId(sid.nextShort());
        videosMapper.insertSelective(video);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateVideo(String videoId, String coverPath) {
        Videos video = new Videos();
        video.setId(videoId);
        video.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(video);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public PagedResult getAllVideos(Videos video, Integer isSaveRecord,Integer page, Integer pageSize) {

        //保存热搜词
        String desc = video.getVideoDesc();
        String userId = video.getUserId();
        if(isSaveRecord != null && isSaveRecord == 1) {
            SearchRecords record = new SearchRecords();
            String recordId = sid.nextShort();
            record.setId(recordId);
            record.setContent(desc);
            searchRecordsMapper.insert(record);
        }

        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryAllVideos(desc,userId);
        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotwords();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void userLikeVideo(String userId, String videoId, String videoCreatorId) {
        //保存用户和视频的喜欢点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos usersLikeVideos = new UsersLikeVideos();
        usersLikeVideos.setId(likeId);
        usersLikeVideos.setUserId(userId);
        usersLikeVideos.setVideoId(videoId);
        UsersLikeVideosMapper.insert(usersLikeVideos);

        //视频喜欢数量累加
        videosMapperCustom.addVideoLikeCount(videoId);
        //用户受喜欢数量的累加
        usersMapper.addReceiveLikeCount(videoCreatorId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void userUnlikeVideo(String userId, String videoId, String videoCreatorId) {
        //删除用户和视频的喜欢点赞关联关系表
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);
        UsersLikeVideosMapper.deleteByExample(example);

        //视频喜欢数量累减
        videosMapperCustom.reduceVideoLikeCount(videoId);
        //用户受喜欢数量的减
        usersMapper.reduceReceiveLikeCount(videoCreatorId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {

        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyLikeVideos(userId);
        PageInfo<VideosVO> pageInfo = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageInfo.getTotal());

        return pagedResult;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<VideosVO> list = videosMapperCustom.queryMyFollowVideos(userId);
        PageInfo<VideosVO> pageInfo = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageInfo.getPages());
        pagedResult.setRecords(pageInfo.getTotal());
        pagedResult.setRows(list);
        pagedResult.setPage(page);

        return pagedResult;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveComment(Comments comment) {
        String id = sid.nextShort();
        comment.setId(id);
        comment.setCreateTime(new Date());
        commentsMapper.insert(comment);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {
        PageHelper.startPage(page, pageSize);
        List<CommentsVO> list = commentsMapperCustom.queryComments(videoId);
        for (CommentsVO commentsVO : list) {
            String timeAgo = TimeAgoUtils.format(commentsVO.getCreateTime());
            commentsVO.setTimeAgoStr(timeAgo);
        }
        PageInfo<CommentsVO> pageInfo = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(page);
        pagedResult.setRows(list);
        pagedResult.setRecords(pageInfo.getTotal());
        pagedResult.setTotal(pageInfo.getPages());

        return pagedResult;
    }
}
