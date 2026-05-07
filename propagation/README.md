# Propagation Strategies

The `propagation` module contains the trigger strategies that decide when a change should be propagated, and the orchestration code that executes propagation once a strategy allows it.

## Trigger strategies

All trigger strategies implement `ChangePropagationTriggerStrategy` and answer one question: should this incoming `VitruviusChange` be propagated now?

The module currently provides these implementations:

- `AlwaysPropagateTriggerStrategy`: always returns `true` and is used as the default.
- `HumanApprovalTriggerStrategy`: asks the user for confirmation through `InternalUserInteractor` and supports accept, deny, or delay.
- `DelayedTriggerStrategy`: waits for a configured `Duration` before allowing propagation.
- `MaturityBasedTriggerStrategy`: compares the maturity of the change and the source model, and only asks for approval when the change is less mature.

## How they are implemented

The strategies are intentionally small and stateless where possible:

- `AlwaysPropagateTriggerStrategy` is a simple no-op implementation.
- `HumanApprovalTriggerStrategy` builds a single-selection dialog and can pause before continuing when the user chooses delay.
- `DelayedTriggerStrategy` sleeps for the configured delay and logs that it is waiting.
- `MaturityBasedTriggerStrategy` uses functions that map a change to maturity levels, then falls back to `HumanApprovalTriggerStrategy` when manual approval is required.

The propagation flow itself starts in `DefaultChangeableModelRepository`. Its `propagateChange(...)` method checks the configured trigger strategy before delegating to `ChangePropagator`.

## How to use them

If you want propagation to always run, use the default repository constructor. It wires in `AlwaysPropagateTriggerStrategy` automatically.

To customize the behavior, pass your own strategy to `DefaultChangeableModelRepository`:

```java
var repository = new DefaultChangeableModelRepository(
    modelRepository,
    specificationProvider,
    userInteractor,
    new HumanApprovalTriggerStrategy("Propagate this change now?")
);
```

For runnable examples, see the `exampels` module. It contains one example each for always propagate, delayed propagation, human approval, and maturity-based propagation.