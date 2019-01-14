package com.inso.core.service.itemsearch;

import java.util.Map;

public interface ItemSearchService {

    /**
     * 搜索功能
     *
     * @param searchMap
     * @return
     */
    public Map<String, Object> search(Map<String, String> searchMap);

    /**
     * 将商品信息保存到索引库
     *
     * @param id
     */
    public void updateToSolr(Long id);

    /**
     * 删除索引库中的商品信息
     * @param id
     */
    public void deleteItemFormSolr(Long id);
}
