#!/bin/bash
# Always start with a valid placeholder, then upgrade if secret is valid

python -c "
import json
json.dump({
    'project_info': {'project_number': '0', 'project_id': 'ci-placeholder', 'storage_bucket': 'ci-placeholder'},
    'client': [{
        'client_info': {'mobilesdk_app_id': '1:0:android:ci-placeholder', 'android_client_info': {'package_name': 'com.lucasdelima.louveapp'}},
        'oauth_client': [], 'api_key': [{'current_key': 'ci-placeholder'}],
        'services': {'appinvite_service': {'other_platform_oauth_client': []}}
    }],
    'configuration_version': '1'
}, open('app/google-services.json', 'w'), indent=2)
"
echo "OK google-services.json placeholder criado"

# Upgrade with real secret if available and valid
if [ -n "$GOOGLE_SERVICES_JSON" ]; then
  TEMP=$(echo "$GOOGLE_SERVICES_JSON" | base64 --decode 2>/dev/null)
  if echo "$TEMP" | python -c "import sys,json; json.load(sys.stdin)" 2>/dev/null; then
    echo "$TEMP" > app/google-services.json
    echo "OK google-services.json decodificado com sucesso"
  else
    echo "AVISO google-services.json invalido, usando placeholder"
  fi
fi