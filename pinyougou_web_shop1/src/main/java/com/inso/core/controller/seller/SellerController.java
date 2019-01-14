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
     * 商家申请入驻(商家注册)
     *
     * @param seller
     * @return
     */
    @RequestMapping("add.do")
    public Result add(@RequestBody Seller seller) {
        try {
            // System.out.println(seller);
            sellerService.add(seller);
            return new Result(true, "提交申请成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "提交申请失败");
        }
    }



}
