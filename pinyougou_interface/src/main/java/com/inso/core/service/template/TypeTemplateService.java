package com.inso.core.service.template;

import com.inso.core.entity.PageResult;
import com.inso.core.pojo.template.TypeTemplate;

import java.util.List;
import java.util.Map;

public interface TypeTemplateService {


    /**
     * 条件查询及列表展示
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate);

    /**
     * 新增
     */
    void add(TypeTemplate typeTemplate) ;


    /**
     * 更新之主键查询
     * @param id
     * @return
     */
    TypeTemplate findOne(Long id);

    /**
     * 更新
     * @param typeTemplate
     */
    void update(TypeTemplate typeTemplate);

    /**
     * 批量删除
     * @param ids
     */
    void delete(Long[] ids);


    /**
     * 根据模板id获取规格以及规格选项
     * @param id
     * @return
     */
    public List<Map> findBySpecList(Long id);
}
