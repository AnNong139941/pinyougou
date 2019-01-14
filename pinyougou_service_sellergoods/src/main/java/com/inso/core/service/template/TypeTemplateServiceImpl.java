package com.inso.core.service.template;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.inso.core.dao.specification.SpecificationOptionDao;
import com.inso.core.dao.template.TypeTemplateDao;
import com.inso.core.entity.PageResult;
import com.inso.core.pojo.specification.SpecificationOption;
import com.inso.core.pojo.specification.SpecificationOptionQuery;
import com.inso.core.pojo.template.TypeTemplate;
import com.inso.core.pojo.template.TypeTemplateQuery;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class TypeTemplateServiceImpl implements TypeTemplateService {

    @Resource
    private TypeTemplateDao typeTemplateDao;

    @Resource
    private RedisTemplate redisTemplate;
    @Resource
    private SpecificationOptionDao specificationOptionDao;

    /**
     * 列别展示以及搜索框功能
     * 当查询模板时将所有模板保存到reids中
     *
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, TypeTemplate typeTemplate) {

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


        //设置分页条件
        PageHelper.startPage(page, rows);

        TypeTemplateQuery typeTemplateQuery = new TypeTemplateQuery();
        //不为空 则执行搜索功能
        if (typeTemplate.getName() != null && !"".equals(typeTemplate.getName().trim())) {
            //封装查询条件
            TypeTemplateQuery.Criteria criteria = typeTemplateQuery.createCriteria();

            criteria.andNameLike("%" + typeTemplate.getName() + "%");

        }
        //根据主键降序
        typeTemplateQuery.setOrderByClause("id desc");
        //查询
        Page<TypeTemplate> p = (Page<TypeTemplate>) typeTemplateDao.selectByExample(typeTemplateQuery);

        return new PageResult(p.getTotal(), p.getResult());
    }

    /**
     * 新增
     *
     * @param typeTemplate
     */
    @Transactional
    @Override
    public void add(TypeTemplate typeTemplate) {
        typeTemplateDao.insertSelective(typeTemplate);
    }

    /**
     * 更新之数据回显
     *
     * @param id
     * @return
     */

    @Override
    public TypeTemplate findOne(Long id) {

        return typeTemplateDao.selectByPrimaryKey(id);
    }

    /**
     * 更新
     *
     * @param typeTemplate
     */
    @Transactional
    @Override
    public void update(TypeTemplate typeTemplate) {

        typeTemplateDao.updateByPrimaryKeySelective(typeTemplate);
    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {

        if (ids != null && ids.length > 0) {
            //自定义批量删除的方法
            typeTemplateDao.deleteByPrimaryKeys(ids);
        }
    }

    /**
     * 根据模板id获取规格以及规格选项
     *
     * @param id
     * @return
     */
    @Override
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

        // list:[{"id":27,"text":"网络","options":[{},{}...]}]
        return specList;
    }


}
