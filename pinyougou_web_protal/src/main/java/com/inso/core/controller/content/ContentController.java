package com.inso.core.controller.content;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.pojo.ad.Content;
import com.inso.core.service.content.ContentService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("content")
public class ContentController {

    @Reference
    private ContentService contentService;

    @RequestMapping("findByCategoryId.do")
    public List<Content> findByCategoryId(Long categoryId) {

        //System.out.println(1);
        List<Content> contentList = contentService.findByCategoryId(categoryId);
        //System.out.println(contentList.toString());
        return contentList;
    }
}
