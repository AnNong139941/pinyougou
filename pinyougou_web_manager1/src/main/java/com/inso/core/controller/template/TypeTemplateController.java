package com.inso.core.controller.template;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.PageResult;
import com.inso.core.entity.Result;
import com.inso.core.pojo.template.TypeTemplate;
import com.inso.core.service.template.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;


    /**
     * 列表查询以及搜索框功能
     *
     * @param page
     * @param rows
     * @param typeTemplate
     * @return
     */
    @RequestMapping("search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody TypeTemplate typeTemplate) {

        return typeTemplateService.search(page, rows, typeTemplate);
    }

    /**
     * 新增
     *
     * @param typeTemplate
     * @return
     */
    @RequestMapping("add.do")
    public Result add(@RequestBody TypeTemplate typeTemplate) {
        try {
            typeTemplateService.add(typeTemplate);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }
    }

    /**
     * 修改之数据回显
     * @param id
     * @return
     */
    @RequestMapping("findOne.do")
    public TypeTemplate findOne(Long id) {

        return typeTemplateService.findOne(id);
    }


    /**
     * 修改
     * @param typeTemplate
     * @return
     */
    @RequestMapping("update.do")
    public Result update(@RequestBody TypeTemplate typeTemplate) {
        try {
            typeTemplateService.update(typeTemplate);
            return new Result(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }
    }

    /**
     * 批量删除
     * @param ids
     * @return
     */
    @RequestMapping("delete.do")
    public Result delete(Long[] ids) {
        try {
            typeTemplateService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }
    }

}
