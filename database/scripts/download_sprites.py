#/usr/bin/python -tt
import requests
import os.path
import shutil

out_dir = '../../app/src/main/res/drawable'

serverbase = 'http://pokeapi.co'
sprite_query = '/media/img/'

for poke_id in range(1, 719):
    out_file = os.path.join(out_dir, 'ic_pokemon_' + str(poke_id) + '.png')
    
    with open(out_file, 'wb+') as f:
        url = serverbase + sprite_query + str(poke_id) + '.png'
        response = requests.get(url, stream=True)
        
        shutil.copyfileobj(response.raw, f)
