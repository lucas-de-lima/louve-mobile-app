import subprocess, json

query = open(r"D:\Projetos\harness-agentic-sdlc-base\project\louve-mobile-app\scripts\get_items.graphql").read()
result = subprocess.run(["gh", "api", "graphql", "-f", f"query={query}"], capture_output=True, text=True)
data = json.loads(result.stdout)
items = data["data"]["node"]["items"]["nodes"]

cards = []
for item in items:
    content = item.get("content")
    if content and content.get("__typename") == "DraftIssue":
        title = content.get("title", "")
        if title.startswith("Epic") or title.startswith("F-") or title.startswith("US"):
            cards.append({"id": item["id"], "title": title})

print(f"Found {len(cards)} cards")
with open(r"D:\Projetos\harness-agentic-sdlc-base\project\louve-mobile-app\scripts\card_ids.py", "w") as f:
    f.write("cards = ")
    json.dump(cards, f, indent=2)