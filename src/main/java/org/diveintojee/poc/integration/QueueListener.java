package org.diveintojee.poc.integration;

import org.springframework.jms.listener.adapter.MessageListenerAdapter;
import org.springframework.stereotype.Component;

import javax.jms.Message;

/**
 * @author louis.gueye@gmail.com
 */
@Component("queueListener")
public class QueueListener extends MessageListenerAdapter {

    @Override
    public void onMessage(Message message) {
        super.onMessage(message);    //To change body of overridden methods use File | Settings | File Templates.
    }

}
