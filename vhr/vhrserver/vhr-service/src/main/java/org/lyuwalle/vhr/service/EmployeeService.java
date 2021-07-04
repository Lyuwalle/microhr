package org.lyuwalle.vhr.service;

import org.lyuwalle.vhr.mapper.EmployeeMapper;
import org.lyuwalle.vhr.model.Employee;
import org.lyuwalle.vhr.model.MailConstants;
import org.lyuwalle.vhr.model.MailSendLog;
import org.lyuwalle.vhr.model.RespPageBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @Author lyuWalle
 * @Date 2020/12/21 15:51
 * @Version 1.0
 */
@Service
public class EmployeeService {

    @Autowired
    EmployeeMapper employeeMapper;

    //下面的RabbitTemplate是自己在RabbitConfig里面定义的，而不是系统提供的了
    @Autowired
    RabbitTemplate rabbitTemplate;

    @Autowired
    MailSendLogService mailSendLogService;

    /*日志*/
    public static final Logger logger = LoggerFactory.getLogger(EmployeeService.class);

    SimpleDateFormat yearFormat = new SimpleDateFormat("yyyy");
    SimpleDateFormat monthFormat = new SimpleDateFormat("MM");
    DecimalFormat decimalFormat = new DecimalFormat("##.00");

    /*page表示第几页，size表示size个数据，这里的page = (page - 1) * size的变换是为了sql查询的limit语句*/
    public RespPageBean getEmployeeByPage(Integer page, Integer size, Employee employee, Date[] beginDateScope) {
          if(page != null && size != null){
              page = (page - 1) * size;
          }
          List<Employee> data = employeeMapper.getEmployeeByPage(page, size, employee, beginDateScope);
          //total表示总的员工的数量
          Long total = employeeMapper.getTotal(employee, beginDateScope);
          RespPageBean bean = new RespPageBean();
          bean.setData(data);
          bean.setTotal(total);
          return bean;
    }

    public Integer addEmp(Employee employee){
            Date beginContract = employee.getBeginContract();
            Date endContract = employee.getEndContract();
            double month = (Double.parseDouble(yearFormat.format(endContract)) - Double.parseDouble(yearFormat.format(beginContract))) * 12 + (Double.parseDouble(monthFormat.format(endContract)) - Double.parseDouble(monthFormat.format(beginContract)));
            employee.setContractTerm(Double.parseDouble(decimalFormat.format(month / 12)));
            int result = employeeMapper.insertSelective(employee);
            if (result == 1) {  //如果插入员工成功，就把邮件中的一些信息提取出来
                Employee emp = employeeMapper.getEmployeeById(employee.getId());

                //生成消息的唯一id，MailSendLog表示消息类（包括msgId，empId,status,routekey,Exchange,等，和数据库mail_send_log对应）
                String msgId = UUID.randomUUID().toString();
                MailSendLog mailSendLog = new MailSendLog();
                mailSendLog.setMsgId(msgId);
                mailSendLog.setCreateTime(new Date());
                mailSendLog.setExchange(MailConstants.MAIL_EXCHANGE_NAME);
                mailSendLog.setRouteKey(MailConstants.MAIL_ROUTING_KEY_NAME);
                mailSendLog.setEmpId(emp.getId());
                mailSendLog.setCount(0);
                //投递失败时，重新投递这个消息的时间间隔是1min
//count为什么不能设置为0？？？？？？？？？？？？？？？？因为在mapper里面的insert语句里面没有添加count
                mailSendLog.setTryTime(new Date(System.currentTimeMillis() + 1000 * 60 * MailConstants.MSG_TIMEOUT));
                mailSendLogService.insert(mailSendLog);
                /*三个参数：交换器，路由键，点对点式交换器将会根据路由键把消息发送到相应的消息队列上面，emp是要发送的东西，emp会先序列化之后再发送出去*/
                /*CorrelationData表示这个消息的唯一表示，rabbitTemplate.setConfirmCallback((data, ack, cause)里面的data参数就是这个*/
                rabbitTemplate.convertAndSend(MailConstants.MAIL_EXCHANGE_NAME, MailConstants.MAIL_ROUTING_KEY_NAME, emp, new CorrelationData(msgId));
                logger.info(emp.toString());
            }
            return result;
        }
    public Integer getMaxWorkId() {
        return employeeMapper.maxWorkID();
    }

    public Integer deleteEmpByEid(Integer id) {
        return employeeMapper.deleteByPrimaryKey(id);
    }

    public Integer updateEmp(Employee employee) {
        return employeeMapper.updateByPrimaryKeySelective(employee);
    }

    public Integer addEmps(List<Employee> list) {
        return employeeMapper.addEmps(list);
    }

    public RespPageBean getEmployeeByPageWithSalary(Integer page, Integer size) {
        if (page != null && size != null) {
            page = (page - 1) * size;
        }
        List<Employee> list = employeeMapper.getEmployeeByPageWithSalary(page, size);
        RespPageBean respPageBean = new RespPageBean();
        respPageBean.setData(list);
        respPageBean.setTotal(employeeMapper.getTotal(null, null));
        return respPageBean;
    }

    public Integer updateEmployeeSalaryById(Integer eid, Integer sid) {
        return employeeMapper.updateEmployeeSalaryById(eid, sid);
    }

    public Employee getEmployeeById(Integer empId) {
        return employeeMapper.getEmployeeById(empId);
    }
}
