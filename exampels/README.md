# Change Propagation Examples

Runnable examples demonstrating the four `ChangePropagationTriggerStrategy` implementations.

## Scenario

A safety engineer changes the **braking distance** requirement from 50 m to 40 m
(`MechanicalRequirements`). Depending on the active strategy, this change is propagated to the
**target deceleration** in `SoftwareRequirements` immediately, after a delay, or only after
human approval.

## Examples

| Class | Strategy | Behaviour |
|---|---|---|
| `AlwaysPropagateExample` | `AlwaysPropagateTriggerStrategy` | Propagates immediately, no interaction |
| `DelayedPropagationExample` | `DelayedTriggerStrategy` | Waits 5 seconds, then propagates |
| `HumanApprovalExample` | `HumanApprovalTriggerStrategy` | Prompts for Accept / Deny / Delay (5 s) |
| `MaturityBasedExample` | `MaturityBasedTriggerStrategy` | Auto-propagates if change maturity ≥ model maturity; otherwise asks for approval |

## Build & Run

**Prerequisites:** build and install the main project first.

```bash
cd ..
mvn install -DskipTests
```

**Compile and collect dependencies** (run once from `exampels/`):

```bash
mvn compile dependency:copy-dependencies
```

**Run an example:**

```bash
java -cp "target/classes:target/dependency/*" tools.vitruv.change.propagation.examples.AlwaysPropagateExample
java -cp "target/classes:target/dependency/*" tools.vitruv.change.propagation.examples.DelayedPropagationExample
java -cp "target/classes:target/dependency/*" tools.vitruv.change.propagation.examples.HumanApprovalExample
java -cp "target/classes:target/dependency/*" tools.vitruv.change.propagation.examples.MaturityBasedExample
```

For `HumanApprovalExample` and `MaturityBasedExample` (Scenario B), type `0` to accept,
`1` to deny, or `2` to delay when prompted.
