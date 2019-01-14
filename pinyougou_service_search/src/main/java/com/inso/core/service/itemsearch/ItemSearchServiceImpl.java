package com.inso.core.service.itemsearch;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.inso.core.dao.item.ItemDao;
import com.inso.core.pojo.item.Item;
import com.inso.core.pojo.item.ItemQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.solr.core.SolrTemplate;
import org.springframework.data.solr.core.query.*;
import org.springframework.data.solr.core.query.result.*;

import javax.annotation.Resource;
import java.util.*;


@Service
public class ItemSearchServiceImpl implements ItemSearchService {

    @Resource
    private SolrTemplate solrTemplate;

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private ItemDao itemDao;

    @Override
    public Map<String, Object> search(Map<String, String> searchMap) {
        //1.商品结果集
        Map<String, Object> resultMap = new HashMap<>();

        //2.处理关键字,去除搜索时关键字中间的空格,前后的空格前端以及实现
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)) {
            keywords = keywords.replace(" ", ""); //以空格切割,替换成空字符串
            resultMap.put("keywords", keywords);
        }
        //封装搜索条件
        //Map<String, Object> map = searchForPage(searchMap);
        Map<String, Object> map = searchForHighlightPage(searchMap);

        resultMap.putAll(map);

        //2.商品分类列表 分组查询 categoryList
        List<String> categoryList = searchForGroupPage(searchMap);
        if (categoryList != null && categoryList.size() > 0) {
            //3.加载第一个分类下的品牌以及规格
            Map<String, Object> brandAndSpecMap = searchBrandAndSpecByCategory(categoryList.get(0));
            resultMap.putAll(brandAndSpecMap);
            resultMap.put("categoryList", categoryList);
        }
        return resultMap;

    }

    /**
     * 将商品信息保存到索引库
     *
     * @param id
     */
    @Override
    public void updateToSolr(Long id) {
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

    //封装第一个分类下的品牌以及规格
    private Map<String, Object> searchBrandAndSpecByCategory(String categoryName) {

        HashMap<String, Object> map = new HashMap<>();
        //从缓存中取出模板id 根据分类id
        Object typeId = redisTemplate.boundHashOps("itemCat").get(categoryName);

        //从缓存中取出品牌信息 根据模板id
        List<Map> brandList = (List<Map>) redisTemplate.boundHashOps("brandList").get(typeId);
        map.put("brandList", brandList);
        //从缓存中取出规格信息 根据模板id
        List<Map> specList = (List<Map>) redisTemplate.boundHashOps("specList").get(typeId);
        map.put("specList", specList);
        return map;
    }

    private List<String> searchForGroupPage(Map<String, String> searchMap) {
        //1.设置关键字
        String keywords = searchMap.get("keywords");

        Criteria criteria = new Criteria("item_keywords");
        if (keywords != null && !"".equals(keywords)) {
            criteria.is(keywords);
        }

        SimpleQuery query = new SimpleQuery(criteria);

        //2.设置分组
        GroupOptions groupOptions = new GroupOptions();
        //2.1分页条件
        groupOptions.addGroupByField("item_category");
        query.setGroupOptions(groupOptions);
        GroupPage<Item> groupPage = solrTemplate.queryForGroupPage(query, Item.class);
        //3.封装分组条件
        //3.1 获得分页结果
        ArrayList<String> list = new ArrayList<>();
        GroupResult<Item> groupResult = groupPage.getGroupResult("item_category");
        Page<GroupEntry<Item>> groupEntries = groupResult.getGroupEntries();
        for (GroupEntry<Item> groupEntry : groupEntries) {
            String value = groupEntry.getGroupValue();
            list.add(value);
        }
        return list;
    }

    /**
     * 关键字搜索高亮并分页
     *
     * @param searchMap
     * @return
     */
    private Map<String, Object> searchForHighlightPage(Map<String, String> searchMap) {


        //1.设置检索条件
        Criteria criteria = new Criteria("item_keywords");
        String keywords = searchMap.get("keywords");
        if (keywords != null && !"".equals(keywords)) {
            criteria.is(keywords);
        }


        SimpleHighlightQuery query = new SimpleHighlightQuery(criteria);

        //2.设置分页条件
        Integer pageNo = Integer.valueOf(searchMap.get("pageNo"));
        Integer pageSize = Integer.valueOf(searchMap.get("pageSize"));
        //起始行=(页码-1)*每页个数
        Integer offset = (pageNo - 1) * pageSize;
        query.setOffset(offset);   //起始行
        query.setRows(pageSize);    //每页显示个数


        //3.关键字高亮
        HighlightOptions highlightOptions = new HighlightOptions();
        //高亮就是给检索的关键字加样式
        highlightOptions.setSimplePrefix("<font color='red'>");   //添加样式的开始
        highlightOptions.setSimplePostfix("</font>");           //结尾
        highlightOptions.addField("item_title");                //如果该字段包含关键字就高亮
        query.setHighlightOptions(highlightOptions);    //设置高亮

        //设置条件过滤
        //商品分类
        if (searchMap.get("category") != null && !"".equals(searchMap.get("category"))) {

            Criteria cri = new Criteria("item_category");
            cri.is(searchMap.get("category"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
            query.addFilterQuery(filterQuery);
        }

        //商品品牌
        if (searchMap.get("brand") != null && !"".equals(searchMap.get("brand"))) {

            Criteria cri = new Criteria("item_brand");
            cri.is(searchMap.get("brand"));
            SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
            query.addFilterQuery(filterQuery);
        }

        //商品规格
        String spec = searchMap.get("spec");
        if (spec != null && !"".equals(spec)) {
            // 栗子：{"机身内存":"16G","网络":"联通3G"}
            // item_spec_*：  item_spec_机身内存:16G
            Map<String, String> specMap = JSON.parseObject(spec, Map.class);
            Set<Map.Entry<String, String>> entrySet = specMap.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                Criteria cri = new Criteria("item_spec_" + entry.getKey());
                cri.is(entry.getValue());
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
                query.addFilterQuery(filterQuery);
            }

        }

        // 商品价格
        String price = searchMap.get("price");
        if (price != null && !"".equals(price)) {
            // 传递的价格：区间段  min-max  xxx以上 min-*
            String[] prices = price.split("-");
            if (price.contains("*")) { // xxx以上
                Criteria cri = new Criteria("item_price");
                cri.greaterThanEqual(prices[0]);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
                query.addFilterQuery(filterQuery);
            } else {
                Criteria cri = new Criteria("item_price");
                cri.between(prices[0], prices[1], true, true);
                SimpleFilterQuery filterQuery = new SimpleFilterQuery(cri);
                query.addFilterQuery(filterQuery);
            }
        }

        //关键字查询之排序
        if (searchMap.get("sort") != null && !"".equals(searchMap.get("sort"))) {
            if ("ASC".equals(searchMap.get("sort"))) {
                Sort sort = new Sort(Sort.Direction.ASC, "item_" + searchMap.get("sortField"));
                query.addSort(sort);
            } else {
                Sort sort = new Sort(Sort.Direction.DESC, "item_" + searchMap.get("sortField"));
                query.addSort(sort);
            }
        }


        //查询
        HighlightPage<Item> highlightPage = solrTemplate.queryForHighlightPage(query, Item.class);

        //将高亮结果重新设置给普通title上
        List<HighlightEntry<Item>> highlighted = highlightPage.getHighlighted();

        if (highlighted != null && highlighted.size() > 0) {
            for (HighlightEntry<Item> itemHighlightEntry : highlighted) {
                Item item = itemHighlightEntry.getEntity();       //普通的title(没有高亮)
                List<HighlightEntry.Highlight> highlights = itemHighlightEntry.getHighlights(); //已经高亮了的结果集
                if (highlights != null && highlights.size() > 0) {
                    String title = highlights.get(0).getSnipplets().get(0);
                    item.setTitle(title);
                }
            }
        }

        //
        //4.封装结果并返回
        HashMap<String, Object> map = new HashMap<>();
        //总页数
        map.put("totalPages", highlightPage.getTotalPages());
        //总条数
        map.put("total", highlightPage.getTotalElements());
        //结果集
        map.put("rows", highlightPage.getContent());
        return map;

    }
    /**
     * 删除索引库中的商品信息
     * @param id
     */
    public void deleteItemFormSolr(Long id) {
        SimpleQuery simpleQuery = new SimpleQuery("item_goodsid:" + id);
        solrTemplate.delete(simpleQuery);
        solrTemplate.commit();

    }

}
