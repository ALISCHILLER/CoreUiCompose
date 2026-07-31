# Release Control Files

This directory contains the machine-readable release control plane for MSA Core UI.

- `release-manifest.json`: artifact identity, target platform, version, and required gates.
- `release-gates.json`: current release decision, gate status, evidence, and exit criteria.
- `requirements-traceability.json`: mapping from every `FR-*` requirement in `PRD.md` to a module, evidence, verification method, gate, and current status.

The source package is intentionally marked as a release candidate. A stable or production-ready declaration is prohibited until every gate in `release-gates.json` is `passed` and the source revision plus checksums are recorded by the release workflow.
