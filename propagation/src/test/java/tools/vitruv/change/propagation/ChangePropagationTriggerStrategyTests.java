package tools.vitruv.change.propagation;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Proxy;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import tools.vitruv.change.interaction.InternalUserInteractor;
import tools.vitruv.change.interaction.builder.ConfirmationInteractionBuilder;

class ChangePropagationTriggerStrategyTests {

  @Test
  void humanApprovalAllowsPropagationWhenUserConfirms() {
    List<String> capturedMessages = new ArrayList<>();
    var userInteractor = createInteractor(Boolean.TRUE, capturedMessages);
    var strategy = new HumanApprovalTriggerStrategy("Please confirm propagation");

    var shouldPropagate = strategy.shouldPropagate(null, userInteractor);

    assertTrue(shouldPropagate);
    assertEquals(List.of("Please confirm propagation"), capturedMessages);
  }

  @Test
  void humanApprovalSkipsPropagationWhenUserRejects() {
    var userInteractor = createInteractor(Boolean.FALSE, new ArrayList<>());
    var strategy = new HumanApprovalTriggerStrategy();

    var shouldPropagate = strategy.shouldPropagate(null, userInteractor);

    assertFalse(shouldPropagate);
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

  @Test
  void delayedStrategyThrowsWhenInterrupted() {
    var strategy =
        new DelayedTriggerStrategy(
            Duration.ofSeconds(1),
            delay -> {
              throw new InterruptedException("interrupted");
            });

    assertThrows(IllegalStateException.class, () -> strategy.shouldPropagate(null, null));
    assertTrue(Thread.currentThread().isInterrupted());
    Thread.interrupted();
  }

  private static InternalUserInteractor createInteractor(
      Boolean confirmationResult, List<String> capturedMessages) {
    var optionalSteps =
        (ConfirmationInteractionBuilder.OptionalSteps)
            Proxy.newProxyInstance(
                ChangePropagationTriggerStrategyTests.class.getClassLoader(),
                new Class<?>[] {ConfirmationInteractionBuilder.OptionalSteps.class},
                (proxy, method, args) -> {
                  if ("startInteraction".equals(method.getName())) {
                    return confirmationResult;
                  }
                  return proxy;
                });

    var confirmationBuilder =
        (ConfirmationInteractionBuilder)
            Proxy.newProxyInstance(
                ChangePropagationTriggerStrategyTests.class.getClassLoader(),
                new Class<?>[] {ConfirmationInteractionBuilder.class},
                (proxy, method, args) -> {
                  if ("message".equals(method.getName()) && args != null && args.length == 1) {
                    capturedMessages.add((String) args[0]);
                    return optionalSteps;
                  }
                  return null;
                });

    return
        (InternalUserInteractor)
            Proxy.newProxyInstance(
                ChangePropagationTriggerStrategyTests.class.getClassLoader(),
                new Class<?>[] {InternalUserInteractor.class},
                (proxy, method, args) -> {
                  if ("getConfirmationDialogBuilder".equals(method.getName())) {
                    return confirmationBuilder;
                  }
                  if (method.getReturnType().equals(boolean.class)) {
                    return false;
                  }
                  return null;
                });
  }
}
