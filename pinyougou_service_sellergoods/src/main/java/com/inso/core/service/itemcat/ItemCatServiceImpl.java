package com.inso.core.service.itemcat;

import com.alibaba.dubbo.config.annotation.Service;
import com.inso.core.dao.item.ItemCatDao;
import com.inso.core.pojo.item.ItemCat;
import com.inso.core.pojo.item.ItemCatQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class ItemCatServiceImpl implements ItemCatService {

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 根据上级id查询列表
     *当查询商品分类时将所有分类保存到redis中(交给定时器)。 分类名称---模板id。
     * @param parentId
     * @return
     */
    @Override
    public List<ItemCat> findParentId(Long parentId) {

        //查询所有的库存分类,放入到缓存中
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        if (itemCatList != null && itemCatList.size() > 0) {
            for (ItemCat itemCat : itemCatList) {
                //根据分类名称获得模板id
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
            }
        }

        //封装查询条件
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        itemCatQuery.createCriteria().andParentIdEqualTo(parentId);
        return itemCatDao.selectByExample(itemCatQuery);
    }

    /**
     * 修改之数据回显
     *
     * @param id
     * @return
     */
    @Override
    public ItemCat findOne(Long id) {
        return itemCatDao.selectByPrimaryKey(id);

    }

    @Override
    public void update(ItemCat itemCat) {
        itemCatDao.updateByPrimaryKeySelective(itemCat);
    }

    /**
     * 新增分类
     *
     * @param itemCat
     */
    @Transactional
    @Override
    public void add(ItemCat itemCat) {

        itemCatDao.insertSelective(itemCat);
    }

    /**
     * 查询所有分类用于商品列表三级分类展示
     *
     * @return
     */
    @Override
    public List<ItemCat> findAll() {

        return itemCatDao.selectByExample(null);
    }




    /* *//**
     * 分页+查询
     *
     * @param page
     * @param rows
     * @param itemCat
     * @return
     *//*
    @Override
    public PageResult search(int page, int rows, ItemCat itemCat) {

        //设置分页条件
        PageHelper.startPage(page, rows);

        //设置分页条件
        ItemCatQuery itemCatQuery = new ItemCatQuery();
        ItemCatQuery.Criteria criteria = itemCatQuery.createCriteria();
        if (itemCat != null && !"".equals(itemCat)) {
            if (itemCat.getName() != null && !"".equals(itemCat.getName())) {
                criteria.andNameLike("%" + itemCat.getName() + "%");
            }

        }



        //查询
        Page<ItemCat> p = (Page<ItemCat>) itemCatDao.selectByExample(itemCatQuery);

        return new PageResult(p.getTotal(), p.getResult());
    }*/
}
