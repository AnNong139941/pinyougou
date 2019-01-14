package com.inso.core.controller.template;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.PageResult;
import com.inso.core.entity.Result;
import com.inso.core.pojo.template.TypeTemplate;
import com.inso.core.service.template.TypeTemplateService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("typeTemplate")
public class TypeTemplateController {

    @Reference
    private TypeTemplateService typeTemplateService;


    /**
     * 通过模板id查询对应品牌
     *
     * @param id
     * @return
     */
    @RequestMapping("findOne.do")
    public TypeTemplate findOne(Long id) {

        return typeTemplateService.findOne(id);
    }


    /**
     * 通过模板id查询对应的规格以及规格选项
     * @param id
     * @return
     */
    @RequestMapping("findBySpecList.do")
    public List<Map> findBySpecList(Long id) {

        return typeTemplateService.findBySpecList(id);

    }
}
