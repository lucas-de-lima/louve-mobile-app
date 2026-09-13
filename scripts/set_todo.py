import subprocess, json

# Set all new features and US to Todo status
d = subprocess.run(['gh','issue','list','--state','open','--limit','300','--json','number'], capture_output=True, text=True)
open_issues = [u['number'] for u in json.loads(d.stdout) if u['number'] >= 164]
print('Issues novas:', len(open_issues))

q = open(r'D:\Projetos\harness-agentic-sdlc-base\project\louve-mobile-app\scripts\get_items.graphql').read()
r = subprocess.run(['gh','api','graphql','-f',f'query={q}'], capture_output=True, text=True)
data = json.loads(r.stdout)
items = data['data']['node']['items']['nodes']

mutation = "mutation($project:ID!,$item:ID!,$field:ID!,$value:String!){updateProjectV2ItemFieldValue(input:{projectId:$project itemId:$item fieldId:$field value:{singleSelectOptionId:$value}}){clientMutationId}}"

target = set(open_issues)
count = 0
for item in items:
    content = item.get('content')
    if content and content.get('__typename') == 'Issue':
        iq = 'query{node(id:"' + item['id'] + '"){...on ProjectV2Item{content{...on Issue{number}}}}}'
        r2 = subprocess.run(['gh','api','graphql','-f',f'query={iq}'], capture_output=True, text=True)
        try:
            num = json.loads(r2.stdout)['data']['node']['content']['number']
            if num in target:
                subprocess.run(['gh','api','graphql','-f',f'query={mutation}','-F','project=PVT_kwHOBn6SPs4BhJgi','-F',f'item={item["id"]}','-F','field=PVTSSF_lAHOBn6SPs4BhJgizhgGXN8','-F','value=f75ad846'], capture_output=True)
                count += 1
        except Exception:
            pass
print(f'Set {count} novos cards para Todo')