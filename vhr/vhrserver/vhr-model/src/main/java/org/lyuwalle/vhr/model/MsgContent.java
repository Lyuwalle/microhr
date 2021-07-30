package org.lyuwalle.vhr.model;

import lombok.Data;

import java.util.Date;

@Data
public class MsgContent {
    private Integer id;

    private String title;

    private String message;

    private Date createdate;

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public void setMessage(String message) {
        this.message = message == null ? null : message.trim();
    }

}