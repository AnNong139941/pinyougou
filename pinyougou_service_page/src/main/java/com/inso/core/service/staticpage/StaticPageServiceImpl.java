package com.inso.core.service.staticpage;

import com.inso.core.dao.good.GoodsDao;
import com.inso.core.dao.good.GoodsDescDao;
import com.inso.core.dao.item.ItemCatDao;
import com.inso.core.dao.item.ItemDao;
import com.inso.core.pojo.good.Goods;
import com.inso.core.pojo.good.GoodsDesc;
import com.inso.core.pojo.item.Item;
import com.inso.core.pojo.item.ItemCat;
import com.inso.core.pojo.item.ItemQuery;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StaticPageServiceImpl implements StaticPageService, ServletContextAware {

    @Resource
    private GoodsDao goodsDao;

    @Resource
    private GoodsDescDao goodsDescDao;

    @Resource
    private ItemDao itemDao;

    @Resource
    private ItemCatDao itemCatDao;

    /**
     * 注入freeMarkerConfigurer的好处:
     * 可以获取configuration
     * 可以指定模板位置
     */
    private Configuration configuration;

    public void setFreeMarkerConfigurer(FreeMarkerConfigurer freeMarkerConfigurer) {
        this.configuration = freeMarkerConfigurer.getConfiguration();
    }

    private ServletContext servletContext;

    @Override
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    /**
     * 生成商品详情的静态页
     *
     * @param id
     */
    @Override
    public void getHtml(Long id) {
        //1.创建Configuration并指定模板位置(配置文件注入)
        try {
            //2.获取模板
            Template template = configuration.getTemplate("item.ftl");
            //3.准备数据
            Map<String, Object> dataModel = getDataModel(id);
            //4.数据+模板=输出
            //指定静态页生成的位置：项目发布的真实的路径下
            String path = "/" + id + ".html";
            //注入ServletContext 获取当前根目录
            String realPath = servletContext.getRealPath(path);
            File file = new File(realPath);
            //和页面保持一致的编码格式 故使用OutputStreamWriter
            OutputStreamWriter writer = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
            template.process(dataModel, writer);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //静态页所需要的数据
    private Map<String, Object> getDataModel(Long id) {
        Map<String, Object> dataModel = new HashMap<String, Object>();
        //商品标题以及副标题
        Goods goods = goodsDao.selectByPrimaryKey(id);
        dataModel.put("goods", goods);
        //商品图片,包装,售后等信息
        GoodsDesc goodsDesc = goodsDescDao.selectByPrimaryKey(id);
        dataModel.put("goodsDesc", goodsDesc);
        //商品分类
        ItemCat itemCat1 = itemCatDao.selectByPrimaryKey(goods.getCategory1Id());
        ItemCat itemCat2 = itemCatDao.selectByPrimaryKey(goods.getCategory2Id());
        ItemCat itemCat3 = itemCatDao.selectByPrimaryKey(goods.getCategory3Id());
        dataModel.put("itemCat1", itemCat1);
        dataModel.put("itemCat2", itemCat2);
        dataModel.put("itemCat3", itemCat3);
        //商品对应库存
        ItemQuery itemQuery = new ItemQuery();
        itemQuery.createCriteria().andGoodsIdEqualTo(id).andNumGreaterThan(0);
        List<Item> itemList = itemDao.selectByExample(itemQuery);
        dataModel.put("itemList", itemList);

        return dataModel;
    }


}
