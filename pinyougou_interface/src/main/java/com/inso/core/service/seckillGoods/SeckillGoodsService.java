package com.inso.core.service.seckillGoods;

import com.inso.core.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    /**
     * 返回当前正在参与秒杀的商品
     *
     * @return
     */
    List<SeckillGoods> findList();

    /**
     * 商品详情页,根据id获取商品(从缓存中获取)
     *
     * @param id
     * @return
     */
    SeckillGoods findOne(Long id);
}
