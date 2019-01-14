package com.inso.core.controller.goods;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.PageResult;
import com.inso.core.entity.Result;
import com.inso.core.pojo.good.Goods;
import com.inso.core.service.goods.GoodsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("goods")
public class GoodsController {

    @Reference
    private GoodsService goodsService;

    /**
     * 运营商系统待审核商品列表展示
     *
     * @param page
     * @param rows
     * @param goods
     * @return
     */
    @RequestMapping("search.do")
    public PageResult searchForManager(int page, int rows, @RequestBody Goods goods) {

        return goodsService.searchForManager(page, rows, goods);
    }

    /**
     * 商品审核
     *
     * @param ids
     * @param status
     * @return
     */
    @RequestMapping("updateStatus.do")
    public Result updateStatus(Long[] ids, String status) {
        try {
            goodsService.updateStatus(ids, status);
            return new Result(true, "操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "操作失败");
        }

    }

    /**
     * 逻辑删除：更新状态（is_delete  默认值：null  删除的状态：1）
     * @param ids
     * @return
     */
    @RequestMapping("delete.do")
    public Result delete(Long[] ids) {
        try {
            goodsService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }

    }
}
