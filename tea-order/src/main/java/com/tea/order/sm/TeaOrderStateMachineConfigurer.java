package com.tea.order.sm;

import com.tea.order.domain.OrderStatus;
import com.tea.order.sm.action.AllocateOrderAction;
import com.tea.order.sm.action.AllocationFailedCompensationAction;
import com.tea.order.sm.action.ValidateOrderAction;
import com.tea.order.sm.action.ValidationFailedCompensationAction;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

import static com.tea.order.domain.OrderStatus.ALLOCATED;
import static com.tea.order.domain.OrderStatus.ALLOCATING;
import static com.tea.order.domain.OrderStatus.ALLOCATION_ERROR;
import static com.tea.order.domain.OrderStatus.DELIVERING_ERROR;
import static com.tea.order.domain.OrderStatus.NEW;
import static com.tea.order.domain.OrderStatus.PENDING_INVENTORY;
import static com.tea.order.domain.OrderStatus.PICKED_UP;
import static com.tea.order.domain.OrderStatus.VALIDATED;
import static com.tea.order.domain.OrderStatus.VALIDATING;
import static com.tea.order.domain.OrderStatus.VALIDATION_ERROR;
import static com.tea.order.sm.OrderEvent.ALLOCATE;
import static com.tea.order.sm.OrderEvent.ALLOCATION_FAILED;
import static com.tea.order.sm.OrderEvent.ALLOCATION_OK;
import static com.tea.order.sm.OrderEvent.ORDER_PICK_UP;
import static com.tea.order.sm.OrderEvent.VALIDATE;
import static com.tea.order.sm.OrderEvent.VALIDATION_FAILED;
import static com.tea.order.sm.OrderEvent.VALIDATION_OK;
import static com.tea.order.sm.OrderEvent.WAIT_FOR_INVENTORY;

@Configuration
@RequiredArgsConstructor
@EnableStateMachineFactory
public class TeaOrderStateMachineConfigurer extends StateMachineConfigurerAdapter<OrderStatus, OrderEvent> {
    private final ValidateOrderAction validateAction;
    private final AllocateOrderAction allocationAction;
    private final ValidationFailedCompensationAction validationFailedCompensationAction;
    private final AllocationFailedCompensationAction allocationFailedCompensationAction;

    @Override
    public void configure(StateMachineStateConfigurer<OrderStatus, OrderEvent> states) throws Exception {
        states.withStates()
                .initial(NEW)
                .states(EnumSet.allOf(OrderStatus.class))
                .end(PICKED_UP)
                .end(VALIDATION_ERROR)
                .end(ALLOCATION_ERROR)
                .end(DELIVERING_ERROR);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStatus, OrderEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(NEW).event(VALIDATE).target(VALIDATING)
                    .action(validateAction)
                .and().withExternal()
                // validations
                .source(VALIDATING).event(VALIDATION_FAILED).target(VALIDATION_ERROR)
                    .action(validationFailedCompensationAction)
                .and().withExternal()
                .source(VALIDATING).event(VALIDATION_OK).target(VALIDATED)
                .and().withExternal()
                .source(VALIDATED).event(ALLOCATE).target(ALLOCATING)
                    .action(allocationAction)
                .and().withExternal()
                // allocations
                .source(ALLOCATING).event(ALLOCATION_FAILED).target(ALLOCATION_ERROR)
                    .action(allocationFailedCompensationAction)
                .and().withExternal()
                .source(ALLOCATING).event(WAIT_FOR_INVENTORY).target(PENDING_INVENTORY)
                .and().withExternal()
                .source(PENDING_INVENTORY).event(ALLOCATE).target(ALLOCATING)
                .and().withExternal()
                .source(ALLOCATING).event(ALLOCATION_OK).target(ALLOCATED)
                .and().withExternal()
                // pickup
                .source(ALLOCATED).event(ORDER_PICK_UP).target(PICKED_UP);
    }
}
