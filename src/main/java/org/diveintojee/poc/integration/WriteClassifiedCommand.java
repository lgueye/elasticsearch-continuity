package org.diveintojee.poc.integration;

import com.google.common.base.Objects;
import org.diveintojee.poc.domain.Classified;

/**
 * User: louis.gueye@gmail.com Date: 22/11/12 Time: 11:40
 */
public class WriteClassifiedCommand {

    private Classified classified;
    private Operation operation;

    public WriteClassifiedCommand(Classified classified, Operation operation) {
        this.classified = classified;
        this.operation = operation;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
                .add("classified", classified)
                .add("opreation", operation).toString();
    }

    public Classified getClassified() {
        return classified;
    }


    public Operation getOperation() {
        return operation;
    }
}
