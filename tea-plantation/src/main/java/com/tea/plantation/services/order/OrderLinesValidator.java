package com.tea.plantation.services.order;

import com.tea.common.dto.order.TeaOrderLineDto;
import com.tea.plantation.repository.TeaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderLinesValidator {
    private final TeaRepository repository;

    public boolean areOrderLinesValid(List<TeaOrderLineDto> lines) {
        if (CollectionUtils.isEmpty(lines)) {
            log.debug("No order lines present, fail validation");
            return false;
        }
        for (var line : lines) {
            UUID teaId = line.getTeaId();
            if (!repository.existsByUuid(teaId)) {
                log.debug("Fail order validation since tea with uuid[{}] doesn't exists", teaId);
                return false;
            }
        }
        log.debug("Order lines are valid");
        return true;
    }
}
