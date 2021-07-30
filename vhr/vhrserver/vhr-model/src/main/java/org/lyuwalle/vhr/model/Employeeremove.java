package org.lyuwalle.vhr.model;

import lombok.Data;

import java.util.Date;

@Data
public class Employeeremove {
    private Integer id;

    private Integer eid;

    private Integer afterdepid;

    private Integer afterjobid;

    private Date removedate;

    private String reason;

    private String remark;

}