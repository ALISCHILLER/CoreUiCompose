#!/usr/bin/env bash
set -euo pipefail
export TERM="${TERM:-dumb}"

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
cd "$ROOT_DIR"

fail() {
  printf 'Static audit failed: %s\n' "$1" >&2
  exit 1
}

pass() {
  printf 'PASS %s\n' "$1"
}

if grep -RInE '^(<<<<<<<|=======|>>>>>>>)' \
  --exclude-dir=.git --exclude-dir=.gradle --exclude-dir=build --exclude='*.patch' .; then
  fail 'unresolved merge-conflict marker found'
fi
pass 'merge-conflict marker scan'

if grep -RInE 'TODO\(|error\("Not implemented|UnsupportedOperationException\(' \
  --include='*.kt' --include='*.java' \
  --exclude-dir=build --exclude-dir=test --exclude-dir=androidTest \
  core-ui-* catalog consumer-smoke maven-consumer maven-calendar-consumer 2>/dev/null; then
  fail 'runtime placeholder or unsupported operation found in production source'
fi
pass 'production runtime-placeholder scan'

if grep -RInE '(latest\.release|latest\.integration|[0-9]+\.\+)' \
  --include='*.gradle' --include='*.gradle.kts' --include='*.toml' \
  --exclude-dir=build .; then
  fail 'dynamic dependency version found'
fi
pass 'dynamic dependency version scan'

if grep -RInE 'allowInsecureProtocol[[:space:]]*=[[:space:]]*true|url[[:space:]]*=[[:space:]]*uri\("http://' \
  --include='*.gradle' --include='*.gradle.kts' --exclude-dir=build .; then
  fail 'insecure Gradle repository configuration found'
fi
pass 'Gradle repository transport scan'

if grep -RInE '^import androidx\.compose\.foundation\.layout\.(weight|matchParentSize)$' \
  --include='*.kt' --exclude-dir=build .; then
  fail 'scope-only Compose layout modifier imported as a top-level symbol'
fi

if grep -RInE '\bcubicTo\(' --include='*.kt' --exclude-dir=build .; then
  fail 'unsupported Compose vector path cubicTo call found; use curveTo'
fi

if grep -RInE '^import androidx\.compose\.ui\.input\.pointer\.consume$' \
  --include='*.kt' --exclude-dir=build .; then
  fail 'PointerInputChange.consume is a member API and must not be imported'
fi

if grep -RIn 'Dp.Zero' --include='*.kt' --exclude-dir=build .; then
  fail 'Dp.Zero is not compatible with the supported Compose toolchain; use 0.dp'
fi
pass 'Compose compatibility regression scan'

python3 - <<'PY_NAVIGATION'
from pathlib import Path
import re

app = Path('catalog/src/main/java/com/msa/coreui/catalog/CatalogApp.kt').read_text(encoding='utf-8')
pages = Path('catalog/src/main/java/com/msa/coreui/catalog/CatalogShowcasePages.kt')
tags = Path('catalog/src/main/java/com/msa/coreui/catalog/CatalogTestTags.kt').read_text(encoding='utf-8')
tests = Path('catalog/src/androidTest/java/com/msa/coreui/catalog/CatalogAppTest.kt').read_text(encoding='utf-8')

required_app_fragments = {
    'Navigation destination': 'CatalogPage.NAVIGATION',
    'Layout destination': 'CatalogPage.LAYOUT',
    'Navigation card routing': 'CategoryItem("navigation",',
    'Layout card routing': 'CategoryItem("layout",',
    'primary-page navigation mapping': 'val primaryPages = listOf(',
    'detail-page navigation fallback': 'val selectedNavigationIndex = primaryPages.indexOf(page)',
}
for label, fragment in required_app_fragments.items():
    if fragment not in app:
        raise SystemExit(f'Missing Catalog navigation contract: {label}')

