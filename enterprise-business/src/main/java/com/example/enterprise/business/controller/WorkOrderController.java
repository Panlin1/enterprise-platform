package com.example.enterprise.business.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.enterprise.business.dto.WorkOrderCreateDTO;
import com.example.enterprise.business.dto.WorkOrderQueryDTO;
import com.example.enterprise.business.dto.WorkOrderStatusDTO;
import com.example.enterprise.business.dto.WorkOrderUpdateDTO;
import com.example.enterprise.business.service.WorkOrderService;
import com.example.enterprise.business.vo.WorkOrderVO;
import com.example.enterprise.common.core.result.PageResult;
import com.example.enterprise.common.core.result.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 业务工单接口。
 */
@Tag(name = "业务工单")
@RestController
@RequestMapping("/api/biz/work-orders")
@SaCheckLogin
public class WorkOrderController {

    private final WorkOrderService workOrderService;

    public WorkOrderController(WorkOrderService workOrderService) {
        this.workOrderService = workOrderService;
    }

    @Operation(summary = "工单分页")
    @GetMapping
    public Result<PageResult<WorkOrderVO>> page(@ModelAttribute WorkOrderQueryDTO query) {
        return Result.success(workOrderService.page(query));
    }

    @Operation(summary = "工单详情")
    @GetMapping("/{id}")
    public Result<WorkOrderVO> detail(@PathVariable Long id) {
        return Result.success(workOrderService.getById(id));
    }

    @Operation(summary = "创建工单")
    @PostMapping
    public Result<Long> create(@Valid @RequestBody WorkOrderCreateDTO dto) {
        return Result.success(workOrderService.create(dto));
    }

    @Operation(summary = "更新工单")
    @PutMapping("/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody WorkOrderUpdateDTO dto) {
        workOrderService.update(id, dto);
        return Result.success();
    }

    @Operation(summary = "变更状态")
    @PutMapping("/{id}/status")
    public Result<Void> changeStatus(@PathVariable Long id, @Valid @RequestBody WorkOrderStatusDTO dto) {
        workOrderService.changeStatus(id, dto);
        return Result.success();
    }

    @Operation(summary = "删除工单")
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        workOrderService.delete(id);
        return Result.success();
    }
}
