package com.inso.core.dao.ad;

import com.inso.core.pojo.ad.ContentCategory;
import com.inso.core.pojo.ad.ContentCategoryQuery;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ContentCategoryDao {
    int countByExample(ContentCategoryQuery example);

    int deleteByExample(ContentCategoryQuery example);

    int deleteByPrimaryKey(Long id);

    int insert(ContentCategory record);

    int insertSelective(ContentCategory record);

    List<ContentCategory> selectByExample(ContentCategoryQuery example);

    ContentCategory selectByPrimaryKey(Long id);

    int updateByExampleSelective(@Param("record") ContentCategory record, @Param("example") ContentCategoryQuery example);

    int updateByExample(@Param("record") ContentCategory record, @Param("example") ContentCategoryQuery example);

    int updateByPrimaryKeySelective(ContentCategory record);

    int updateByPrimaryKey(ContentCategory record);
}