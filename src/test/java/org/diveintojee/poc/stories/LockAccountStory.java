/**
 *
 */
package org.diveintojee.poc.stories;

import org.diveintojee.poc.steps.Exchange;
import org.diveintojee.poc.steps.LockAccountSteps;
import org.jbehave.core.io.CodeLocations;
import org.jbehave.core.io.StoryFinder;
import org.jbehave.core.steps.InjectableStepsFactory;
import org.jbehave.core.steps.InstanceStepsFactory;

import java.util.Arrays;
import java.util.List;

/**
 * @author louis.gueye@gmail.com
 */
public class LockAccountStory extends AbstractJUnitStories {

    @Override
    public InjectableStepsFactory stepsFactory() {
        return new InstanceStepsFactory(configuration(), new LockAccountSteps(new Exchange()));
    }

    @Override
    protected List<String> storyPaths() {
        return new StoryFinder().findPaths(CodeLocations.codeLocationFromClass(this.getClass()).getFile(),
                Arrays.asList("**/lock_account.story"), null);
    }
}
