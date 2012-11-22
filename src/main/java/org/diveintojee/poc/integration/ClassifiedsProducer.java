package org.diveintojee.poc.integration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;


/**
 * @author louis.gueye@gmail.com
 */
@Component
public class ClassifiedsProducer implements WriteClassifiedEventProducer {

    private Set<WriteClassifiedEventListener>
        writeClassifiedEventListeners = new HashSet<WriteClassifiedEventListener>();

    private ConcurrentLinkedQueue<WriteClassifiedCommand> writeClassifiedsQueue = new ConcurrentLinkedQueue<WriteClassifiedCommand>();

    public void write(WriteClassifiedCommand writeClassifiedCommand) {
        writeClassifiedsQueue.add(writeClassifiedCommand);
        notifyListeners();
    }

    private void notifyListeners() {
        for (WriteClassifiedEventListener writeClassifiedEventListener : writeClassifiedEventListeners) {
            writeClassifiedEventListener.onMessage();
        }
    }

    @Override
    public void unregisterListener(WriteClassifiedEventListener writeClassifiedEventListener) {
        this.writeClassifiedEventListeners.remove(writeClassifiedEventListener);
    }

    @Override
    public void registerListener(WriteClassifiedEventListener writeClassifiedEventListener) {
        this.writeClassifiedEventListeners.add(writeClassifiedEventListener);
    }

    public WriteClassifiedCommand consume() {
        return writeClassifiedsQueue.remove();
    }
}