if not re.search(r'CategoryItem\("navigation"[^\n]*CatalogPage\.NAVIGATION\)', app):
    raise SystemExit('Navigation category must route to CatalogPage.NAVIGATION')
if not re.search(r'CategoryItem\("layout"[^\n]*CatalogPage\.LAYOUT\)', app):
    raise SystemExit('Layout category must route to CatalogPage.LAYOUT')
for forbidden in ('selectedIndex = page.ordinal', 'page = CatalogPage.entries[index]'):
    if forbidden in app:
        raise SystemExit(f'Catalog detail navigation must not depend on enum ordinal: {forbidden}')

if not pages.is_file():
    raise SystemExit('Missing CatalogShowcasePages.kt')
page_source = pages.read_text(encoding='utf-8')
for function_name in ('NavigationShowcasePage', 'LayoutShowcasePage'):
    if f'fun {function_name}(' not in page_source:
        raise SystemExit(f'Missing real Catalog page: {function_name}')
for tag in ('NAVIGATION_SCREEN', 'LAYOUT_SCREEN'):
    if tag not in tags or tag not in tests:
        raise SystemExit(f'Missing Catalog UI regression coverage for {tag}')

print('PASS Catalog Navigation/Layout destination contract')
PY_NAVIGATION

if grep -RInE '^import androidx\.compose\.material\.' \
  --include='*.kt' --exclude-dir=build .; then
  fail 'Material 2 import found in production or test Kotlin source'
fi

if grep -RInE '\bHijrah(Date|Chronology)?\b|ZoneOffset\.ofHoursMinutes\([[:space:]]*3[[:space:]]*,[[:space:]]*30[[:space:]]*\)|UTC\+0?3:30' \
  --include='*.kt' --include='*.java' --exclude-dir=build .; then
  fail 'legacy chronology or fixed Iran offset found'
fi
pass 'Material/chronology/fixed-offset regression scan'

if find . -type f \( \
  -name '*.jks' -o -name '*.keystore' -o -name '*.p12' -o -name '*.pem' -o \
  -name 'google-services.json' -o -name 'GoogleService-Info.plist' -o \
  -name '.env' -o -name 'local.properties' -o -name 'secrets.properties' -o \
  -name 'keystore.properties' \
\) -not -path './.git/*' -print -quit | grep -q .; then
  fail 'sensitive file pattern found'
fi

if grep -RInE '(BEGIN (RSA |EC |OPENSSH )?PRIVATE KEY|AKIA[0-9A-Z]{16}|gh[pousr]_[A-Za-z0-9]{30,}|AIza[0-9A-Za-z_-]{35}|xox[baprs]-[A-Za-z0-9-]{20,})' \
  --exclude-dir=.git --exclude-dir=.gradle --exclude-dir=build .; then
  fail 'private key or high-confidence credential pattern found'
fi
pass 'secret/signing-material scan'

while IFS= read -r -d '' script; do
  bash -n "$script" || fail "shell syntax error in $script"
done < <(find scripts -type f -name '*.sh' -print0)
pass 'shell script syntax'

python3 - <<'PY'
from pathlib import Path
import ast

for path in Path('scripts').rglob('*.py'):
    ast.parse(path.read_text(encoding='utf-8'), filename=str(path))
print('PASS Python script syntax')
PY

python3 scripts/verify-prd-traceability.py
bash scripts/check-version-alignment.sh

python3 - <<'PY'
from __future__ import annotations

from pathlib import Path
import json
import re
import tomllib
import xml.etree.ElementTree as ET
import zipfile

SKIP_PARTS = {'.git', '.gradle', 'build'}
ANDROID_NS = '{http://schemas.android.com/apk/res/android}'


def included(path: Path) -> bool:
    return not any(part in SKIP_PARTS for part in path.parts)


xml_paths = [path for path in Path('.').rglob('*.xml') if included(path)]
for path in xml_paths:
    ET.parse(path)
print(f'PASS XML parsing ({len(xml_paths)} files)')

