package com.inso.core.service.brand;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.inso.core.dao.good.BrandDao;
import com.inso.core.entity.PageResult;
import com.inso.core.pojo.good.Brand;
import com.inso.core.pojo.good.BrandQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
public class BrandServiceImpl implements BrandService {

    /**
     * 查询所有品牌
     */
    @Autowired
    private BrandDao brandDao;

    @Override
    public List<Brand> findAll() {
        return brandDao.selectByExample(null);
    }

    /**
     * 查询所有品牌并且分页
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        //设置分页助手分页条件
        PageHelper.startPage(pageNum, pageSize);

        //查询结果
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(null);

        //封装数据
        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());

        return pageResult;
    }

    /**
     * 条件查询
     *
     * @param pageNum
     * @param pageSize
     * @param brand
     * @return
     */
    @Override
    public PageResult findCondition(Integer pageNum, Integer pageSize, Brand brand) {
        //设置查询条件
        BrandQuery brandQuery = new BrandQuery();

        BrandQuery.Criteria criteria = brandQuery.createCriteria();
        //判空
        if (brand.getName() != null && !"".equals(brand.getName().trim())) {
            criteria.andNameLike("%" + brand.getName() + "%");
        }
        if (brand.getFirstChar() != null && !"".equals(brand.getFirstChar().trim())) {
            criteria.andFirstCharEqualTo(brand.getFirstChar());
        }

        //使用分页助手
        PageHelper.startPage(pageNum, pageSize);
        PageHelper.orderBy("id DESC"); //根据id降序(用于添加后的显示)
        Page<Brand> page = (Page<Brand>) brandDao.selectByExample(brandQuery);

        PageResult pageResult = new PageResult(page.getTotal(), page.getResult());

        return pageResult;

    }

    /**
     * 保存品牌信息
     *
     * @param brand
     */
    @Transactional
    @Override
    public void add(Brand brand) {
        brandDao.insertSelective(brand);
    }

    /**
     * 修改品牌之数据回显
     *
     * @param id
     * @return
     */
    @Transactional
    @Override
    public Brand findOne(Long id) {
        return brandDao.selectByPrimaryKey(id);
    }

    /**
     * 修改品牌
     *
     * @param brand
     */
    @Transactional
    @Override
    public void update(Brand brand) {
        brandDao.updateByPrimaryKey(brand);
    }

    /**
     * 批量删除品牌
     *
     * @param ids
     */
    @Transactional
    @Override
    public void delete(Long[] ids) {
       /* for (Long id : ids) {
            brandDao.deleteByPrimaryKey(id);            //一个一个删除 并没有达到批量效果
        }*/
        //自定义批量删除方法 自己写映射语句
        if (ids != null && ids.length > 0) {
            brandDao.deleteByPrimaryKeys(ids);
        }
    }

    /**
     * 模板管理中品牌下拉列表数据
     * @return
     */
    @Override
    public List<Map<String, String>> selectOptionList() {

        return brandDao.selectOptionList();
    }

}
