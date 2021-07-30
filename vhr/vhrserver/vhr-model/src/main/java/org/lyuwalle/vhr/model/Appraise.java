package org.lyuwalle.vhr.model;

import lombok.Data;

import java.util.Date;

@Data
public class Appraise {
    private Integer id;

    private Integer eid;

    private Date appdate;

    private String appresult;

    private String appcontent;

    private String remark;

}