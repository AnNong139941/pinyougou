package com.inso.core.controller.cart;

import com.alibaba.dubbo.config.annotation.Reference;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.inso.core.entity.Result;
import com.inso.core.pojo.cart.Cart;
import com.inso.core.pojo.item.Item;
import com.inso.core.pojo.order.OrderItem;
import com.inso.core.service.cart.CartService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Reference
    private CartService cartService;

    /**
     * 将商品添加到购物车
     * 从静态页跳转到购物车页面有CORS(跨域资源共享)问题
     *
     * @param itemId
     * @param num
     * @return
     */
    @RequestMapping("addGoodsToCartList.do")
    //@CrossOrigin(origins = {"http://localhost:9003"},allowCredentials = "true")
    @CrossOrigin(origins = {"http://localhost:9003"})    //默认为true,无需设置
    public Result addGoodsToCartList(Long itemId, Integer num,
                                     HttpServletRequest request, HttpServletResponse response) {
        try {

            //定义标识
            boolean flag = false;


            // 服务器端支持CORS
            //response.setHeader("Access-Control-Allow-Origin", "http://localhost:9003");
            // 携带cookie
            //response.setHeader("Access-Control-Allow-Credentials", "true");

            // 加入购物车逻辑实现
            //1.定义空的购物车集合
            List<Cart> cartList = null;
            //2.判断是否有购物车
            Cookie[] cookies = request.getCookies();
            if (cookies != null && cookies.length > 0) {

                for (Cookie cookie : cookies) {
                    //2.1 有车就取出来赋值给空车
                    if ("BUYER_CART".equals(cookie.getName())) {
                        String cookieValue = null;
                        try {
                            cookieValue = URLDecoder.decode(cookie.getValue(), "utf-8");
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }

                        //转成对象 赋值给空购物车
                        cartList = JSONArray.parseArray(cookieValue, Cart.class);
                        //找到车就跳出循环
                        flag = true;
                        break;
                    }
                }

            }

            //2.2 如果没有车 自己创建一个
            if (cartList == null) {
                cartList = new ArrayList<>();
            }
            // 到这里我们已经有购物车了 接下来就是装商品
            //3.创建购物车 封装页面数据,对cookie进行瘦身,
            // 因为我们不可能将所有字段放入cookie(4kb的大小限制),所以放入关键字段即可
            Cart cart = new Cart();
            Item item = cartService.findOne(itemId);
            cart.setSellerId(item.getSellerId());

            //3.1封装购物项 瘦身 只设置数量和skuid
            ArrayList<OrderItem> orderItemList = new ArrayList<>();
            OrderItem orderItem = new OrderItem();
            orderItem.setItemId(itemId);
            orderItem.setNum(num);
            orderItemList.add(orderItem);
            cart.setOrderItemList(orderItemList);  //设置商品购物项


            //4.将商品放入购物车
            //4.1判断是否是同一个商家 判断sellerId是否相同即可
            int sellerIndexOf = cartList.indexOf(cart);
            // -1表示不是同一个商家
            if (sellerIndexOf != -1) {
                // 属于同一个商家
                // 4.2、判断该商品是否属于同款：判断itemId是否一样，因此需要重写orderItem中的方法
                // 取出该商家数据并判断
                Cart oldCart = cartList.get(sellerIndexOf);
                List<OrderItem> oldOrderItemList = oldCart.getOrderItemList();
                int itemIndexOf = oldOrderItemList.indexOf(orderItem);
                if (itemIndexOf != -1) {
                    //同款商品 合并数量
                    OrderItem oldOrderItem = oldOrderItemList.get(itemIndexOf);
                    oldOrderItem.setNum(oldOrderItem.getNum() + num);
                } else {
                    //同款 不是同款商品
                    oldOrderItemList.add(orderItem);
                }

            } else {
                //不是同一个商家 直接放入购物车
                cartList.add(cart);
            }
            //保存购物车 需判断是否登录
            //5.1 已登录 放入Redis中
            //获取到未登录(anonymousUser)或者登录后的用户名
            String name = SecurityContextHolder.getContext().getAuthentication().getName();
            //System.out.println(name); //anonymousUser
            if (!"anonymousUser".equals(name)) {
                cartService.mergeCarList(cartList, name);
                // 标识flag为true 说明如果本地有 清空本地购物车
                if (flag) {
                    Cookie cookie = new Cookie("BUYER_CART", null);
                    //设置存活时间
                    cookie.setMaxAge(0);
                    //设置cookie共享
                    cookie.setPath("/");
                    response.addCookie(cookie);
                }
            } else {
                //5.2 未登录将购物车保存到本地cookie中  JSON-->对象
                Cookie cookie = new Cookie("BUYER_CART", URLEncoder.encode(JSON.toJSONString(cartList), "utf-8"));
                //设置存活时间
                cookie.setMaxAge(60 * 60);
                //设置cookie共享
                cookie.setPath("/");
                response.addCookie(cookie);
            }
            return new Result(true, "加入购物车成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "加入购物车失败");
        }

    }

    /**
     * 填充页面展示所需的数据
     *
     * @param request
     * @return
     */
    @RequestMapping("findCartList.do")
    public List<Cart> findCartList(HttpServletRequest request, HttpServletResponse response) {
        //1.定义空的购物车集合
        List<Cart> cartList = null;
        //2.判断是否有购物车
        Cookie[] cookies = request.getCookies();
        if (cookies != null && cookies.length > 0) {

            for (Cookie cookie : cookies) {
                //2.1 有车就取出来赋值给空车
                if ("BUYER_CART".equals(cookie.getName())) {

                    String cookieValue = null;
                    try {
                        cookieValue = URLDecoder.decode(cookie.getValue(), "utf-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    //转成对象 赋值给空购物车

                    cartList = JSONArray.parseArray(cookieValue, Cart.class);
                    //找到车就跳出循环
                    break;
                }
            }

        }

        //TODO 已登录 从Redis中获取数据 填充数据
        String name = SecurityContextHolder.getContext().getAuthentication().getName();
        if (!"anonymousUser".equals(name)) {
            //已登录 将本地购物车同步到缓存中
            if (cartList != null) {

                cartService.mergeCarList(cartList, name);
                //清空cookie
                Cookie cookie = new Cookie("BUYER_CART", null);
                cookie.setMaxAge(0);
                cookie.setPath("/"); // 设置cookie共享
                response.addCookie(cookie);
            }
            //从Redis中取出购物车
            cartList = cartService.findCartListFromRedis(name);
        }
        if (cartList != null) {
            //填充页面需要的数据(cookie中只封装了一些简单字段)
            cartList = cartService.autoDataToCartList(cartList);
        }

        return cartList;
    }
}
