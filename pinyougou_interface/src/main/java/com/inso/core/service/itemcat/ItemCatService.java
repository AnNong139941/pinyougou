package com.inso.core.service.itemcat;

import com.inso.core.pojo.item.ItemCat;

import java.util.List;

public interface ItemCatService {

    /**
     * 根据上级id查询列表
     *
     * @param parentId
     * @return
     */
    public List<ItemCat> findParentId(Long parentId);


    /**
     * 修改之数据回显
     *
     * @param id
     * @return
     */
    ItemCat findOne(Long id);

    /**
     * 修改
     *
     * @param itemCat
     */
    public void update(ItemCat itemCat);

    /**
     * 新增分类
     *
     * @param itemCat
     */
    void add(ItemCat itemCat);


    /**
     * 查询所有分类用于商品列表三级分类展示
     * @return
     */
    public List<ItemCat> findAll();






}
