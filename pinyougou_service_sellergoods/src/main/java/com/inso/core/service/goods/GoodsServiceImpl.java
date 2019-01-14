package com.inso.core.service.goods;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.inso.core.dao.good.BrandDao;
import com.inso.core.dao.good.GoodsDao;
import com.inso.core.dao.good.GoodsDescDao;
import com.inso.core.dao.item.ItemCatDao;
import com.inso.core.dao.item.ItemDao;
import com.inso.core.dao.seller.SellerDao;
import com.inso.core.entity.PageResult;
import com.inso.core.pojo.good.Goods;
import com.inso.core.pojo.good.GoodsDesc;
import com.inso.core.pojo.good.GoodsQuery;
import com.inso.core.pojo.item.Item;
import com.inso.core.pojo.item.ItemCat;
import com.inso.core.pojo.item.ItemQuery;
import com.inso.core.vo.GoodsVo;
import org.apache.activemq.command.ActiveMQTextMessage;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.SimpleQuery;
import org.springframework.data.solr.repository.SelectiveStats;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.jms.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class GoodsServiceImpl implements GoodsService {

    @Resource
    private GoodsDescDao goodsDescDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private BrandDao brandDao;

    @Resource
    private SellerDao sellerDao;

    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private JmsTemplate jmsTemplate;

    @Resource
    private Destination topicPageAndSolrDestination;

    @Resource
    private Destination queueSolrDeleteDestination;

    @Transactional
    @Override
    public void add(GoodsVo goodsVo) {

        //1.保存商品信息
        Goods goods = goodsVo.getGoods();
        //设置审核状态 返回自增主键
        goods.setAuditStatus("0");
        //返回自增主键
        goodsDao.insertSelective(goods);


        //2.保存商品详细信息
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        //设置外键
        goodsDesc.setGoodsId(goods.getId());
        goodsDescDao.insertSelective(goodsDesc);

        //3.保存库存信息

        //判断是否启用规格
        if ("1".equals(goods.getIsEnableSpec())) {
            //启用规格 一对多
            List<Item> itemList = goodsVo.getItemList();
            if (itemList != null && itemList.size() > 0) {
                for (Item item : itemList) {
                    //设置标题:spu+spu副标题+规格名称
                    String title = goods.getGoodsName() + " " + goods.getCaption();
                    //规格名称
                    // {"机身内存":"16G","网络":"联通3G"}
                    Map<String, String> map = JSON.parseObject(item.getSpec(), Map.class);
                    Set<Map.Entry<String, String>> entrySet = map.entrySet();
                    for (Map.Entry<String, String> entry : entrySet) {
                        //规格名称
                        title += " " + entry.getValue();

                    }
                    item.setTitle(title);

                    setAttributeForItem(item, goods, goodsDesc);

                    itemDao.insertSelective(item);


                }
            }


        } else {
            //未启用一对一
            Item item = new Item();
            item.setTitle(goods.getGoodsName() + " " + goods.getCaption());
            item.setIsDefault("1");
            item.setPrice(goods.getPrice());
            item.setSpec("{}");
            setAttributeForItem(item, goods, goodsDesc);
            itemDao.insertSelective(item);
        }


    }

    /**
     * 商品的列表展示
     * 需是当前商家下的商品列表
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @Override
    public PageResult search(int page, int rows, Goods goods) {
        //设置 分页条件
        PageHelper.startPage(page, rows);
        //设置查询条件
        GoodsQuery goodsQuery = new GoodsQuery();
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();
        if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus())) {
            criteria.andAuditStatusEqualTo(goods.getAuditStatus());
        }
        if (goods.getGoodsName() != null && !"".equals(goods.getGoodsName())) {
            criteria.andGoodsNameLike("%" + goods.getGoodsName() + "%");
        }
        //System.out.println(goods.getSellerId());
        if (goods.getSellerId().trim() != null && !"".equals(goods.getSellerId().trim())) {
            criteria.andSellerIdEqualTo(goods.getSellerId().trim());
        }

        goodsQuery.setOrderByClause("id desc"); // 根据id降序
        //查询
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);

        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 修改之数据回显
     *
     * @param id
     * @return
     */
    @Override
    public GoodsVo findOne(Long id) {

        GoodsVo goodsVo = new GoodsVo();

        Goods goods = goodsDao.selectByPrimaryKey(id);

        //封装商品
        goodsVo.setGoods(goods);
        //封装商品扩展
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        goodsVo.setGoodsDesc(goodsDesc);

        //封装库存信息
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id);

        List<Item> itemList = itemDao.selectByExample(itemQuery);
        goodsVo.setItemList(itemList);
        return goodsVo;

    }

    /**
     * 更新商品
     *
     * @param goodsVo
     */
    @Transactional
    @Override
    public void update(GoodsVo goodsVo) {

        //更新商品
        Goods goods = goodsVo.getGoods();
        //修改后需重新审核
        goods.setAuditStatus("0");
        goodsDao.updateByPrimaryKeySelective(goods);
        //更新商品扩展
        GoodsDesc goodsDesc = goodsVo.getGoodsDesc();
        goodsDescDao.updateByPrimaryKeySelective(goodsDesc);

        //更新库存  先删除再插入
        List<Item> itemList = goodsVo.getItemList();
        ItemQuery itemQuery = new ItemQuery();
        ItemQuery.Criteria criteria = itemQuery.createCriteria();
        criteria.andGoodsIdEqualTo(goods.getId());

        //删除
        itemDao.deleteByExample(itemQuery);

        //添加
        //判断是否启用规格
        if ("1".equals(goods.getIsEnableSpec())) {
            //启用规格 一对多
            if (itemList != null && itemList.size() > 0) {
                for (Item item : itemList) {
                    //设置标题:spu+spu副标题+规格名称
                    String title = goods.getGoodsName() + " " + goods.getCaption();
                    //规格名称
                    // {"机身内存":"16G","网络":"联通3G"}
                    Map<String, String> map = JSON.parseObject(item.getSpec(), Map.class);
                    Set<Map.Entry<String, String>> entrySet = map.entrySet();
                    for (Map.Entry<String, String> entry : entrySet) {
                        //规格名称
                        title += " " + entry.getValue();

                    }
                    item.setTitle(title);

                    setAttributeForItem(item, goods, goodsDesc);

                    itemDao.insertSelective(item);


                }
            }


        } else {
            //未启用一对一
            Item item = new Item();
            item.setTitle(goods.getGoodsName() + " " + goods.getCaption());
            item.setIsDefault("1");
            item.setPrice(goods.getPrice());
            item.setSpec("{}");
            setAttributeForItem(item, goods, goodsDesc);
            itemDao.insertSelective(item);
        }


    }

    /**
     * 运营商查询待审核商品列表
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @Override
    public PageResult searchForManager(int page, int rows, Goods goods) {
        //设置分页条件
        PageHelper.startPage(page, rows);
        //设置查询条件 1.待审核 2.未删除
        GoodsQuery goodsQuery = new GoodsQuery();
        //降序
        goodsQuery.setOrderByClause("id desc");
        GoodsQuery.Criteria criteria = goodsQuery.createCriteria();

        if (goods.getAuditStatus() != null && !"".equals(goods.getAuditStatus())) {

            //查询的是待审核的
            criteria.andAuditStatusEqualTo(goods.getAuditStatus());
        }

        //未删除的
        criteria.andIsDeleteIsNull();

        //查询
        Page<Goods> p = (Page<Goods>) goodsDao.selectByExample(goodsQuery);

        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 商品审核
     *
     * @param ids
     * @param status
     */
    @Transactional
    @Override
    public void updateStatus(Long[] ids, String status) {

        if (ids != null && ids.length > 0) {
            Goods goods = new Goods();
            //审核状态
            goods.setAuditStatus(status);

            for (final Long id : ids) {
                goods.setId(id);

                //修改
                goodsDao.updateByPrimaryKeySelective(goods);
                if ("1".equals(status)) {
                    System.out.println("商品id: "+id);
                    //审核通过
                    //2. 将商品保存到索引库 本次将所有商品导入索引库
                    //dataImportToSolr();
                    SelectiveDataImportToSolr(id);
                    // 3.生成该商品详情的静态页
                    //将消息(商品id)发送到mq中
                    jmsTemplate.send(topicPageAndSolrDestination, new MessageCreator() {
                        @Override
                        public Message createMessage(Session session) throws JMSException {
                            //将商品id封装成消息体 文本消息发送到mq以供生成索引以及静态页使用
                            TextMessage textMessage = session.createTextMessage(String.valueOf(id));
                            return textMessage;
                        }
                    });
                }
            }
        }
    }

    //将数据保存到索引库(只是审核通过的商品)
    private void SelectiveDataImportToSolr(Long id) {
        ItemQuery query = new ItemQuery();
        query.createCriteria().andGoodsIdEqualTo(id).andStatusEqualTo("1").andIsDefaultEqualTo("1");
        List<Item> items = itemDao.selectByExample(query);
        if (items != null && items.size() > 0) {
            for (Item item : items) {
                //处理动态字段
                //数据库中拿到的是JSON格式 而我们定义的是Map
                String spec = item.getSpec();
                Map<String, String> map = JSON.parseObject(spec, Map.class);

                //重新设置
                item.setSpecMap(map);

            }
            //导入到索引库
            solrTemplate.saveBeans(items);
            solrTemplate.commit();
        }
    }


    //将数据库数据导入索引库(所有)
    private void dataImportToSolr() {
        //查询所有sku
        ItemQuery itemQuery = new ItemQuery();
        //将商家的商品查询出来
        itemQuery.createCriteria().andStatusEqualTo("1");

        List<Item> items = itemDao.selectByExample(itemQuery);
        if (items != null && items.size() > 0) {
            for (Item item : items) {
                //处理动态字段
                //数据库中拿到的是JSON格式 而我们定义的是Map
                String spec = item.getSpec();
                Map<String, String> map = JSON.parseObject(spec, Map.class);

                //重新设置
                item.setSpecMap(map);

                //导入到索引库
                solrTemplate.saveBeans(items);
                solrTemplate.commit();
            }
        }

    }

    /**
     * 逻辑删除：更新状态（is_delete  默认值：null  删除的状态：1）
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
        if (ids != null && ids.length > 0) {
            Goods goods = new Goods();
            //修改删除状态
            goods.setIsDelete("1");
            //更新
            for (final Long id : ids) {
                goods.setId(id);

                goodsDao.updateByPrimaryKeySelective(goods);
                // 删除索引库信息
                //参数格式:  *:*
                /*SimpleQuery query = new SimpleQuery("item_goodsid:" + id);
                solrTemplate.delete(query);
                solrTemplate.commit();*/

                //删除索引库操作交给service-search  发送消息
                jmsTemplate.send(queueSolrDeleteDestination, new MessageCreator() {
                    @Override
                    public Message createMessage(Session session) throws JMSException {

                        TextMessage textMessage = session.createTextMessage(String.valueOf(id));

                        return textMessage;

                    }
                });
                //TODO 删除商品详情页(可选),不删除可用于历史订单查看

            }
        }
    }

    /**
     * 设置公共的属性(保存)
     *
     * @param item
     * @param goods
     * @param goodsDesc
     */
    private void setAttributeForItem(Item item, Goods goods, GoodsDesc goodsDesc) {

        //设置商品图片
        String itemImages = goodsDesc.getItemImages();
        //[{"color":"粉色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXq2AFIs5AAgawLS1G5Y004.jpg"},
        // {"color":"黑色","url":"http://192.168.25.133/group1/M00/00/00/wKgZhVmOXrWAcIsOAAETwD7A1Is874.jpg"}]
        List<Map> images = JSON.parseArray(itemImages, Map.class);
        if (images != null && images.size() > 0) {
            //只取出第一张即可
            String image = images.get(0).get("url").toString();
            item.setImage(image);

        }
        //设置三级分类id
        item.setCategoryid(goods.getCategory3Id());
        //设置商品状态  商品状态，1-正常，2-下架，3-删除
        item.setStatus("1");
        //创建时间
        item.setCreateTime(new Date());
        //更新时间
        item.setUpdateTime(new Date());
        //商品id
        item.setGoodsId(goods.getId());
        //商家id
        item.setSellerId(goods.getSellerId());
        //商品三级分类名称
        item.setCategory(itemCatDao.selectByPrimaryKey(goods.getCategory3Id()).getName());
        //品牌名称
        item.setBrand(brandDao.selectByPrimaryKey(goods.getBrandId()).getName());
        //店铺名称
        item.setSeller(sellerDao.selectByPrimaryKey(goods.getSellerId()).getNickName());
    }


}
