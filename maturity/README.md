# Maturity

Provides runtime tracking of maturity levels for EMF model objects.

## Concepts

A model object (`EObject`) can be in one of three maturity levels, ordered by increasing
confidence:

| Level | Meaning |
|---|---|
| `DRAFT` | Work in progress, subject to change |
| `REVIEWED` | Checked and approved, but not yet locked |
| `FINAL` | Stable, no further changes expected |

Every level transition is recorded with a timestamp, forming a full history for each tracked
object.

## API

### `MaturityManager`

Tracks the maturity state of a single `EObject`.

```java
MaturityManager manager = new MaturityManager(myObject);
manager.updateMaturityLevel(MaturityLevelEnum.REVIEWED);

MaturityLevelEnum level = manager.getCurrentLevel(); // REVIEWED
boolean done = manager.isFinal();                    // false
MaturityState state = manager.getState();            // defensive copy with full history
```

### `MaturityUtil`

Static facade for managing maturity across multiple objects without holding `MaturityManager`
references manually.

```java
MaturityUtil.setMaturityLevel(myObject, MaturityLevelEnum.FINAL);
MaturityLevelEnum level = MaturityUtil.getMaturityLevel(myObject);
boolean reviewed = MaturityUtil.isReviewed(myObject);
MaturityUtil.removeMaturityTracking(myObject);
```

## Relation to propagation

The `MaturityBasedTriggerStrategy` in the `propagation` module uses maturity levels to decide
whether a change may be propagated automatically or requires human approval.
