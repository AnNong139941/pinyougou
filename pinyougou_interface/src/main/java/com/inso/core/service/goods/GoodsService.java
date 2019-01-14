package com.inso.core.service.goods;

import com.inso.core.entity.PageResult;
import com.inso.core.pojo.good.Goods;
import com.inso.core.vo.GoodsVo;

public interface GoodsService {

    /**
     * 添加商品
     *
     * @param goodsVo
     */
    void add(GoodsVo goodsVo);

    /**
     * 商品的列表展示以及搜索功能
     * 需是当前商家下的商品列表
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    PageResult search(int page, int rows, Goods goods);

    /**
     * 修改之数据回显
     *
     * @param id
     * @return
     */
    GoodsVo findOne(Long id);

    /**
     * 更新商品
     *
     * @param goodsVo
     */
    void update(GoodsVo goodsVo);

    /**
     * 商品审核
     * 运营商查询待审核商品列表
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    PageResult searchForManager(int page, int rows, Goods goods);

    /**
     * 商品审核
     *
     * @param ids
     * @param status
     */
    void updateStatus(Long[] ids, String status);

    /**
     * 逻辑删除：更新状态（is_delete  默认值：null  删除的状态：1）
     *
     * @param ids
     */
    void delete(Long[] ids);
}
