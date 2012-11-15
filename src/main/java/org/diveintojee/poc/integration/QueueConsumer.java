package org.diveintojee.poc.integration;

import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import javax.jms.Message;

/**
 * @author louis.gueye@gmail.com
 */
@Component("queueConsumer")
public class QueueConsumer extends MessageListenerAdapter {

    @Override
    public void onMessage(Message message) {
        super.onMessage(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void stopConsumingClassifieds() {
        throw new UnsupportedOperationException("Not implemented");
    }

    public void startConsumingClassifieds() {
        throw new UnsupportedOperationException("Not implemented");
    }
}
