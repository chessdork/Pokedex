#/usr/bin/python -tt
import json
import urllib.request
import requests

out = '../data/pokemon_api.json'

serverbase = 'http://pokeapi.co'
dex_query = '/api/v1/pokedex/1'

pokemon = list()
i = 0

with open(out, 'w+') as f:
    url = serverbase + dex_query
    response = requests.get(url)
    data = response.json()
  
    for poke in data['pokemon']:
        name = poke['name']
        poke_query = '/' + poke['resource_uri']

        url = serverbase + poke_query
        response = requests.get(url)
        data = response.json()

        # rename "compoundeyes" to "compound eyes"
        data.replace('compoundeyes', 'compound eyes')

        pokemon.append(data);
        print(i)
        i = i + 1
    
    json.dump(pokemon, f)
    f.flush()
