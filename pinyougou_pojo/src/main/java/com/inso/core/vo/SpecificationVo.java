package com.inso.core.vo;

import com.inso.core.pojo.specification.Specification;
import com.inso.core.pojo.specification.SpecificationOption;

import java.io.Serializable;
import java.util.List;

/**
 * 封装vo
 */
public class SpecificationVo implements Serializable {
    private Specification specification;                        //规格
   private List<SpecificationOption> specificationOptionList;   //规格选项

    public Specification getSpecification() {
        return specification;
    }

    public void setSpecification(Specification specification) {
        this.specification = specification;
    }

    public List<SpecificationOption> getSpecificationOptionList() {
        return specificationOptionList;
    }

    public void setSpecificationOptionList(List<SpecificationOption> specificationOptionList) {
        this.specificationOptionList = specificationOptionList;
    }
}
