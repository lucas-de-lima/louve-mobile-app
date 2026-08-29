#!/bin/bash
# Initialize google-services.json for CI
# Creates a valid placeholder if the secret is unavailable or invalid.

set -e

if [ -n "$GOOGLE_SERVICES_JSON" ]; then
  echo "$GOOGLE_SERVICES_JSON" | base64 --decode > app/google-services.json
  if python -c "import json; json.load(open('app/google-services.json'))" 2>/dev/null; then
    echo "OK google-services.json decodificado"
    exit 0
  else
    echo "AVISO google-services.json invalido"
  fi
fi

# Create placeholder
python -c "
import json
payload = {
    'project_info': {'project_number': '0', 'project_id': 'ci-placeholder', 'storage_bucket': 'ci-placeholder'},
    'client': [{
        'client_info': {'mobilesdk_app_id': '1:0:android:ci-placeholder', 'android_client_info': {'package_name': 'com.lucasdelima.louveapp'}},
        'oauth_client': [], 'api_key': [{'current_key': 'ci-placeholder'}],
        'services': {'appinvite_service': {'other_platform_oauth_client': []}}
    }],
    'configuration_version': '1'
}
with open('app/google-services.json', 'w') as f:
    json.dump(payload, f, indent=2)
"
echo "OK google-services.json placeholder criado"