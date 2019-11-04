package com.gxb.mapper;

import com.gxb.pojo.Comments;
import com.gxb.pojo.vo.CommentsVO;
import com.gxb.utils.MyMapper;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<Comments> {

    List<CommentsVO> queryComments(String videoId);
}