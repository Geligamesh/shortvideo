package com.gxb.service.impl;

import com.gxb.mapper.BgmMapper;
import com.gxb.pojo.Bgm;
import com.gxb.service.BgmService;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;
    @Autowired
    private Sid sid;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public List<Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Bgm queryBgmById(String bgmId) {
        return bgmMapper.selectByPrimaryKey(bgmId);
    }
}
