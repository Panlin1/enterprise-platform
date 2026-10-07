package com.example.enterprise.business.service;

import com.example.enterprise.business.dto.WorkOrderCreateDTO;
import com.example.enterprise.business.dto.WorkOrderQueryDTO;
import com.example.enterprise.business.dto.WorkOrderStatusDTO;
import com.example.enterprise.business.dto.WorkOrderUpdateDTO;
import com.example.enterprise.business.vo.WorkOrderVO;
import com.example.enterprise.common.core.result.PageResult;

/**
 * 工单业务接口。
 */
public interface WorkOrderService {

    PageResult<WorkOrderVO> page(WorkOrderQueryDTO query);

    WorkOrderVO getById(Long id);

    Long create(WorkOrderCreateDTO dto);

    void update(Long id, WorkOrderUpdateDTO dto);

    void changeStatus(Long id, WorkOrderStatusDTO dto);

    void delete(Long id);
}
