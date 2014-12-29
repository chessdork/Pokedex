#/usr/bin/python -tt
import requests
import os.path
import shutil
import json

out_dir = '../../app/src/main/res/drawable'

serverbase = 'http://www.pokestadium.com/assets/img/sprites/official-artwork/'

pokemon = '../data/pokemon.json'

with open(pokemon) as pokes:
    data = json.load(pokes)

    for p in data:
        name = p['name'].title()
        national_id = p['national_id']

        if national_id > 4 and national_id < 720:
            out_file = os.path.join(out_dir, 'ic_pokemon_' + str(national_id) + '.png')
            print(out_file)

            with open(out_file, 'wb+') as f:
                url = serverbase + str(national_id) + '-' + name + '.png'
                response = requests.get(url, stream=True)
                
                shutil.copyfileobj(response.raw, f)
    
    

