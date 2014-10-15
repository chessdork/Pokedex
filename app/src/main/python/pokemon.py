import json

with open('../res/raw/pokemon.json') as f:
    data = json.load(f)

for p in data['result']:
    enum = p['alias'].upper().replace('-','_')
    name = p['name']
    base_alias = p['base_alias']
    hp = p['hp']
    attack = p['patk']
    defense = p['pdef']
    specialAttack = p['spatk']
    specialDefense = p['spdef']
    speed = p['spe']
    # check for "Unreleased" or other tags longer than 4 characters.
    tag = p['tags'][0]['shorthand']

    types = ', '.join(['PokemonType.%s' % a['alias'].upper() for a in p['types']])
    abilities = ', '.join(['Ability.%s' % a['alias'].upper() for a in p['abilities']])


    print('%s("%s", "%s", Arrays.asList(%s), Arrays.asList(%s), %d, %d, %d, %d, %d, %d, "%s"),' % (
        enum, name, base_alias, types, abilities, hp, attack, defense, specialAttack, specialDefense, speed, tag))
