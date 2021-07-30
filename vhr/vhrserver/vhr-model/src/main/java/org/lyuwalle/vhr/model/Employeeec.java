package org.lyuwalle.vhr.model;

import lombok.Data;

import java.util.Date;

@Data
public class Employeeec {
    private Integer id;

    private Integer eid;

    private Date ecdate;

    private String ecreason;

    private Integer ecpoint;

    private Integer ectype;

    private String remark;

}