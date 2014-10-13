import json

with open('../res/raw/items.json') as f:
    data = json.load(f)

for move in data['result']:
    enum = move['alias'].upper().replace('-','_')
    name = move['name']
    description = move['description']
    
    print('%s("%s", "%s"),' % (
        enum, name, description))
