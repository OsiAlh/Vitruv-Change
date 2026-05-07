package tools.vitruv.change.propagation;

import static com.google.common.base.Preconditions.checkArgument;

import java.time.Duration;
import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.interaction.InternalUserInteractor;

/**
 * Strategy asking a human for confirmation before propagation starts.
 */
public class HumanApprovalTriggerStrategy implements ChangePropagationTriggerStrategy {
  private static final String DEFAULT_PROMPT = "Propagate this change now?";
  private static final Duration DEFAULT_DELAY = Duration.ofDays(1);
  private static final int ACCEPT_OPTION = 0;
  private static final int DENY_OPTION = 1;
  private static final int DELAY_OPTION = 2;
  private final String prompt;
  private final Duration delay;
  private final Sleeper sleeper;

  /** Creates the strategy with a default prompt. */
  public HumanApprovalTriggerStrategy() {
    this(DEFAULT_PROMPT, DEFAULT_DELAY);
  }

  /**
   * Creates the strategy with a custom prompt.
   *
   * @param prompt dialog message shown to the user
   */
  public HumanApprovalTriggerStrategy(String prompt) {
    this(prompt, DEFAULT_DELAY);
  }

  /**
   * Creates the strategy with a custom prompt and delay duration for delayed propagation.
   *
   * @param prompt dialog message shown to the user
   * @param delay delay to apply when the user chooses to delay propagation
   */
  public HumanApprovalTriggerStrategy(String prompt, Duration delay) {
    this(prompt, delay, duration -> Thread.sleep(duration.toMillis()));
  }

  HumanApprovalTriggerStrategy(String prompt, Duration delay, Sleeper sleeper) {
    checkArgument(prompt != null && !prompt.isBlank(), "prompt must not be blank");
    checkArgument(delay != null && !delay.isNegative(), "delay must not be negative");
    checkArgument(sleeper != null, "sleeper must not be null");
    this.prompt = prompt;
    this.delay = delay;
    this.sleeper = sleeper;
  }

  @Override
  public boolean shouldPropagate(
      VitruviusChange<Uuid> change, InternalUserInteractor userInteractor) {
    checkArgument(userInteractor != null, "userInteractor must not be null");
    var answer =
        userInteractor
            .getSingleSelectionDialogBuilder()
            .message(prompt)
            .choices(java.util.List.of("Accept", "Deny", "Delay"))
            .title("Change Propagation")
            .positiveButtonText("Confirm")
            .cancelButtonText("Cancel")
            .startInteraction();

    if (answer == null || answer == DENY_OPTION) {
      return false;
    }
    if (answer == ACCEPT_OPTION) {
      return true;
    }
    if (answer == DELAY_OPTION) {
      return waitForDelayThenPropagate();
    }
    return false;
  }

  private boolean waitForDelayThenPropagate() {
    if (delay.isZero()) {
      return true;
    }
    try {
      sleeper.sleep(delay);
      return true;
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new IllegalStateException("Delayed propagation interrupted", e);
    }
  }

  @FunctionalInterface
  interface Sleeper {
    void sleep(Duration delay) throws InterruptedException;
  }
}
