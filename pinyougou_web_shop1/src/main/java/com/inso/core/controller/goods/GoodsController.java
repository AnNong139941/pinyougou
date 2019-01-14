package com.inso.core.controller.goods;


import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.PageResult;
import com.inso.core.entity.Result;
import com.inso.core.pojo.good.Goods;
import com.inso.core.service.goods.GoodsService;
import com.inso.core.vo.GoodsVo;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 新增商品
     *
     * @param goodsVo
     * @return
     */
    @RequestMapping("/add.do")
    public Result add(@RequestBody GoodsVo goodsVo) {

        try {
            //设置商家id
            String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
            goodsVo.getGoods().setSellerId(sellerId);

            goodsService.add(goodsVo);
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");
        }

    }

    /**
     * 商品的列表展示以及搜索功能
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @RequestMapping("search.do")
    public PageResult search(int page, int rows, @RequestBody Goods goods) {

        // System.out.println(goods);
        //设置商家id
        String sellerId = SecurityContextHolder.getContext().getAuthentication().getName();
        // System.out.println(sellerId);
        goods.setSellerId(sellerId);

        return goodsService.search(page, rows, goods);


    }

    /**
     * 修改之数据回显
     *
     * @param id
     * @return
     */
    @RequestMapping("findOne")
    public GoodsVo findOne(Long id) {

        return goodsService.findOne(id);
    }

    /**
     * 商品更新
     * @param goodsVo
     * @return
     */
    @RequestMapping("update.do")
    public Result update(@RequestBody GoodsVo goodsVo) {

        try {
            goodsService.update(goodsVo);
            return new Result(true,"更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false,"更新失败");
        }
    }


}
