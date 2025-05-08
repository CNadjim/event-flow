package io.github.cnadjim.eventflow.spring.starter.adaptor;

public interface SpringAdaptor<SPRING_CLASS, EVENT_FLOW_CLASS> {

    SPRING_CLASS toSpring(EVENT_FLOW_CLASS eventFlowClass);

    EVENT_FLOW_CLASS fromSpring(SPRING_CLASS springOrder);
}
