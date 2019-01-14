package com.inso.core.service.content;

import com.inso.core.entity.PageResult;
import com.inso.core.pojo.ad.Content;

import java.util.List;


public interface ContentService {

	public List<Content> findAll();

	public PageResult findPage(Content content, Integer pageNum, Integer pageSize);

	public void add(Content content);

	public void edit(Content content);

	public Content findOne(Long id);

	public void delAll(Long[] ids);

	//大广告轮播图
	List<Content> findByCategoryId(Long categoryId);
}
