package com.inso.core.controller.seller;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.PageResult;
import com.inso.core.entity.Result;
import com.inso.core.pojo.seller.Seller;
import com.inso.core.service.seller.SellerService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("seller")
public class SellerController {

    @Reference
    private SellerService sellerService;


    /**
     * 未审核商家列表查询以及搜索框功能
     *
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    @RequestMapping("search.do")
    public PageResult search(Integer page, Integer rows, @RequestBody Seller seller) {

        //System.out.println(seller.getStatus());
        return sellerService.search(page, rows, seller);
    }


    /**
     * 待审核商家详情
     *
     * @param id
     * @return
     */
    @RequestMapping("findOne.do")
    public Seller findOne(String id) {
        return sellerService.findOne(id);
    }

    /**
     * 商家审核
     */
    @RequestMapping("updateStatus.do")
    public Result updateStatus(String sellerId, String status) {

        try {
            sellerService.updateStatus(sellerId, status);
            return new Result(true, "操作成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "操作失败");
        }
    }
}

