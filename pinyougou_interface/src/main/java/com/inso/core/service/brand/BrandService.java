package com.inso.core.service.brand;

import com.inso.core.entity.PageResult;
import com.inso.core.pojo.good.Brand;

import java.util.List;
import java.util.Map;

public interface BrandService {

    /**
     * 查询所有品牌
     * @return
     */
    public List<Brand> findAll();

    /***
     * 查询所有品牌并分页
     * @param pageNum
     * @param pageSize
     * @return
     */
    public PageResult findPage(Integer pageNum,Integer pageSize);

    /**
     * 条件查询
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */

    PageResult findCondition(Integer pageNum, Integer pageSize, Brand brand);

    /**
     * 新增品牌信息
     * @param brand
     */
    void add(Brand brand);

    /**
     * 修改品牌之数据回显
     * @param id
     * @return
     */
    Brand findOne(Long id);

    /**
     * 修改品牌
     * @param brand
     */
    void update(Brand brand);

    /**
     * 批量删除品牌
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 模板管理中品牌下拉列表数据
     * @return
     */
    List<Map<String,String>> selectOptionList();

}
