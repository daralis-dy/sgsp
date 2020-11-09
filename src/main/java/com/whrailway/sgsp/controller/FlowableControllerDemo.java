package com.whrailway.sgsp.controller;

import com.whrailway.sgsp.utils.R;
import io.swagger.annotations.ApiOperation;
import org.apache.tomcat.jni.Proc;
import org.flowable.engine.ProcessEngine;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.spring.ProcessEngineFactoryBean;
import org.flowable.task.api.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * ClassName: FlowableControllerDemo
 * Description: 流程模型Flowable使用demo
 *
 * @author dy
 * @date 2020-11-5
 */
@RestController
@RequestMapping("/flowable")
public class FlowableControllerDemo {
    @Autowired
    private RuntimeService runtimeService;
    @Autowired
    private TaskService taskService;

    /**
     * .提交采购订单的审批请求
     *
     * @param userId 用户id
     */
    @RequestMapping("/start/{userId}/{purchaseOrderId}")
    public R startFlow(@PathVariable String userId, @PathVariable String purchaseOrderId) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("userId", userId);
        map.put("purchaseOrderId", purchaseOrderId);
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("OrderApproval", map);
        String processId = processInstance.getId();
        String name = processInstance.getName();
        System.out.println(processId + ":" + name);
        return R.ok(processId + ":" + name);
    }

    /**
     * .获取用户的任务
     *
     * @param userId 用户id
     */
    @GetMapping("/getTasks/{userId}")
    public R getTasks(@PathVariable String userId) {
        List<Task> tasks = taskService.createTaskQuery().taskAssignee(userId).orderByTaskCreateTime().desc().list();
        return R.ok(tasks.toString());
    }

    /**
     * .审批通过
     */
    @RequestMapping("/success/{taskId}")
    public R success(@PathVariable String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return R.error("流程不存在");
        }
        //通过审核
        HashMap<String, Object> map = new HashMap<>();
        map.put("approved", true);
        taskService.complete(taskId, map);
        return R.ok("流程审核通过！");
    }

    /**
     * .审批不通过
     */
    @RequestMapping("/faile/{taskId}")
    public R faile(@PathVariable String taskId) {
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return R.error("流程不存在");
        }
        //通过审核
        HashMap<String, Object> map = new HashMap<>();
        map.put("approved", false);
        taskService.complete(taskId, map);
        return R.ok();
    }

    @RequestMapping("/test")
    public R test(){
        ProcessInstance processInstance =
                runtimeService.startProcessInstanceByKey("Test");
        List<Task> tasks = taskService.createTaskQuery().taskAssignee("testuser").list();
        return R.ok(tasks.toString());

    }

    @ApiOperation("flowable并行会签测试，超过一半则通过")
    @RequestMapping(value = "/livingtest", method = RequestMethod.GET)
    public void living(){
        HashMap<String, Object> map = new HashMap<>();
        //定义的人员列表4人
        String[] v = { "user1", "user2", "user3", "user4" };
        map.put("assigneeList", Arrays.asList(v));
        ProcessInstance pi = runtimeService.startProcessInstanceByKey("living",map);

        List<Task> list = taskService.createTaskQuery().processInstanceId(pi.getId()).list();
        System.out.println("--------创建4个并行任务---------------");
        int  i=0;

        for (Task task : list) {
            i=i+1;
            System.out.println("============ i ="+i);
            System.out.println("================== 节点name is =  "+task.getName());
            System.out.println("================== 办理人assginee is = " + task.getAssignee());
            System.out.println("================== 节点id is =  "+task.getId());
            //变相判断已经二人提交 之后人员不提交
            if (i<3) {
                System.out.println("================== 提交 节点 id is="+task.getId());
                taskService.complete(task.getId());
            }
        }
        //判断值为 50% 所以提交人达到2人 会签节点即可通过
        Task task = taskService.createTaskQuery().processInstanceId(pi.getId()).singleResult();
        System.out.println("====================================================================================");
        //验证是否已通过
        System.out.println("===================task id is="+task.getId());
        System.out.println("===================task name is="+task.getName());
    }
}
