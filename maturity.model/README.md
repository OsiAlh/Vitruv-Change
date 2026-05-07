# Maturity Model

EMF-based meta-model defining the data structures used by the `maturity` module.

## Model elements

### `MaturityLevelEnum`
Enumeration of the three maturity levels: `DRAFT` · `REVIEWED` · `FINAL`.

### `MaturityState`
Holds the complete maturity history of one `EObject`.

| Feature | Type | Description |
|---|---|---|
| `target` | `EObject` | The tracked model object |
| `history` | `EList<MaturityChange>` | Ordered list of all level transitions |

### `MaturityChange`
A single recorded level transition.

| Feature | Type | Description |
|---|---|---|
| `date` | `Date` | When the transition occurred |
| `oldLevel` | `MaturityLevelEnum` | Previous level |
| `newLevel` | `MaturityLevelEnum` | New level |

## Source

The model is defined in `src/main/maturity.ecore`. The Java classes in
`target/generated-sources/ecore/` are generated from it via the `maturity.genmodel` and should
not be edited manually.
