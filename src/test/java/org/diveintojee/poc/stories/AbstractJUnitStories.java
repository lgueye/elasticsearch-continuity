/**
 *
 */
package org.diveintojee.poc.stories;

import org.jbehave.core.configuration.Configuration;
import org.jbehave.core.configuration.MostUsefulConfiguration;
import org.jbehave.core.failures.FailingUponPendingStep;
import org.jbehave.core.junit.JUnitStories;
import org.jbehave.core.reporters.Format;

/**
 * @author louis.gueye@gmail.com
 */
public abstract class AbstractJUnitStories extends JUnitStories {

    @Override
    public Configuration configuration() {

        Configuration configuration = new MostUsefulConfiguration();
        configuration.usePendingStepStrategy(new FailingUponPendingStep());
        configuration.storyReporterBuilder() // Configure report builder
                .withFormats(Format.HTML_TEMPLATE, Format.ANSI_CONSOLE) // Configure
                .withFailureTrace(true) //
                .withMultiThreading(true);

        configuration.storyControls().doSkipScenariosAfterFailure(false).doResetStateBeforeStory(true);

        return configuration;
    }

}
