import json

with open('pokemon.json') as f:
    data = json.load(f)

for p in data:
    enum = p['alias'].upper().replace('-','_')
    name = p['name']
    base_alias = p['base_alias']
    hp = p['hp']
    attack = p['patk']
    defense = p['pdef']
    specialAttack = p['spatk']
    specialDefense = p['spdef']
    speed = p['spe']

    types = ', '.join(['PokemonType.%s' % a['alias'].upper() for a in p['types']])
    abilities = ', '.join(['Ability.%s' % a['alias'].upper() for a in p['abilities']])


    print('%s("%s", "%s", Arrays.asList(%s), Arrays.asList(%s), %d, %d, %d, %d, %d, %d),' % (
        enum, name, base_alias, types, abilities, hp, attack, defense, specialAttack, specialDefense, speed))
