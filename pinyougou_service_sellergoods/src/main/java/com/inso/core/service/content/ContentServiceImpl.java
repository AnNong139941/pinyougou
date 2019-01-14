package com.inso.core.service.content;

import java.util.List;

import com.inso.core.dao.ad.ContentDao;
import com.inso.core.entity.PageResult;
import com.inso.core.pojo.ad.Content;
import com.inso.core.pojo.ad.ContentQuery;
import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;


@Service
public class ContentServiceImpl implements ContentService {

    @Resource
    private ContentDao contentDao;
    @Resource
    private RedisTemplate redisTemplate;


    @Override
    public List<Content> findAll() {
        List<Content> list = contentDao.selectByExample(null);
        return list;
    }

    @Override
    public PageResult findPage(Content content, Integer pageNum, Integer pageSize) {
        PageHelper.startPage(pageNum, pageSize);
        Page<Content> page = (Page<Content>) contentDao.selectByExample(null);
        return new PageResult(page.getTotal(), page.getResult());
    }

    /**
     * 新增
     *
     * @param content
     */
    @Transactional
    @Override
    public void add(Content content) {
        //清理缓存 保证新增后获取到的是新数据
        clearCache(content.getCategoryId());
        contentDao.insertSelective(content);
    }

    /**
     * 修改 前后的缓存都需要清除
     *
     * @param content
     */
    @Transactional
    @Override
    public void edit(Content content) {
        //查询修改前的分类id 在数据库中
        Long oldCategoryId = contentDao.selectByPrimaryKey(content.getId()).getCategoryId();
        //页面传过来的最新的分类id
        Long newCategoryId = content.getCategoryId();
        //判断分类id是否发生改变
        if (oldCategoryId != newCategoryId) {
            //清理缓存
            clearCache(oldCategoryId);
            clearCache(newCategoryId);
        } else {
            //删除任意一个
            clearCache(oldCategoryId);
        }

        //查询
        contentDao.updateByPrimaryKeySelective(content);


    }

    @Override
    public Content findOne(Long id) {
        Content content = contentDao.selectByPrimaryKey(id);
        return content;
    }

    /**
     * 批量删除 删除后需清理缓存
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delAll(Long[] ids) {
        if (ids != null) {
            for (Long id : ids) {

                //查询分类id
                Long categoryId = contentDao.selectByPrimaryKey(id).getCategoryId();
                //清理缓存
                clearCache(categoryId);
                contentDao.deleteByPrimaryKey(id);
            }
        }
    }

    /**
     * 大广告轮播图 (Redis缓存机制)
     *
     * @param categoryId
     * @return
     */
    @Override
    public List<Content> findByCategoryId(Long categoryId) {

        //直接从缓存中取 如果有直接返回
        List<Content> contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
        //System.out.println(contentList);
        //如果缓存中没有
        if (contentList == null) {
            //解决高并发问题 此时十万用户都在这里等待 第一个人去数据库查完放到缓存,第二个人拿到锁就可以直接返回了(缓存中有)
            synchronized (this) {
                contentList = (List<Content>) redisTemplate.boundHashOps("content").get(categoryId);
                if (contentList == null) {

                    ContentQuery contentQuery = new ContentQuery();
                    ContentQuery.Criteria criteria = contentQuery.createCriteria();

                    criteria.andCategoryIdEqualTo(categoryId);
                    // 可用的广告：status=1
                    criteria.andStatusEqualTo("1");

                    //根据sort_order排序
                    contentQuery.setOrderByClause("sort_order");
                    //查询 获取广告列表
                    contentList = contentDao.selectByExample(contentQuery);

                    //放入缓存
                    redisTemplate.boundHashOps("content").put(categoryId, contentList);

                }
            }
        } else {
            System.out.println("直接从缓存中获取... ...");
        }
        //返回
        return contentList;
    }

    //清理缓存
    private void clearCache(Long categoryId) {
        redisTemplate.boundHashOps("content").delete(categoryId);
    }


}
