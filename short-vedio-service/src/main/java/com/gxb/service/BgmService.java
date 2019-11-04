package com.gxb.service;

import com.gxb.pojo.Bgm;

import java.util.List;

public interface BgmService {

    /**
     * 查询背景音乐列表
     * @return
     */
    public List<Bgm> queryBgmList();

    /**
     * 根据bgmId查询信息
     * @param bgmId
     * @return
     */
    public Bgm queryBgmById(String bgmId);
}
