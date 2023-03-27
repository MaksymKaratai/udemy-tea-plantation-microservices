package com.tea.order.mapper;

import com.tea.common.Constants;
import com.tea.order.domain.TeaOrder;
import com.tea.common.dto.order.TeaOrderDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public abstract class TeaOrderMapperDecorator implements TeaOrderMapper {
    private TeaOrderMapper delegate;

    @Autowired
    @Qualifier(Constants.MAPSTRUCT_DELEGATE)
    public void setDelegate(TeaOrderMapper delegate) {
        this.delegate = delegate;
    }

    @Override
    public TeaOrder toEntity(TeaOrderDto teaOrderDto) {
        TeaOrder teaOrder = delegate.toEntity(teaOrderDto);
        for (var line : teaOrder.getOrderLines()) {
            line.setTeaOrder(teaOrder);
        }
        return teaOrder;
    }
}
