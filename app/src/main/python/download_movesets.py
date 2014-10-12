#/usr/bin/python -tt
import json
import urllib.request
import requests

serverbase = 'http://www.smogon.com/dex/api/query?q='
query_part_1='{"pokemon":{"gen":"xy","alias":"'
query_part_2='"},"$":["name","alias","gen",{"genfamily":["alias","gen"]},{"alts":["alias","suffix","height","weight","gkpower",{"types":["alias","name","gen"]},{"$groupby":"modifier","effectives":["modifier",{"type":["alias","name","gen"]}]},{"abilities":["alias","name","gen","description"]},{"tags":["name","alias","shorthand","gen"]},"hp","patk","pdef","spatk","spdef","spe"]},{"family":["root",{"members":["name","alias","gen"]},{"evolutions":["preevo","evo"]}],"$tree":["root",["members","name"],["evolutions","preevo","evo"]]},{"movesets":["name",{"tags":["alias","shorthand","name","gen"]},{"items":["alias","name","gen","description"]},{"abilities":["alias","name","gen"]},{"evconfigs":["hp","patk","pdef","spatk","spdef","spe"]},{"natures":["hp","patk","pdef","spatk","spdef","spe"]},{"$groupby":"slot","moveslots":["slot",{"move":["name","alias","gen"]}]},"description"]},{"moves":["name","alias","gen","category","power","accuracy","pp","description",{"type":["alias","name","gen"]}]}]}'

file = open('../res/raw/pokemon.json')
dex = json.load(file)

pokelist = dex['result']
aliases = set()

for poke in pokelist:
    alias = poke['base_alias']

    if not alias in aliases:
        aliases.add(alias)
        path = 'out/movesets/' + alias + '.json'
        out = open(path, 'w+')

        url = serverbase + query_part_1 + alias + query_part_2
        response = requests.get(url)
        data = response.json()

        out.write(json.dumps(data, indent=1, separators=(',', ': ')))
        out.flush()
        out.close()

