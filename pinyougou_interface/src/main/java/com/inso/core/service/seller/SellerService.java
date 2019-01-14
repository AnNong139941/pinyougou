package com.inso.core.service.seller;

import com.inso.core.entity.PageResult;
import com.inso.core.pojo.seller.Seller;

public interface SellerService {
    /**
     * 商家申请入驻(商家注册)
     * @param seller
     */
    void add(Seller seller);

    /**
     * 未审核商家列表查询
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    public PageResult search(Integer page,Integer rows,Seller seller);


    /**
     * 待审核商家详情
     * @return
     */
    public Seller findOne(String id);

    /**
     * 商家审核
     * @param sellerId
     * @param status
     */
    public void updateStatus(String sellerId, String status);
}
