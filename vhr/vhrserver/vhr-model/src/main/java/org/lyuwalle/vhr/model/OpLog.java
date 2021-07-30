package org.lyuwalle.vhr.model;

import lombok.Data;

import java.util.Date;

@Data
public class OpLog {
    private Integer id;

    private Date adddate;

    private String operate;

    private Integer hrid;

}