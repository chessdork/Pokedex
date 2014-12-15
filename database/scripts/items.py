import json

with open('../res/raw/moves.json') as f:
    data = json.load(f)

for move in data['result']:
    enum = move['alias'].upper().replace('-','_')
    name = move['name']
    types = "PokemonType." + move['type']['name'].upper()
    accuracy = move['accuracy']
    power = move['power']
    pp = move['pp']
    category = "MoveCategory." + move['category'].upper().replace('-','_')
    description = move['description']
    
    print('%s("%s", %s, %d, %d, %d, %s, "%s"),' % (
        enum, name, types, accuracy, power, pp, category,
        description))
