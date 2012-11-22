package org.diveintojee.poc.integration;

import org.diveintojee.poc.persistence.search.SearchEngine;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author louis.gueye@gmail.com
 */
@Component
public class ClassifiedsConsumer implements WriteClassifiedEventListener, InitializingBean {

    @Autowired
    private ClassifiedsProducer classifiedsProducer;

    @Autowired
    private SearchEngine searchEngine;

    public void stopConsumingWriteCommands() {
        classifiedsProducer.unregisterListener(this);
    }

    public void startConsumingClassifieds() {
        classifiedsProducer.registerListener(this);
    }

    /**
     * Invoked by a BeanFactory after it has set all bean properties supplied (and satisfied
     * BeanFactoryAware and ApplicationContextAware). <p>This method allows the bean instance to
     * perform initialization only possible when all bean properties have been set and to throw an
     * exception in the event of misconfiguration.
     *
     * @throws Exception in the event of misconfiguration (such as failure to set an essential
     *                   property) or if initialization fails.
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        startConsumingClassifieds();
    }

    @Override
    public void onMessage() {
        WriteClassifiedCommand command = classifiedsProducer.consume();
        final Operation operation = command.getOperation();
        switch (operation) {
            case delete:
              searchEngine.removeFromIndex(command.getClassified());break;
            case write:
              searchEngine.index(command.getClassified());break;
            default:
              throw new UnsupportedOperationException("Command operation '" + operation + "' not yet supported");
          }
          System.out.println("new message arrived: " + command);
    }
}
