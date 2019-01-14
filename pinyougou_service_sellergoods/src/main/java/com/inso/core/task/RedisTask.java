package com.inso.core.task;

import com.alibaba.fastjson.JSON;
import com.inso.core.dao.item.ItemCatDao;
import com.inso.core.dao.item.ItemDao;
import com.inso.core.dao.specification.SpecificationOptionDao;
import com.inso.core.dao.template.TypeTemplateDao;
import com.inso.core.pojo.item.ItemCat;
import com.inso.core.pojo.specification.SpecificationOption;
import com.inso.core.pojo.specification.SpecificationOptionQuery;
import com.inso.core.pojo.template.TypeTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 定时器:将商品分类,商品模板定时的放入Redis缓存中
 */
@Component
public class RedisTask {


    @Resource
    private ItemCatDao itemCatDao;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    //将商品分类同步到缓存
    //cron:年月日 时分秒(反序)
    @Scheduled(cron = "0 15 15 28 12 ?")
    public void autoToRedisFromItemCat() {
        //查询所有的库存分类,放入到缓存中
        List<ItemCat> itemCatList = itemCatDao.selectByExample(null);
        if (itemCatList != null && itemCatList.size() > 0) {
            for (ItemCat itemCat : itemCatList) {
                //根据分类名称获得模板id
                redisTemplate.boundHashOps("itemCat").put(itemCat.getName(), itemCat.getTypeId());
            }
        }
        System.out.println("商品分类-定时器执行啦... ...");
    }

    //将商品模板同步到缓存
    @Scheduled(cron = "0 15 15 28 12 ?")
    public void aotuToRedisFromTemplate() {
        //查询所有的模板 将数据放入缓存
        List<TypeTemplate> typeTemplateList = typeTemplateDao.selectByExample(null);
        if (typeTemplateList != null && typeTemplateList.size() > 0) {
            for (TypeTemplate typeTem : typeTemplateList) {
                //缓存该模板下的所有的品牌
                //String brandIds = template.getBrandIds(); //JSON转list<map>
                List<Map> brandList = JSON.parseArray(typeTem.getBrandIds(), Map.class);

                //放入缓存(根据模板id获取品牌)
                redisTemplate.boundHashOps("brandList").put(typeTem.getId(), brandList);

                //缓存该模板下的所有的规格(包含规格选项)
                List<Map> specList = findBySpecList(typeTem.getId());
                //放入缓存
                redisTemplate.boundHashOps("specList").put(typeTem.getId(), specList);
            }

        }
        System.out.println("商品模板定时器执行啦... ...");
    }

    public List<Map> findBySpecList(Long id) {

        TypeTemplate typeTemplate = typeTemplateDao.selectByPrimaryKey(id);

        //只需要规格信息
        String specIds = typeTemplate.getSpecIds();

        //将JSON串转为对象
        //  [{"id":27,"text":"网络"},{"id":32,"text":"机身内存"}]
        List<Map> specList = JSON.parseArray(specIds, Map.class);

        //根据规格id查询规格选项
        if (specList != null && specList.size() > 0) {
            for (Map map : specList) {
                //规格id
                String specID = map.get("id").toString();

                //通过规格id查询
                SpecificationOptionQuery optionQuery = new SpecificationOptionQuery();
                optionQuery.createCriteria().andSpecIdEqualTo(Long.valueOf(specID));

                List<SpecificationOption> options = specificationOptionDao.selectByExample(optionQuery);

                map.put("options", options);

            }
        }
        return specList;
    }
}
