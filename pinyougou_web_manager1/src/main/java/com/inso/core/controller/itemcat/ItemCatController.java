package com.inso.core.controller.itemcat;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.Result;
import com.inso.core.pojo.item.ItemCat;
import com.inso.core.service.itemcat.ItemCatService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;

    /**
     * 查询以及分页
     *
     * @param page
     * @param rows
     * @param itemCat
     * @return
     *//*
    @RequestMapping("search.do")
    public PageResult search(int page, int rows,  @RequestBody ItemCat itemCat) {
        return itemCatService.search(page, rows, itemCat);
    }*/

    /**
     * 新增分类
     *
     * @param itemCat
     * @return
     */
    @RequestMapping("add.do")
    public Result add(@RequestBody ItemCat itemCat) {

        try {
            itemCatService.add(itemCat);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }
    }


    /**
     * 修改之数据回显
     *
     * @param id
     * @return
     */
    @RequestMapping("findOne.do")
    public ItemCat findOne(Long id) {

        return itemCatService.findOne(id);
    }

    /**
     * 修改
     *
     * @param itemCat
     * @return
     */
    @RequestMapping("update.do")
    public Result update(@RequestBody ItemCat itemCat) {

        try {
            itemCatService.update(itemCat);
            return new Result(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }
    }


    /**
     * 根据上级id查询列表
     *
     * @param parentId
     * @return
     */
    @RequestMapping("findByParentId.do")
    public List<ItemCat> findByParentId(Long parentId) {

        return itemCatService.findParentId(parentId);

    }

    /**
     * 运行商系统商品三级分类展示
     *
     * @return
     */
    @RequestMapping("findAll.do")
    public List<ItemCat> findAll() {

        return itemCatService.findAll();
    }

}
