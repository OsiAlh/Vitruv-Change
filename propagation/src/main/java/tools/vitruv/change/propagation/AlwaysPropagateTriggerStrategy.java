package tools.vitruv.change.propagation;

import tools.vitruv.change.atomic.uuid.Uuid;
import tools.vitruv.change.composite.description.VitruviusChange;
import tools.vitruv.change.interaction.InternalUserInteractor;

/**
 * Default strategy that always allows propagation.
 */
public class AlwaysPropagateTriggerStrategy implements ChangePropagationTriggerStrategy {

  @Override
  public boolean shouldPropagate(
      VitruviusChange<Uuid> change, InternalUserInteractor userInteractor) {
    return true;
  }
}
