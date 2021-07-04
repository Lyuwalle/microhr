package org.lyuwalle.vhr.controller.emp;

import org.lyuwalle.vhr.model.*;
import org.lyuwalle.vhr.service.*;
import org.lyuwalle.vhr.utils.POIUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @Author lyuWalle
 * @Date 2020/12/21 15:45
 * @Version 1.0
 */
@RestController
@RequestMapping("/employee/basic/")
public class EmpBasicController {

    @Autowired
    EmployeeService employeeService;

    @Autowired
    NationService nationService;
    @Autowired
    PoliticsstatusService politicsstatusService;
    @Autowired
    JobLevelService jobLevelService;
    @Autowired
    PositionService positionService;

    @Autowired
    DepartmentService departmentService;

    @GetMapping("/")
    /*默认是第一页，每页查询10个*/
    public RespPageBean getEmployeeByPage(@RequestParam(defaultValue = "1") Integer page,
                                          @RequestParam(defaultValue = "10") Integer size,
                                          Employee employee, Date[] beginDateScope){
        return employeeService.getEmployeeByPage(page, size, employee, beginDateScope);
    }
    @PostMapping("/")
    public RespBean addEmp(@RequestBody Employee employee) {
        if (employeeService.addEmp(employee) == 1) {
            return RespBean.ok("添加成功!");
        }
        return RespBean.error("添加失败!");
    }

    @PutMapping("/")
    public RespBean updateEmp(@RequestBody Employee employee) {
        if (employeeService.updateEmp(employee) == 1) {
            return RespBean.ok("更新成功!");
        }
        return RespBean.error("更新失败!");
    }

    @DeleteMapping("/{id}")
    public RespBean deleteEmpByEid(@PathVariable Integer id) {
        if (employeeService.deleteEmpByEid(id) == 1) {
            return RespBean.ok("删除成功!");
        }
        return RespBean.error("删除失败!");
    }

    @GetMapping("/maxWorkID")
    public RespBean getMaxWorkID(){
        return RespBean.build().setStatus(200).setObj(String.format("%08d", employeeService.getMaxWorkId() + 1));
    }

    @GetMapping("/nations")
    public List<Nation> getAllNations() {
        return nationService.getAllNations();
    }

    @GetMapping("/politicsstatus")
    public List<Politicsstatus> getAllPoliticsstatus() {
        return politicsstatusService.getAllPoliticsstatus();
    }

    @GetMapping("/joblevels")
    public List<JobLevel> getAllJobLevels() {
        return jobLevelService.getAllJobLevels();
    }

    @GetMapping("/positions")
    public List<Position> getAllPositions() {
        return positionService.getAllPositions();
    }

    @GetMapping("/deps")
    public List<Department> getAllDepartments() {
        return departmentService.getAllDepartments();
    }

    /*导入数据*/
    @PostMapping("/import")
    /*file表示前端上传的文件*/
    public RespBean importData(MultipartFile file) throws IOException {
        /*excel存的是中文的数据，要转成id之后才能存到List<Employee>里面*/
        List<Employee> list = POIUtils.excelToEmployee(file,
                nationService.getAllNations(),
                politicsstatusService.getAllPoliticsstatus(),
                departmentService.getAllDepartmentsWithOutChildren(),
                positionService.getAllPositions(),
                jobLevelService.getAllJobLevels()
        );
        if (employeeService.addEmps(list) == list.size()) {
            return RespBean.ok("上传成功");
        }
        return RespBean.error("上传失败");
    }

    /*导出数据*/
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportData() {
        /*首先去数据库里面查询出所有的员工数据，然后返回*/
        List<Employee> list = (List<Employee>) employeeService.getEmployeeByPage(null, null,
                new Employee(),null).getData();
        return POIUtils.employeeToExcel(list);
    }
}
