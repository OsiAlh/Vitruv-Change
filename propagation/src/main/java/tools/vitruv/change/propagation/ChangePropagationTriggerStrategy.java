package tools.vitruv.change.propagation;

import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.interaction.InternalUserInteractor;

/**
 * Strategy deciding whether a change should be propagated now.
 */
@FunctionalInterface
public interface ChangePropagationTriggerStrategy {

  /**
   * Decides whether propagation should be executed for the given input change.
   *
   * @param change the incoming change to be propagated
   * @param userInteractor user interaction facility that can be used to ask for confirmation
   * @return {@code true} if propagation should proceed, {@code false} otherwise
   */
  boolean shouldPropagate(VitruviusChange<Uuid> change, InternalUserInteractor userInteractor);
}