with Path('gradle/libs.versions.toml').open('rb') as source:
    tomllib.load(source)
print('PASS TOML parsing')

json_paths = [path for path in Path('.').rglob('*.json') if included(path)]
for path in json_paths:
    json.loads(path.read_text(encoding='utf-8'))
print(f'PASS JSON parsing ({len(json_paths)} files)')

text_extensions = {
    '.kt', '.kts', '.java', '.xml', '.toml', '.yml', '.yaml', '.md', '.properties',
    '.json', '.sh', '.py', '.txt', '.pro', '.conf',
}
trailing = []
for path in Path('.').rglob('*'):
    if not path.is_file() or not included(path) or path.suffix.lower() not in text_extensions:
        continue
    for line_number, line in enumerate(path.read_text(encoding='utf-8').splitlines(), start=1):
        if line.endswith((' ', '\t')):
            trailing.append(f'{path}:{line_number}')
if trailing:
    raise SystemExit('Trailing whitespace found:\n' + '\n'.join(trailing[:50]))
print('PASS trailing-whitespace scan')

wrapper_jar = Path('gradle/wrapper/gradle-wrapper.jar')
if not wrapper_jar.is_file():
    raise SystemExit('Missing Gradle wrapper JAR')
with zipfile.ZipFile(wrapper_jar) as archive:
    corrupt = archive.testzip()
    if corrupt is not None:
        raise SystemExit(f'Corrupt Gradle wrapper JAR entry: {corrupt}')
print('PASS Gradle wrapper JAR integrity')

wrapper = Path('gradle/wrapper/gradle-wrapper.properties').read_text(encoding='utf-8')
checksum_lines = [line for line in wrapper.splitlines() if line.startswith('distributionSha256Sum=')]
if len(checksum_lines) != 1 or not re.fullmatch(r'[0-9a-fA-F]{64}', checksum_lines[0].split('=', 1)[1].strip()):
    raise SystemExit('Gradle wrapper must declare one 64-character SHA-256 checksum')
print('PASS Gradle distribution checksum configuration')


def string_keys(path: str) -> set[str]:
    root = ET.parse(path).getroot()
    return {node.attrib['name'] for node in root.findall('string')}


english_keys = string_keys('catalog/src/main/res/values/strings.xml')
persian_keys = string_keys('catalog/src/main/res/values-fa/strings.xml')
if english_keys != persian_keys:
    missing_fa = sorted(english_keys - persian_keys)
    missing_en = sorted(persian_keys - english_keys)
    raise SystemExit(
        f'Catalog string resources are not bilingual: missing-fa={missing_fa}, missing-en={missing_en}'
    )
print(f'PASS bilingual catalog string parity ({len(english_keys)} keys)')

required_catalog_resources = [
    Path('catalog/src/main/res/xml/backup_rules.xml'),
    Path('catalog/src/main/res/xml/data_extraction_rules.xml'),
    Path('catalog/src/main/res/xml/locales_config.xml'),
]
missing = [str(path) for path in required_catalog_resources if not path.is_file()]
if missing:
    raise SystemExit(f'Missing catalog security/localization resources: {missing}')

manifest_path = Path('catalog/src/main/AndroidManifest.xml')
manifest = ET.parse(manifest_path).getroot()
application = manifest.find('application')
if application is None:
    raise SystemExit('Catalog manifest must declare an application')
required_application_values = {
    'label': '@string/app_name',
    'allowBackup': 'false',
    'usesCleartextTraffic': 'false',
    'supportsRtl': 'true',
    'localeConfig': '@xml/locales_config',
}
for attribute, expected in required_application_values.items():
    actual = application.attrib.get(ANDROID_NS + attribute)
    if actual != expected:
        raise SystemExit(
            f'Catalog application android:{attribute} must be {expected!r}, got {actual!r}'
        )
if application.attrib.get(ANDROID_NS + 'debuggable') == 'true':
    raise SystemExit('Catalog application must not set android:debuggable=true')

