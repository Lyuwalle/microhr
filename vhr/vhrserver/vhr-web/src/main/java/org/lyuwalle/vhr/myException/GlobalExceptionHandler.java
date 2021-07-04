package org.lyuwalle.vhr.myException;

import org.lyuwalle.vhr.model.RespBean;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @Author lyuWalle
 * @Date 2020/12/18 15:58
 * @Version 1.0
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    /*表示当异常的类型是括号里面的类型时，就会生效*/
    @ExceptionHandler(SQLException.class)
    public RespBean mySQLException(SQLException e){
        if (e instanceof SQLIntegrityConstraintViolationException) {
            return RespBean.error("该数据有关联数据，操作失败!");
        }
        return RespBean.error("数据库异常，操作失败！");
    }
}
