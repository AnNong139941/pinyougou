package com.inso.core.service.seckillGoods;

import com.inso.core.pojo.seckill.SeckillGoods;

import java.util.List;

public interface SeckillGoodsService {

    /**
     * ���ص�ǰ���ڲ�����ɱ����Ʒ
     *
     * @return
     */
    List<SeckillGoods> findList();

    /**
     * ��Ʒ����ҳ,����id��ȡ��Ʒ(�ӻ����л�ȡ)
     *
     * @param id
     * @return
     */
    SeckillGoods findOne(Long id);
}