for component_name in ('activity', 'activity-alias', 'service', 'receiver', 'provider'):
    for component in application.findall(component_name):
        if component.find('intent-filter') is not None and ANDROID_NS + 'exported' not in component.attrib:
            raise SystemExit(
                f'{manifest_path}: {component_name} with intent-filter must declare android:exported'
            )
print('PASS catalog manifest security/localization contract')

settings = Path('settings.gradle.kts').read_text(encoding='utf-8')
configured_modules = set(re.findall(r'include\("(:[^"]+)"\)', settings))
expected_modules = {
    ':core-ui-foundation', ':core-ui-components', ':core-ui-calendar-core',
    ':core-ui-calendar', ':core-ui', ':core-ui-bom', ':catalog', ':consumer-smoke',
    ':maven-consumer', ':maven-calendar-consumer',
}
if configured_modules != expected_modules:
    raise SystemExit(
        f'Gradle module set mismatch: expected={sorted(expected_modules)}, actual={sorted(configured_modules)}'
    )
print(f'PASS Gradle module shape ({len(configured_modules)} modules)')

root_build = Path('build.gradle.kts').read_text(encoding='utf-8')
consumer_wiring_requirements = {
    'published consumer preBuild wiring': 'tasks.matching { it.name == "preBuild" }.configureEach',
    'publication dependency': 'dependsOn(publishAllPublicationsToBuildRepositoryRepository)',
    'configuration-cache isolation': 'notCompatibleWithConfigurationCache(',
}
missing_wiring = [
    label for label, fragment in consumer_wiring_requirements.items() if fragment not in root_build
]
if missing_wiring:
    raise SystemExit(
        'Published Maven consumer wiring is incomplete: ' + ', '.join(missing_wiring)
    )
print('PASS published Maven consumer task/configuration-cache wiring')

showcase = Path('docs/screenshots/app-showcase.png')
readme = Path('README.md').read_text(encoding='utf-8')
if not showcase.is_file() or 'docs/screenshots/app-showcase.png' not in readme:
    raise SystemExit('README showcase image is missing or not linked')
print('PASS README showcase path')

workflow_paths = sorted(Path('.github/workflows').glob('*.yml'))
if not workflow_paths:
    raise SystemExit('No GitHub Actions workflows found')
for path in workflow_paths:
    lines = path.read_text(encoding='utf-8').splitlines()
    if not any(line.startswith('permissions:') for line in lines):
        raise SystemExit(f'{path} must declare top-level permissions')
    if any('pull_request_target:' in line for line in lines):
        raise SystemExit(f'{path} uses pull_request_target, which is not allowed by this project policy')
    if any(re.match(r'^\s*permissions:\s*write-all\s*$', line) for line in lines):
        raise SystemExit(f'{path} grants write-all permissions')
    for index, line in enumerate(lines):
        match = re.search(r'uses:\s*([^\s]+)@([^\s]+)', line)
        if not match:
            continue
        action, reference = match.groups()
        if reference.lower() in {'main', 'master', 'latest', 'head'}:
            raise SystemExit(f'{path}:{index + 1} uses floating action ref {reference}')
        if action == 'actions/checkout':
            nearby = '\n'.join(lines[index + 1:index + 8])
            if not re.search(r'persist-credentials:\s*false', nearby):
                raise SystemExit(f'{path}:{index + 1} checkout must disable persisted credentials')
print(f'PASS GitHub workflow security baseline ({len(workflow_paths)} workflows)')
PY

if ! command -v ruby >/dev/null 2>&1; then
  fail 'Ruby is required for dependency-free YAML syntax validation'
fi
ruby -e 'require "yaml"; ARGV.each { |path| YAML.parse_file(path) }' \
  .github/dependabot.yml .github/workflows/*.yml
pass 'workflow/Dependabot YAML syntax'

printf 'Static audit passed.\n'
