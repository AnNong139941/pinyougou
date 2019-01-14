package com.inso.core.service.seller;

import com.alibaba.dubbo.config.annotation.Service;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.inso.core.dao.seller.SellerDao;
import com.inso.core.entity.PageResult;
import com.inso.core.pojo.seller.Seller;
import com.inso.core.pojo.seller.SellerQuery;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import java.util.Date;


@Service
public class SellerServiceImpl implements SellerService {
    @Resource
    private SellerDao sellerDao;

    /**
     * 商家申请入驻(商家注册)
     *
     * @param seller
     */
    @Transactional
    @Override
    public void add(Seller seller) {

        /**
         * 设置状态:
         *  status状态值：  0：未审核   1：已审核   2：审核未通过   3：关闭
         *
         */

        seller.setStatus("0");      //初始化状态值 未审核

        seller.setCreateTime(new Date());     //设置提交时间

        //BCrypt加密
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = encoder.encode(seller.getPassword());
        seller.setPassword(password);

        //保存
        sellerDao.insertSelective(seller);
    }

    /**
     * 未审核商家列表查询以及搜索框功能
     *
     * @param page
     * @param rows
     * @param seller
     * @return
     */
    @Override
    public PageResult search(Integer page, Integer rows, Seller seller) {

        //设置分页条件
        PageHelper.startPage(page, rows);

        //设置查询条件
        SellerQuery sellerQuery = new SellerQuery();
        SellerQuery.Criteria criteria = sellerQuery.createCriteria();

        //封装条件
        if (seller.getStatus() != null && !"".equals(seller.getStatus().trim())) {
            criteria.andStatusEqualTo(seller.getStatus().trim());
        }
        if (seller.getName() != null &&! "".equals(seller.getName())) {
            criteria.andNameLike("%" + seller.getName() + "%");
        }

        if (seller.getNickName() != null && !"".equals(seller.getNickName())) {
            criteria.andNickNameLike("%" + seller.getNickName() + "%");
        }

        //查询
        Page<Seller> p = (Page<Seller>) sellerDao.selectByExample(sellerQuery);

        return new PageResult(p.getTotal(), p.getResult());
    }

    @Override
    public Seller findOne(String  id) {
        return sellerDao.selectByPrimaryKey(id);
    }

    /**
     * 商家审核
     * @param sellerId
     * @param status
     */
    @Transactional
    @Override
    public void updateStatus(String sellerId, String status) {

        // 更新审核的状态
        Seller seller = sellerDao.selectByPrimaryKey(sellerId);
        seller.setStatus(status);
        sellerDao.updateByPrimaryKeySelective(seller);
    }
}
