package org.lyuwalle.vhr.controller.system.basic;

import org.lyuwalle.vhr.mapper.JobLevelMapper;
import org.lyuwalle.vhr.model.JobLevel;
import org.lyuwalle.vhr.model.RespBean;
import org.lyuwalle.vhr.service.JobLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author lyuWalle
 * @Date 2020/12/18 21:36
 * @Version 1.0
 */
@RestController
@RequestMapping("/system/basic/joblevel")
public class JobLevelController {

    @Autowired
    JobLevelService jobLevelService;

    @GetMapping("/")
    public List<JobLevel> getAllPositions(){
        return jobLevelService.getAllPositions();
    }

    @PostMapping("/")
    public RespBean addJobLevel(@RequestBody JobLevel jobLevel){
        if(jobLevelService.addJobLevel(jobLevel) == 1){
            return RespBean.ok("添加职称等级成功！");
        }else{
            return RespBean.error("添加职称等级失败！");
        }
    }

    @PutMapping("/")
    public RespBean updateJobLevel(@RequestBody JobLevel jobLevel){
        if(jobLevelService.updateJobLevel(jobLevel) == 1){
            return RespBean.ok("更新职称等级成功！");
        }else{
            return RespBean.error("更新职称等级失败！");
        }
    }

    @DeleteMapping("/{id}")
    public RespBean deleteJobLevelById(@PathVariable Integer id){
        if(jobLevelService.deleteJobLevelById(id) == 1){
            return RespBean.ok("删除职称等级成功！");
        }else{
            return RespBean.error("删除职称等级失败！");
        }
    }

    @DeleteMapping("/")
    public RespBean deleteJobLevelByIds(Integer[] ids){
        if(jobLevelService.deleteJobLevelsByIds(ids) == ids.length){
            return RespBean.ok("删除成功！");
        }else{
            return RespBean.error("删除失败！");
        }
    }
}
