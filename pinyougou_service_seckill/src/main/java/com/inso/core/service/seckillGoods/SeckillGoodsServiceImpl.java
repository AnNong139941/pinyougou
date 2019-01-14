package com.inso.core.service.seckillGoods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.inso.core.dao.seckill.SeckillGoodsDao;
import com.inso.core.pojo.seckill.SeckillGoods;
import com.inso.core.pojo.seckill.SeckillGoodsQuery;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class SeckillGoodsServiceImpl implements SeckillGoodsService {

    @Resource
    private SeckillGoodsDao seckillGoodsDao;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 返回当前正在参与秒杀的商品
     *
     * @return
     */
    @Override
    public List<SeckillGoods> findList() {
        //从缓存中取出list
        List<SeckillGoods> seckillGoodsList = redisTemplate.boundHashOps("seckillGoods").values();
        if (seckillGoodsList == null || seckillGoodsList.size() == 0) {
            SeckillGoodsQuery query = new SeckillGoodsQuery();
            SeckillGoodsQuery.Criteria criteria = query.createCriteria();
            //1.审核通过的
            criteria.andStatusEqualTo("1");
            //2.开始时间小于当前时间的
            criteria.andStartTimeLessThanOrEqualTo(new Date());
            //3.结束时间大于当前时间的
            criteria.andEndTimeGreaterThan(new Date());
            //4.剩余库存数大于0的
            criteria.andStockCountGreaterThan(0);
            seckillGoodsList = seckillGoodsDao.selectByExample(query);

            //放入缓存
            for (SeckillGoods seckillGoods : seckillGoodsList) {

                System.out.println("将秒杀商品列表装入缓存");

                redisTemplate.boundHashOps("seckillGoods").put(seckillGoods.getId(), seckillGoods);
            }
        } else {
            System.out.println("从缓存查询");
        }
        return seckillGoodsList;


    }

    /**
     * 商品详情页,根据id获取商品(从缓存中获取)
     *
     * @param id
     * @return
     */
    @Override
    public SeckillGoods findOne(Long id) {
        return (SeckillGoods) redisTemplate.boundHashOps("seckillGoods").get(id);
    }
}
