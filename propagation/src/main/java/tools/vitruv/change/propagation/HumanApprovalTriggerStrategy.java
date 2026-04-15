package tools.vitruv.change.propagation;

import static com.google.common.base.Preconditions.checkArgument;

import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.interaction.InternalUserInteractor;

/**
 * Strategy asking a human for confirmation before propagation starts.
 */
public class HumanApprovalTriggerStrategy implements ChangePropagationTriggerStrategy {
  private static final String DEFAULT_PROMPT = "Propagate this change now?";
  private final String prompt;

  /** Creates the strategy with a default prompt. */
  public HumanApprovalTriggerStrategy() {
    this(DEFAULT_PROMPT);
  }

  /**
   * Creates the strategy with a custom prompt.
   *
   * @param prompt dialog message shown to the user
   */
  public HumanApprovalTriggerStrategy(String prompt) {
    checkArgument(prompt != null && !prompt.isBlank(), "prompt must not be blank");
    this.prompt = prompt;
  }

  @Override
  public boolean shouldPropagate(
      VitruviusChange<Uuid> change, InternalUserInteractor userInteractor) {
    checkArgument(userInteractor != null, "userInteractor must not be null");
    var answer =
        userInteractor
            .getConfirmationDialogBuilder()
            .message(prompt)
            .title("Change Propagation")
            .positiveButtonText("Propagate")
            .negativeButtonText("Skip")
            .startInteraction();
    return Boolean.TRUE.equals(answer);
  }
}
