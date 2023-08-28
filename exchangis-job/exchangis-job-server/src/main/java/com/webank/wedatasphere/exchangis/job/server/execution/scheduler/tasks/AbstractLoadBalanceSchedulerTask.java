package com.webank.wedatasphere.exchangis.job.server.execution.scheduler.tasks;

import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerException;
import com.webank.wedatasphere.exchangis.job.server.exception.ExchangisSchedulerRetryException;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.AbstractExchangisSchedulerTask;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.ScheduleListener;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance.LoadBalancePoller;
import com.webank.wedatasphere.exchangis.job.server.execution.scheduler.loadbalance.SchedulerLoadBalancer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Implement the load balance logic
 * @param <T>
 */
public abstract class AbstractLoadBalanceSchedulerTask<T> extends AbstractExchangisSchedulerTask implements LoadBalanceSchedulerTask<T> {

    private static final Logger LOG = LoggerFactory.getLogger(AbstractLoadBalanceSchedulerTask.class);

    private LoadBalancePoller<T> loadBalancePoller;

    private SchedulerLoadBalancer<T> schedulerLoadBalancer;

    private boolean pollFinish = false;

    /**
     * Schedule listener
     */
    private ScheduleListener<AbstractLoadBalanceSchedulerTask<T>> listener;

    public AbstractLoadBalanceSchedulerTask() {
        super("");
    }

    @Override
    protected void schedule() throws ExchangisSchedulerException, ExchangisSchedulerRetryException {
        LoadBalancePoller<T> loadBalancePoller = getOrCreateLoadBalancePoller();
        if (Objects.isNull(loadBalancePoller)) {
            LOG.warn("LoadBalancePoller is empty in load balance scheduler task [{}]", getName());
            return;
        }
        if (!pollFinish && Objects.nonNull(listener)){
            // Invoke listener
            listener.onSchedule(this);
        }
        List<T> pollElements = new ArrayList<>();
        LOG.info("Start to iterate the poller in load balance scheduler task [{}]", getName());
        while (!pollFinish && null != pollElements) {
            try {
                pollElements = loadBalancePoller.poll();
                Optional.of(pollElements).ifPresent(elements -> {
                    elements.forEach(pollElement -> {
                        try {
                            onPoll(pollElement);
                        } catch (Exception e) {
                            LOG.warn("Error occurred in onPoll in load balance scheduler task [{}]", getName(), e);
                        }
                    });
                    for( T pollElement : elements){
                        try {
                            rePushWithBalancer(pollElement, this.schedulerLoadBalancer);
                        } catch (Exception e) {
                            throw new ExchangisSchedulerException.Runtime(
                                    "Error occurred in rePush in load balance scheduler task [" + getName() + "]", e);
                        }
                    }
                });

            } catch (Exception e) {
                if (e instanceof InterruptedException && pollFinish){
                    LOG.trace("Poller is interrupted by shutdown, will exit gradually");
                }else {
                    if (e instanceof ExchangisSchedulerException.Runtime) {
                        LOG.warn("Schedule method error", e);
                    }
                    LOG.warn("Error occurred in poll/onPoll/rePush in load balance scheduler task [{}]", getName(), e);
                }
            }
        }
        LOG.info("End to iterate the poller in load balance scheduler task [{}]", getName());
    }

    @Override
    public LoadBalancePoller<T> getOrCreateLoadBalancePoller() {
        if (null == this.loadBalancePoller){
            synchronized (this){
                if (null == this.loadBalancePoller) {
                    this.loadBalancePoller = createLoadBalancePoller();
                }
            }
        }
        return this.loadBalancePoller;
    }

    public SchedulerLoadBalancer<T> getSchedulerLoadBalancer() {
        return schedulerLoadBalancer;
    }

    public void setSchedulerLoadBalancer(SchedulerLoadBalancer<T> schedulerLoadBalancer) {
        this.schedulerLoadBalancer = schedulerLoadBalancer;
    }

    public void setScheduleListener(ScheduleListener<AbstractLoadBalanceSchedulerTask<T>> listener){
        this.listener = listener;
    }

    /**
     * Re push the element into poller with balancer
     * @param element element
     * @param loadBalancer load balancer
     */
    private void rePushWithBalancer(T element, SchedulerLoadBalancer<T> loadBalancer) throws ExchangisSchedulerException{
        LoadBalanceSchedulerTask<T> loadBalanceSchedulerTask = loadBalancer.choose(element, this.getClass());
        Optional.ofNullable(loadBalanceSchedulerTask).ifPresent(schedulerTask ->
                schedulerTask.getOrCreateLoadBalancePoller().push(element));
    }
    @Override
    public void kill() {
        pollFinish = true;
        super.kill();
    }

    protected abstract void onPoll(T element) throws ExchangisSchedulerException, ExchangisSchedulerRetryException;

    /**
     * Create the load balance poller
     * @return
     */
    protected abstract LoadBalancePoller<T> createLoadBalancePoller();

}
