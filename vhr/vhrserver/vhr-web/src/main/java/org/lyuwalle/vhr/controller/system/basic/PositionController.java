package org.lyuwalle.vhr.controller.system.basic;

import org.lyuwalle.vhr.model.Position;
import org.lyuwalle.vhr.model.RespBean;
import org.lyuwalle.vhr.service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author lyuWalle
 * @Date 2020/12/18 14:20
 * @Version 1.0
 */
@RestController
@RequestMapping("/system/basic/pos")
public class PositionController {
    @Autowired
    PositionService positionService;

    @GetMapping("/")
    public List<Position> getAllPositions(){
        return positionService.getAllPositions();
    }

    @PostMapping("/")
    public RespBean addPosition(@RequestBody Position position){
        if(positionService.addPosition(position) == 1){
            return RespBean.ok("添加成功！");
        }else{
            return RespBean.error("添加失败！");
        }
    }
    @PutMapping("/")
    public RespBean updatePosition(@RequestBody Position position){
        if(positionService.updatePosition(position) == 1){
            return RespBean.ok("更新成功！");
        }else{
            return RespBean.error("更新失败！");
        }
    }

    @DeleteMapping("/{id}")
    public RespBean deletePositionById(@PathVariable Integer id){
        if(positionService.deletePositionById(id) == 1){
            return RespBean.ok("删除成功！");
        }else{
            return RespBean.error("此职位有关联数据，删除失败！");
        }
    }

    @DeleteMapping("/")
    public RespBean deletePositionsByIds(Integer[] ids) {
        if (positionService.deletePositionsByIds(ids) == ids.length) {
            return RespBean.ok("删除成功!");
        }
        return RespBean.error("删除失败!");
    }
}
