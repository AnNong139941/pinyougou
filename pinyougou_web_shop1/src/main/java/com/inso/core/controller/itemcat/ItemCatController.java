package com.inso.core.controller.itemcat;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.pojo.item.ItemCat;
import com.inso.core.service.itemcat.ItemCatService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("itemCat")
public class ItemCatController {

    @Reference
    private ItemCatService itemCatService;


    /**
     * 通过分类加载模板id
     *
     * @param id
     * @return
     */
    @RequestMapping("findOne.do")
    public ItemCat findOne(Long id) {

        return itemCatService.findOne(id);
    }


    /**
     * 商品录入的分级列表展示
     *
     * @param parentId
     * @return
     */
    @RequestMapping("findByParentId.do")
    public List<ItemCat> findByParentId(Long parentId) {

        return itemCatService.findParentId(parentId);

    }

    /**
     * 查询所有分类用于商品列表三级分类展示
     *
     * @return
     */
    @RequestMapping("findAll.do")
    public List<ItemCat> findAll() {

        return itemCatService.findAll();
    }


}
