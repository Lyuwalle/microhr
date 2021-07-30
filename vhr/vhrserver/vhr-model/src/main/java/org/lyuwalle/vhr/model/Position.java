package org.lyuwalle.vhr.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

@Data
public class Position implements Serializable {
    private Integer id;

    public Position() {

    }

    public Position(String name) {

        this.name = name;
    }

    private String name;

    @JsonFormat(pattern = "yyyy-MM-dd HH:MM:SS", timezone = "Asia/Shanghai")
    private Date createDate;

    private Boolean enabled;

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return Objects.equals(name, position.name);
    }
    @Override
    public int hashCode() {
        return Objects.hash(name);
    }
}