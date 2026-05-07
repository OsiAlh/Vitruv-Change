package tools.vitruv.change.propagation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.interaction.builder.MultipleChoiceSelectionInteractionBuilder;
import tools.vitruv.change.interaction.builder.MultipleChoiceSingleSelectionInteractionBuilder;

class ChangePropagationTriggerStrategyTests {

  @Test
  void humanApprovalAllowsPropagationWhenUserAccepts() {
    var userInteractor = createInteractor(0, new ArrayList<>());
    var strategy = new HumanApprovalTriggerStrategy("Please confirm propagation");

    var shouldPropagate = strategy.shouldPropagate(null, userInteractor);

    assertTrue(shouldPropagate);
  }

  @Test
  void humanApprovalSkipsPropagationWhenUserDenies() {
    var userInteractor = createInteractor(1, new ArrayList<>());
    var strategy = new HumanApprovalTriggerStrategy();

    var shouldPropagate = strategy.shouldPropagate(null, userInteractor);

    assertFalse(shouldPropagate);
  }

  @Test
  void humanApprovalDelaysAndThenPropagatesWhenUserDelays() {
    List<Duration> observedDelays = new ArrayList<>();
    var userInteractor = createInteractor(2, new ArrayList<>());
    var strategy =
        new HumanApprovalTriggerStrategy(
            "Please confirm propagation", Duration.ofMinutes(5), observedDelays::add);

    var shouldPropagate = strategy.shouldPropagate(null, userInteractor);

    assertTrue(shouldPropagate);
    assertEquals(List.of(Duration.ofMinutes(5)), observedDelays);
  }

  @Test
  void delayedStrategyUsesConfiguredDelay() {
    List<Duration> observedDelays = new ArrayList<>();
    var strategy =
        new DelayedTriggerStrategy(Duration.ofMinutes(10), observedDelays::add);

    var shouldPropagate = strategy.shouldPropagate(null, null);

    assertTrue(shouldPropagate);
    assertEquals(List.of(Duration.ofMinutes(10)), observedDelays);
  }

  private static InternalUserInteractor createInteractor(
      Integer selectedIndex, List<String> capturedMessages) {
    var optionalSteps =
        (MultipleChoiceSelectionInteractionBuilder.OptionalSteps<Integer>)
            Proxy.newProxyInstance(
                ChangePropagationTriggerStrategyTests.class.getClassLoader(),
                new Class<?>[] {MultipleChoiceSelectionInteractionBuilder.OptionalSteps.class},
                (proxy, method, args) -> {
                  if ("startInteraction".equals(method.getName())) {
                    return selectedIndex;
                  }
                  return proxy;
                });

    var choicesStep =
        (MultipleChoiceSelectionInteractionBuilder.ChoicesStep<Integer>)
            Proxy.newProxyInstance(
                ChangePropagationTriggerStrategyTests.class.getClassLoader(),
                new Class<?>[] {MultipleChoiceSelectionInteractionBuilder.ChoicesStep.class},
                (proxy, method, args) -> {
                  if ("choices".equals(method.getName())) {
                    return optionalSteps;
                  }
                  return proxy;
                });

    var singleSelectionBuilder =
        (MultipleChoiceSingleSelectionInteractionBuilder)
            Proxy.newProxyInstance(
                ChangePropagationTriggerStrategyTests.class.getClassLoader(),
                new Class<?>[] {MultipleChoiceSingleSelectionInteractionBuilder.class},
                (proxy, method, args) -> {
                  if ("message".equals(method.getName()) && args != null && args.length == 1) {
                    capturedMessages.add((String) args[0]);
                    return choicesStep;
                  }
                  return null;
                });

    return
        (InternalUserInteractor)
            Proxy.newProxyInstance(
                ChangePropagationTriggerStrategyTests.class.getClassLoader(),
                new Class<?>[] {InternalUserInteractor.class},
                (proxy, method, args) -> {
                  if ("getSingleSelectionDialogBuilder".equals(method.getName())) {
                    return singleSelectionBuilder;
                  }
                  if (method.getReturnType().equals(boolean.class)) {
                    return false;
                  }
                  return null;
                });
  }
}
