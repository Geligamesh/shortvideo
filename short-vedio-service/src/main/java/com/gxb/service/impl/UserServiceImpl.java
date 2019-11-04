package com.gxb.service.impl;

import com.gxb.mapper.UsersFansMapper;
import com.gxb.mapper.UsersLikeVideosMapper;
import com.gxb.mapper.UsersMapper;
import com.gxb.mapper.UsersReportMapper;
import com.gxb.pojo.Users;
import com.gxb.pojo.UsersFans;
import com.gxb.pojo.UsersLikeVideos;
import com.gxb.pojo.UsersReport;
import com.gxb.service.UserService;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UsersMapper usersMapper;
    @Autowired
    private Sid sid;
    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;
    @Autowired
    private UsersFansMapper usersFansMapper;
    @Autowired
    private UsersReportMapper usersReportMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUsernameIsExist(String username) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        Users result = usersMapper.selectOneByExample(example);
        return result == null ? false : true;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(Users user) {
        String userId = sid.nextShort();
        user.setId(userId);
        usersMapper.insert(user);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserForLogin(String username, String password) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("username", username);
        criteria.andEqualTo("password", password);
        Users result = usersMapper.selectOneByExample(example);
        return result;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUserInfo(Users user) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", user.getId());
        usersMapper.updateByExampleSelective(user, example);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users queryUserInfo(String userId) {
        Example example = new Example(Users.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("id", userId);
        Users user = usersMapper.selectOneByExample(example);
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean isUserLikeVideo(String userId, String videoId) {

        if (StringUtils.isBlank(userId) && StringUtils.isBlank(videoId)) {
            return false;
        }

        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("videoId", videoId);
        List<UsersLikeVideos> list = usersLikeVideosMapper.selectByExample(example);
        if (list != null && list.size() > 0 ) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUserFanRelation(String userId, String fanId) {

        UsersFans usersFan = new UsersFans();
        String id = sid.nextShort();
        usersFan.setId(id);
        usersFan.setUserId(userId);
        usersFan.setFanId(fanId);

        usersFansMapper.insert(usersFan);

        usersMapper.addFansCount(userId);
        usersMapper.addFollersCount(fanId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteUserFanRelation(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        usersFansMapper.deleteByExample(example);

        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFollersCount(fanId);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryIfFollow(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId",userId);
        criteria.andEqualTo("fanId",fanId);
        List<UsersFans> list = usersFansMapper.selectByExample(example);
        if (list.size() > 0 && list != null && !list.isEmpty()) {
            return true;
        }
        return false;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void reportUser(UsersReport usersReport) {
        String id = sid.nextShort();
        usersReport.setId(id);
        usersReport.setCreateDate(new Date());

        usersReportMapper.insert(usersReport);
    }
}
