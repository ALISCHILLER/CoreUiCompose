#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

required_paths=(
  "PRD.md"
  "README.md"
  "LICENSE"
  "NOTICE"
  "CHANGELOG.md"
  "MIGRATION.md"
  "RELEASE_CHECKLIST.md"
  "release/release-manifest.json"
  "release/release-gates.json"
  "release/requirements-traceability.json"
  "docs/REQUIREMENTS_TRACEABILITY.md"
  "core-ui-bom/build.gradle.kts"
  "core-ui-calendar-core/build.gradle.kts"
  "core-ui-foundation/build.gradle.kts"
  "core-ui-components/build.gradle.kts"
  "core-ui-calendar/build.gradle.kts"
  "core-ui/build.gradle.kts"
  "maven-calendar-consumer/build.gradle.kts"
)

for path in "${required_paths[@]}"; do
  if [[ ! -e "$path" ]]; then
    echo "Missing required distribution path: $path" >&2
    exit 1
  fi
done

python3 - <<'PY'
import json
from pathlib import Path
manifest = json.loads(Path('release/release-manifest.json').read_text(encoding='utf-8'))
required = {
    'core-ui-bom',
    'core-ui-calendar-core',
    'core-ui-foundation',
    'core-ui-components',
    'core-ui-calendar',
    'core-ui',
}
actual = set(manifest.get('artifacts', []))
if actual != required:
    raise SystemExit(f'Artifact manifest mismatch: expected {sorted(required)}, got {sorted(actual)}')
if manifest.get('status') == 'production-ready':
    raise SystemExit('Source distribution must not declare production-ready before runtime release gates pass.')
if manifest.get('requirementsTraceability') != 'release/requirements-traceability.json':
    raise SystemExit('Release manifest does not reference the traceability source of truth.')
if manifest.get('releaseGates') != 'release/release-gates.json':
    raise SystemExit('Release manifest does not reference the gate status source of truth.')
print('Release manifest validated.')
PY

if find . -type f \( -name '*.jks' -o -name '*.keystore' -o -name '*.p12' -o -name '*.pem' \) -print -quit | grep -q .; then
  echo "Signing/private key material must not be present in the source distribution." >&2
  exit 1
fi

bash scripts/static-audit.sh

echo "Distribution verification passed."
