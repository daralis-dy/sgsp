package com.whrailway.sgsp.controller;

import org.flowable.engine.HistoryService;
import org.flowable.engine.RepositoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * flowable流程Demo
 * 需要在flowable-ui的用户管理idm组件中添加测试用户组accounty,包含用户fozzie,组management，包含用户kermit。对应bpmn文件和测试代码。
 * 在flowable-ui的权限控制中，给链各个用户添加访问workflow应用的权限，则测试用户登录flowable-ui后，可以直接在web中启动流程，执行任务。
 */
@RestController
@RequestMapping("/financialReport")
public class FinancialReportDemo {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;
    @Autowired
    private RepositoryService repositoryService;
    @Autowired
    private HistoryService historyService;

    @RequestMapping("/start")
    public void startFlow() {

        //springboot会自动部署resources下的bpmn文件，不需要手动部署，
        //启动一个流程实例
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("FinancialReportProcess");

        //查询accountancy组的任务
        List<Task> tasks = taskService.createTaskQuery().taskCandidateGroup("accountancy").list();
        for (Task task : tasks) {
            System.out.println("Following task is available for accountancy group: " + task.getId());
            //申请任务。bpmn中设计的accountancy组里的任一用户都可以认领，认领了之后任务属于个人
            taskService.claim(task.getId(),"fozzie");
        }
        System.out.println("Number of tasks for fozzie: "
                + taskService.createTaskQuery().taskAssignee("fozzie").count());

        // 验证Fozzie获取了任务
        tasks = taskService.createTaskQuery().taskAssignee("fozzie").list();
        for (Task task : tasks) {
            System.out.println("Task for fozzie: " + task.getName());
            // 完成任务
            taskService.complete(task.getId());
        }


        // 获取并申领第二个任务
        tasks = taskService.createTaskQuery().taskCandidateGroup("management").list();
        for (Task task : tasks) {
            System.out.println("Following task is available for management group: " + task.getId());
            taskService.claim(task.getId(), "kermit");
        }

        // 完成第二个任务并结束流程
        for (Task task : tasks) {
            taskService.complete(task.getId());
        }

        // 验证流程已经结束
        HistoricProcessInstance historicProcessInstance =
                historyService.createHistoricProcessInstanceQuery().processInstanceId(processInstance.getId()).singleResult();
        System.out.println("Process instance end time: " + historicProcessInstance.getEndTime());
    }

}
