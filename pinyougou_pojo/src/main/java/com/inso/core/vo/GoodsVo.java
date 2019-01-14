package com.inso.core.vo;

import com.inso.core.pojo.good.Goods;
import com.inso.core.pojo.good.GoodsDesc;
import com.inso.core.pojo.item.Item;
import com.inso.core.pojo.item.ItemCat;

import java.io.Serializable;
import java.util.List;

/**
 * 封装商品编辑信息
 */
public class GoodsVo implements Serializable {

    private Goods goods;        //封装商品信息
    private GoodsDesc goodsDesc;    //商品详情
    private List<Item> itemList;    //库存信息

    public Goods getGoods() {
        return goods;
    }

    public void setGoods(Goods goods) {
        this.goods = goods;
    }

    public GoodsDesc getGoodsDesc() {
        return goodsDesc;
    }

    public void setGoodsDesc(GoodsDesc goodsDesc) {
        this.goodsDesc = goodsDesc;
    }

    public List<Item> getItemList() {
        return itemList;
    }

    public void setItemList(List<Item> itemList) {
        this.itemList = itemList;
    }
}
