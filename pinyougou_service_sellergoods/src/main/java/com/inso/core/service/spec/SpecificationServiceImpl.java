package com.inso.core.service.spec;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.inso.core.dao.specification.SpecificationDao;
import com.inso.core.dao.specification.SpecificationOptionDao;
import com.inso.core.entity.PageResult;
import com.inso.core.pojo.specification.Specification;
import com.inso.core.pojo.specification.SpecificationOption;
import com.inso.core.pojo.specification.SpecificationOptionQuery;
import com.inso.core.pojo.specification.SpecificationQuery;
import com.inso.core.vo.SpecificationVo;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Service
public class SpecificationServiceImpl implements SpecificationService {

    @Resource
    private SpecificationDao specificationDao;

    @Resource
    private SpecificationOptionDao specificationOptionDao;

    /**
     * 规格信息列表展示以及搜索功能
     *
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Specification specification) {
        //设置分页条件
        PageHelper.startPage(page, rows);

        //设置查询条件
        SpecificationQuery query = new SpecificationQuery();
        SpecificationQuery.Criteria criteria = query.createCriteria();
        //判断是否为空 ,如果为空查询所有
        if (specification.getSpecName() != null && !"".equals(specification.getSpecName().trim())) {
            //如果不为空 条件查询(根据名字模糊查询)
            criteria.andSpecNameLike("%" + specification.getSpecName() + "%");

        }

        //设置根据id倒序排列
        query.setOrderByClause("id desc");
        //查询
        Page<Specification> p = (Page<Specification>) specificationDao.selectByExample(query);
        //System.out.println(p.toString());
        PageResult pageResult = new PageResult(p.getTotal(), p.getResult());
        //System.out.println(pageResult.toString());
        return pageResult;
    }

    /**
     * 新增规格信息以及规格选项
     *
     * @param specificationVo
     */
    @Transactional
    @Override
    public void add(SpecificationVo specificationVo) {

        //获取规格
        Specification specification = specificationVo.getSpecification();
        //保存
        specificationDao.insertSelective(specification);

        //获取规格选项
        List<SpecificationOption> optionList = specificationVo.getSpecificationOptionList();

        if (optionList != null && optionList.size() > 0) {

            for (SpecificationOption specificationOption : optionList) {

                specificationOption.setSpecId(specification.getId());  //规格选项的外键是规格的主键

            }

            //保存规格选项
            specificationOptionDao.insertSelectives(optionList);

        }


    }


    /**
     * 修改之数据回显
     *
     * @param id
     * @return
     */
    @Override
    public SpecificationVo findOne(Long id) {

        //查询规格
        Specification specification = specificationDao.selectByPrimaryKey(id);

        //查询规格选项
        //封装查询条件
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();

        criteria.andSpecIdEqualTo(id);

        List<SpecificationOption> specificationOptionList = specificationOptionDao.selectByExample(specificationOptionQuery);

        SpecificationVo specificationVo = new SpecificationVo();
        //将规格封装到VO中
        specificationVo.setSpecification(specification);
        //将规格选项封装到VO中
        specificationVo.setSpecificationOptionList(specificationOptionList);

        return specificationVo;
    }

    /**
     * 修改操作
     * 1.更新规格
     * 2.更新规格选项:
     * 先删除再插入(批量)
     *
     * @param specificationVo
     */
    @Override
    public void update(SpecificationVo specificationVo) {

        //更新规格
        Specification specification = specificationVo.getSpecification();
        specificationDao.updateByPrimaryKeySelective(specification);

        //更新规格选项
        //封装删除条件
        SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();
        SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();
        //根据外键删除
        criteria.andSpecIdEqualTo(specification.getId());
        specificationOptionDao.deleteByExample(specificationOptionQuery);

        //然后批量插入
        List<SpecificationOption> optionList = specificationVo.getSpecificationOptionList();

        if (optionList != null && optionList.size() > 0) {
            for (SpecificationOption specificationOption : optionList) {
                specificationOption.setSpecId(specification.getId());       //设置外键
            }

            //批量插入
            specificationOptionDao.insertSelectives(optionList);
        }

    }

    /**
     * 批量删除
     *
     * @param ids
     */
    @Override
    public void delete(Long[] ids) {

        if (ids != null && ids.length > 0) {
            //删除规格
            for (Long id : ids) {

                specificationDao.deleteByPrimaryKey(id);

                //删除规格选项 封装条件
                SpecificationOptionQuery specificationOptionQuery = new SpecificationOptionQuery();

                SpecificationOptionQuery.Criteria criteria = specificationOptionQuery.createCriteria();

                criteria.andSpecIdEqualTo(id);

                //删除
                specificationOptionDao.deleteByExample(specificationOptionQuery);
            }
        } else {
            throw new RuntimeException();
        }

    }

    @Override
    public List<Map<String, String>> selectOptionList() {
        return specificationDao.selectOptionList();

    }

}
