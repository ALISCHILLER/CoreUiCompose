#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

python3 - <<'PY'
import re
from pathlib import Path

props = {}
for line in Path('gradle.properties').read_text(encoding='utf-8').splitlines():
    if '=' in line and not line.lstrip().startswith('#'):
        key, value = line.split('=', 1)
        props[key.strip()] = value.strip()

version = props.get('VERSION_NAME')
if not version:
    raise SystemExit('VERSION_NAME is missing from gradle.properties')

catalog = Path('catalog/build.gradle.kts').read_text(encoding='utf-8')
match = re.search(r'versionName\s*=\s*"([^"]+)"', catalog)
if not match:
    raise SystemExit('Catalog versionName was not found')

catalog_version = match.group(1)
base_version = version.removesuffix('-SNAPSHOT')
if catalog_version != base_version:
    raise SystemExit(f'Catalog versionName {catalog_version} does not align with library version {version}')

version_code_match = re.search(r'versionCode\s*=\s*(\d+)', catalog)
if not version_code_match:
    raise SystemExit('Catalog versionCode was not found')
version_code = int(version_code_match.group(1))
try:
    major, minor, patch = (int(part) for part in base_version.split('.'))
except ValueError as error:
    raise SystemExit(f'VERSION_NAME must use semantic major.minor.patch format: {base_version}') from error
expected_version_code = major * 10000 + minor * 100 + patch
if version_code != expected_version_code:
    raise SystemExit(
        f'Catalog versionCode {version_code} does not align with {base_version}; '
        f'expected {expected_version_code}'
    )

print(
    f'Version alignment passed: library={version}, catalog={catalog_version}, '
    f'versionCode={version_code}'
)
PY
