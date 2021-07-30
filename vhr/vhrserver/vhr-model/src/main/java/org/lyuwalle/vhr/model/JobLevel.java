package org.lyuwalle.vhr.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Data
public class JobLevel implements Serializable {
    private Integer id;

    private String name;

    private String titleLevel;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        JobLevel jobLevel = (JobLevel) o;
        return Objects.equals(name, jobLevel.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    public JobLevel() {

    }

    public JobLevel(String name) {

        this.name = name;
    }

    @JsonFormat(pattern = "yyyy-MM-dd  HH:MM:SS",timezone = "Asia/Shanghai")
    private Date createDate;

    private Boolean enabled;

}