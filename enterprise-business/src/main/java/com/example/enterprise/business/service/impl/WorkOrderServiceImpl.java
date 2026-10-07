package com.example.enterprise.business.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.example.enterprise.business.dto.WorkOrderCreateDTO;
import com.example.enterprise.business.dto.WorkOrderQueryDTO;
import com.example.enterprise.business.dto.WorkOrderStatusDTO;
import com.example.enterprise.business.dto.WorkOrderUpdateDTO;
import com.example.enterprise.business.entity.BizWorkOrder;
import com.example.enterprise.business.mapper.BizWorkOrderMapper;
import com.example.enterprise.business.service.WorkOrderService;
import com.example.enterprise.business.vo.WorkOrderVO;
import com.example.enterprise.common.core.constant.ErrorCode;
import com.example.enterprise.common.core.exception.BusinessException;
import com.example.enterprise.common.core.result.PageResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

/**
 * 工单业务实现：CRUD + 简单状态流转校验。
 */
@Service
public class WorkOrderServiceImpl implements WorkOrderService {

    private static final Logger log = LoggerFactory.getLogger(WorkOrderServiceImpl.class);

    /** 状态：待处理 */
    private static final int ST_PENDING = 0;
    /** 状态：处理中 */
    private static final int ST_PROCESSING = 1;
    /** 状态：已完成 */
    private static final int ST_DONE = 2;
    /** 状态：已关闭 */
    private static final int ST_CLOSED = 3;

    private final BizWorkOrderMapper workOrderMapper;

    public WorkOrderServiceImpl(BizWorkOrderMapper workOrderMapper) {
        this.workOrderMapper = workOrderMapper;
    }

    @Override
    public PageResult<WorkOrderVO> page(WorkOrderQueryDTO query) {
        LambdaQueryWrapper<BizWorkOrder> w = new LambdaQueryWrapper<>();
        w.like(StringUtils.hasText(query.getTitle()), BizWorkOrder::getTitle, query.getTitle())
                .eq(query.getStatus() != null, BizWorkOrder::getStatus, query.getStatus())
                .eq(query.getPriority() != null, BizWorkOrder::getPriority, query.getPriority())
                .orderByDesc(BizWorkOrder::getId);
        Page<BizWorkOrder> page = workOrderMapper.selectPage(query.toPage(), w);
        List<WorkOrderVO> list = page.getRecords().stream().map(this::toVO).collect(Collectors.toList());
        return PageResult.of(page, list);
    }

    @Override
    public WorkOrderVO getById(Long id) {
        return toVO(requireOrder(id));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long create(WorkOrderCreateDTO dto) {
        BizWorkOrder order = new BizWorkOrder();
        order.setOrderNo(generateOrderNo());
        order.setTitle(dto.getTitle());
        order.setContent(dto.getContent());
        order.setPriority(dto.getPriority() == null ? 2 : dto.getPriority());
        order.setStatus(ST_PENDING);
        order.setAssigneeId(dto.getAssigneeId());
        order.setRemark(dto.getRemark());
        if (StpUtil.isLogin()) {
            order.setCreatorId(StpUtil.getLoginIdAsLong());
        }
        workOrderMapper.insert(order);
        log.info("工单已创建 id={} orderNo={}", order.getId(), order.getOrderNo());
        return order.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(Long id, WorkOrderUpdateDTO dto) {
        BizWorkOrder order = requireOrder(id);
        // 已完成/已关闭不允许改内容
        if (order.getStatus() != null
                && (order.getStatus() == ST_DONE || order.getStatus() == ST_CLOSED)) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "已完成或已关闭的工单不可修改");
        }
        if (dto.getTitle() != null) {
            order.setTitle(dto.getTitle());
        }
        if (dto.getContent() != null) {
            order.setContent(dto.getContent());
        }
        if (dto.getPriority() != null) {
            order.setPriority(dto.getPriority());
        }
        if (dto.getAssigneeId() != null) {
            order.setAssigneeId(dto.getAssigneeId());
        }
        if (dto.getRemark() != null) {
            order.setRemark(dto.getRemark());
        }
        workOrderMapper.updateById(order);
        log.info("工单已更新 id={}", id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeStatus(Long id, WorkOrderStatusDTO dto) {
        int target = dto.getStatus();
        if (target < ST_PENDING || target > ST_CLOSED) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "非法状态值");
        }
        BizWorkOrder order = requireOrder(id);
        int current = order.getStatus() == null ? ST_PENDING : order.getStatus();
        // 简单流转：不可从关闭回到其他；不可从完成回到待处理
        if (current == ST_CLOSED) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "已关闭工单不可再变更状态");
        }
        if (current == ST_DONE && target == ST_PENDING) {
            throw BusinessException.of(ErrorCode.BAD_REQUEST, "已完成不可回退为待处理");
        }
        order.setStatus(target);
        if (dto.getRemark() != null) {
            order.setRemark(dto.getRemark());
        }
        workOrderMapper.updateById(order);
        log.info("工单状态变更 id={} {} -> {}", id, current, target);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        requireOrder(id);
        workOrderMapper.deleteById(id);
        log.info("工单已删除 id={}", id);
    }

    private BizWorkOrder requireOrder(Long id) {
        BizWorkOrder order = workOrderMapper.selectById(id);
        if (order == null) {
            throw BusinessException.of(ErrorCode.NOT_FOUND, "工单不存在");
        }
        return order;
    }

    /** 生成工单号：WO + 时间 + 随机数 */
    private String generateOrderNo() {
        String time = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        int rnd = ThreadLocalRandom.current().nextInt(1000, 9999);
        return "WO" + time + rnd;
    }

    private WorkOrderVO toVO(BizWorkOrder e) {
        WorkOrderVO vo = new WorkOrderVO();
        vo.setId(e.getId());
        vo.setOrderNo(e.getOrderNo());
        vo.setTitle(e.getTitle());
        vo.setContent(e.getContent());
        vo.setPriority(e.getPriority());
        vo.setStatus(e.getStatus());
        vo.setAssigneeId(e.getAssigneeId());
        vo.setCreatorId(e.getCreatorId());
        vo.setRemark(e.getRemark());
        vo.setCreatedAt(e.getCreatedAt());
        vo.setUpdatedAt(e.getUpdatedAt());
        return vo;
    }
}

