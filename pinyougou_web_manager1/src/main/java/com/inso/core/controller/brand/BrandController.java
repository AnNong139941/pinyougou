package com.inso.core.controller.brand;

import com.alibaba.dubbo.config.annotation.Reference;
import com.inso.core.entity.PageResult;
import com.inso.core.entity.Result;
import com.inso.core.pojo.good.Brand;
import com.inso.core.service.brand.BrandService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Reference
    private BrandService brandService;

    /**
     * 查询所有
     *
     * @return
     */
    @RequestMapping(value = "findAll.do", produces = MediaType.APPLICATION_JSON_VALUE + ";charset=utf-8")
    public List<Brand> findAll() {
        return brandService.findAll();
    }

    /**
     * 分页查询
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/findByPage.do")
    public PageResult findPage(Integer pageNum, Integer pageSize) {
        //System.out.println(pageNum+"========"+pageSize);
        PageResult pageResult = brandService.findPage(pageNum, pageSize);

        return pageResult;
    }

    /**
     * 条件查询
     *
     * @param pageNum
     * @param pageSize
     * @return
     */
    @RequestMapping("/search.do")
    public PageResult search(Integer pageNum, Integer pageSize, @RequestBody Brand brand) {
        //System.out.println(pageNum+"========"+pageSize);
        PageResult pageResult = brandService.findCondition(pageNum, pageSize, brand);

        return pageResult;
    }

    /**
     * 新增品牌信息
     *
     * @param brand
     * @return
     */
    @RequestMapping("save.do")
    public Result add(@RequestBody Brand brand) {
        try {
            //保存数据
            brandService.add(brand);
            //成功
            return new Result(true, "保存成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "保存失败");

        }

    }

    /**
     * 修改品牌之数据回显
     *
     * @param id
     * @return
     */
    @RequestMapping("findById.do")
    public Brand findOne(Long id) {

        Brand brand = brandService.findOne(id);

        return brand;
    }

    /**
     * 修改品牌信息
     *
     * @param brand
     * @return
     */
    @RequestMapping("update.do")
    public Result update(@RequestBody Brand brand) {
        try {
            brandService.update(brand);
            return new Result(true, "更新成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "更新失败");
        }

    }

    /**
     * 批量删除品牌
     *
     * @param ids
     * @return
     */

    @RequestMapping("delete.do")
    public Result delete(Long[] ids) {
        try {
            brandService.delete(ids);
            return new Result(true, "删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return new Result(false, "删除失败");
        }

    }

    /**
     * 模板管理中品牌下拉列表数据
     * @return
     */
    @RequestMapping("selectOptionList.do")
    public List<Map<String,String>> selectOptionList(){

        return brandService.selectOptionList();
    }
}
