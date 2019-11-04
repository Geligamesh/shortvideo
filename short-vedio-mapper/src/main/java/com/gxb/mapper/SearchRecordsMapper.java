package com.gxb.mapper;

import com.gxb.pojo.SearchRecords;
import com.gxb.utils.MyMapper;

import java.util.List;



public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	
	public List<String> getHotwords();
}