package com.inso.core.controller.seckillGoods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.pojo.seckill.SeckillGoods;
import com.inso.core.service.seckillGoods.SeckillGoodsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("seckillGoods")
public class SeckillGoodsController {

    @Reference
    private SeckillGoodsService seckillGoodsService;

    /**
     * 查询秒杀商品列表
     * @return
     */
    @RequestMapping("findList.do")
    public List<SeckillGoods> findList() {

        return seckillGoodsService.findList();
    }

    /**
     * 商品详情页,根据id获取商品(从缓存中获取)
     * @param id
     * @return
     */
    @RequestMapping("findOneFromRedis.do")
    public SeckillGoods findOneFromRedis(Long id) {
        return seckillGoodsService.findOne(id);
    }
}
