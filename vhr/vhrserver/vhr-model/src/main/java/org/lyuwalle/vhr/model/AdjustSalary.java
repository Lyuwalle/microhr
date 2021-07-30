package org.lyuwalle.vhr.model;

import lombok.Data;

import java.util.Date;

@Data
public class AdjustSalary {
    private Integer id;

    private Integer eid;

    private Date asdate;

    private Integer beforesalary;

    private Integer aftersalary;

    private String reason;

    private String remark;

}