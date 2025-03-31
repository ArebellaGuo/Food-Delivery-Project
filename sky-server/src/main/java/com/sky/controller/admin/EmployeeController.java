package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
@Api(tags = "employee apis")
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    @ApiOperation("employee login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }

    /**
     * 退出
     * @return
     */
    @PostMapping("/logout")
    @ApiOperation("employee logout")
    public Result<String> logout() {
        return Result.success();
    }


    @PostMapping
    @ApiOperation("add new employee")
    public Result saveEmployee(@RequestBody EmployeeDTO employeeDTO){
        log.info("Adding new employee {}", employeeDTO);
        System.out.println("Current thread id: " + Thread.currentThread().getId());

        employeeService.save(employeeDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("Get all employees")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("Find all employees:{}", employeePageQueryDTO);
        PageResult pageQuery=employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageQuery);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("Start or ban employee account")
    public Result startOrStop(@PathVariable Integer status, Long id){
        log.info("Freeze or unfreeze employee account: {}, {}",status,id);
        employeeService.startOrStop(status,id);
        return Result.success();
    }

    //get employee info by employee id
    @GetMapping("/{id}")
    @ApiOperation("Get employee info by id")
    public Result<Employee> getById(@PathVariable Long id){
       Employee employee =  employeeService.getById(id);
        return Result.success(employee);

    }

    // update employee info
    @PutMapping
    @ApiOperation("Edit employee account information")
    public Result update(@RequestBody EmployeeDTO employeeDTO){
        log.info("Edit employee information: {}", employeeDTO);
        employeeService.update(employeeDTO);
        return Result.success();
    }
}
