package org.lyuwalle.vhr.model;

import lombok.Data;

import java.util.List;

/**
 *
 */
@Data
public class RespPageBean {
    /*总记录数*/
    private Long total;

    private List<?> data;
}
