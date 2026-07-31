#!/usr/bin/env python3
from __future__ import annotations
import json, re, sys
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
ALLOWED={'standalone-test-passed','partially-verified','test-defined-not-executed','configured-not-executed','build-verification-required','statically-reviewed'}

def fail(message:str)->None:
    print(f'Traceability verification failed: {message}',file=sys.stderr)
    raise SystemExit(1)

prd=(ROOT/'PRD.md').read_text(encoding='utf-8')
requirements_body=prd.split('## 20. ماتریس ردیابی نیازمندی‌ها',1)[0]
prd_ids=re.findall(r'\b(?:FR|NFR)-\d{3}\b',requirements_body)
if len(prd_ids)!=len(set(prd_ids)):
    fail('duplicate requirement ID in normative requirement sections of PRD.md')

trace=json.loads((ROOT/'release/requirements-traceability.json').read_text(encoding='utf-8'))
items=trace.get('requirements',[])
trace_ids=[item.get('id') for item in items]
if len(trace_ids)!=len(set(trace_ids)):
    fail('duplicate requirement ID in traceability JSON')
if set(prd_ids)!=set(trace_ids):
    fail(f'PRD/traceability mismatch; missing={sorted(set(prd_ids)-set(trace_ids))}, extra={sorted(set(trace_ids)-set(prd_ids))}')

gates=json.loads((ROOT/'release/release-gates.json').read_text(encoding='utf-8'))
gate_ids={g['id'] for g in gates.get('gates',[])}
for item in items:
    for field in ('id','module','gate','status','verification','evidence'):
        if not item.get(field): fail(f"{item.get('id','<unknown>')} missing {field}")
    if item['gate'] not in gate_ids: fail(f"{item['id']} references unknown gate {item['gate']}")
    if item['status'] not in ALLOWED: fail(f"{item['id']} uses unsupported status {item['status']}")
    for evidence in item['evidence']:
        if not (ROOT/evidence).exists(): fail(f"{item['id']} evidence path does not exist: {evidence}")

manifest=json.loads((ROOT/'release/release-manifest.json').read_text(encoding='utf-8'))
statuses={g['status'] for g in gates['gates']}
if manifest.get('status')=='production-ready' and statuses!={'passed'}:
    fail('manifest declares production-ready while one or more release gates are not passed')
print(f'Traceability verified: {len(items)} requirements, {len(gate_ids)} gates.')
