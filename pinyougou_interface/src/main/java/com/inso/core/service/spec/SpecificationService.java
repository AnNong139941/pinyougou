package com.inso.core.service.spec;

import com.inso.core.entity.PageResult;
import com.inso.core.pojo.specification.Specification;
import com.inso.core.vo.SpecificationVo;

import java.util.List;
import java.util.Map;

public interface SpecificationService {

    /**
     * 列表展示(查询所有以及搜索框功能)
     *
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    public PageResult search(Integer page, Integer rows, Specification specification);

    /**
     * 添加规格及规格选项
     *
     * @param specificationVo
     */
    void add(SpecificationVo specificationVo);

    /**
     * 修改之数据回显(主键查询)
     *
     * @param id
     * @return
     */
    SpecificationVo findOne(Long id);

    /**
     * 修改操作
     *
     * @param specificationVo
     */
    void update(SpecificationVo specificationVo);

    /**
     * 批量删除
     * @param ids
     */
    void delete(Long[] ids);

    /**
     * 模板管理编辑之下拉数据显示
     * @return
     */
    List<Map<String,String>> selectOptionList();

}
