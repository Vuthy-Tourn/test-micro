package kh.edu.istad.stadoor.consumer.config;

import org.axonframework.eventhandling.EventMessage;
import org.axonframework.messaging.deadletter.DeadLetter;
import org.axonframework.messaging.deadletter.Decisions;
import org.axonframework.messaging.deadletter.EnqueueDecision;
import org.axonframework.messaging.deadletter.EnqueuePolicy;

public class RetryConstrainedEnqueuePolicy implements EnqueuePolicy<EventMessage<?>> {

    private final int retryConstraint;

    public RetryConstrainedEnqueuePolicy(int retryConstraint) {
        this.retryConstraint = retryConstraint;
    }

    @Override
    public EnqueueDecision<EventMessage<?>> decide(DeadLetter<? extends EventMessage<?>> letter, Throwable cause) {
        final int retries = (int) letter.diagnostics().getOrDefault("retries", -1);
        if (retries < retryConstraint) {
            return Decisions.requeue(cause, l -> l.diagnostics().and("retries", retries + 1));
        }
        return Decisions.evict();
    }
}
