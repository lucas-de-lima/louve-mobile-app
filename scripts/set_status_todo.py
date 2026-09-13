import subprocess, json

project_id = "PVT_kwHOBn6SPs4BhJgi"
field_id = "PVTSSF_lAHOBn6SPs4BhJgizhgGXN8"
todo_id = "f75ad846"

# First get all items in the project and their IDs
q = open(r'D:\Projetos\harness-agentic-sdlc-base\project\louve-mobile-app\scripts\get_items.graphql').read()
r = subprocess.run(['gh', 'api', 'graphql', '-f', f'query={q}'], capture_output=True, text=True)
data = json.loads(r.stdout)
items = data['data']['node']['items']['nodes']

# Filter to issues #113-#163
target_nums = set(range(113, 164))
mutation = "mutation($project:ID!,$item:ID!,$field:ID!,$value:String!){updateProjectV2ItemFieldValue(input:{projectId:$project itemId:$item fieldId:$field value:{singleSelectOptionId:$value}}){clientMutationId}}"

count = 0
for item in items:
    content = item.get('content')
    if content and content.get('__typename') == 'Issue':
        # Try to get the issue number
        issues_q = 'query{node(id:"' + item['id'] + '"){...on ProjectV2Item{content{...on Issue{number}}}}}'
        r2 = subprocess.run(['gh', 'api', 'graphql', '-f', f'query={issues_q}'], capture_output=True, text=True)
        try:
            d2 = json.loads(r2.stdout)
            num = d2['data']['node']['content']['number']
            if num in target_nums:
                subprocess.run(['gh', 'api', 'graphql', '-f', f'query={mutation}', '-F', f'project={project_id}', '-F', f'item={item["id"]}', '-F', f'field={field_id}', '-F', f'value={todo_id}'], capture_output=True)
                count += 1
                print(f'Set #{num} to Todo')
        except:
            pass

print(f'\nSet {count} issues to Todo status')