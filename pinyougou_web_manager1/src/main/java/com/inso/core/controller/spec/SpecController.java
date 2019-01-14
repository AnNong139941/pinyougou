package com.inso.core.controller.spec;


import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.PageResult;
import com.inso.core.entity.Result;
import com.inso.core.pojo.specification.Specification;
import com.inso.core.service.spec.SpecificationService;
import com.inso.core.vo.SpecificationVo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("specification")
public class SpecController {

    @Reference
    private SpecificationService specificationService;

    /**
     * 列表展示以及搜索功能
     *
     * @param page
     * @param rows
     * @param specification
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Specification specification) {

        // System.out.println("========================"+rows);
        return specificationService.search(page, rows, specification);
    }

    /**
     * 添加规格及规格选项
     *
     * @param specificationVo
     * @return
     */
    @RequestMapping("add.do")
    public Result add(@RequestBody SpecificationVo specificationVo) {
        try {
            specificationService.add(specificationVo);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");

        }

    }


    /**
     * 修改之数据回显(主键查询)
     *
     * @param id
     * @return
     */
    @RequestMapping("findOne.do")
    public SpecificationVo findOne(Long id) {


        SpecificationVo specificationVo = specificationService.findOne(id);

        return specificationVo;

    }

    /**
     * 修改操作
     *
     * @param specificationVo
     * @return
     */
    @RequestMapping("update.do")
    public Result update(@RequestBody SpecificationVo specificationVo) {
        try {
            specificationService.update(specificationVo);
            return new Result(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");

        }

    }


    /**
     * 批量删除
     *
     * @param ids
     * @return
     */
    @RequestMapping("delete.do")
    public Result delete(Long[] ids) {
        try {
            specificationService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");

        }
    }

    /**
     * 模板管理编辑之下拉菜单数据
     * @return
     */
    @RequestMapping("selectOptionList.do")
    public List<Map<String ,String >> selectOptionList(){

        return specificationService.selectOptionList();
    }
}
