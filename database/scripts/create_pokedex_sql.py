import json
import csv
import sqlite3
import os

# output database
path = '../../app/src/main/assets/databases/pokedex.db'

def create_gens(cursor):
    # create generations table  
    cursor.execute('create table if not exists gens('
                   'id integer primary key, '
                   'name text not null unique '
                   ');')

    # add a few entries
    cursor.execute('insert or ignore into gens(name)'
              'values ("xy"), ("oras");')

def create_abilities(cursor):
    abilities = '../data/abilities.json'
    
    # create abilities table
    cursor.execute('create table if not exists abilities('
                   'id integer primary key, '
                   'name text not null unique, '
                   'description text not null, '
                   'gen_id integer not null, '
                   'foreign key(gen_id) references gens(id)'
                   ');')

    with open(abilities) as f:
        data = json.load(f)

        for p in data['result']:
            name = p['name']
            description = p['description']
            gen = (p['gen'],)

            cursor.execute('select id from gens where name=?', gen)
            gen_id = cursor.fetchone()[0]

            values = (name, description, gen_id)
        
            cursor.execute('insert or ignore into abilities(name, description, gen_id)'
                           'values (?,?,?);', values)

def create_types(cursor):
    types = '../data/types.csv'
    
    cursor.execute('create table if not exists types('
                   'id integer primary key, '
                   'name text not null unique, '
                   'grad_start_color text not null, '
                   'grad_end_color text not null, '
                   'border_color text not null'
                   ');')

    with open(types) as f:
        data = csv.reader(f)
        next(data) #skip header row

        for t in data:
            name = t[0]
            grad_start_color = t[1]
            grad_end_color = t[2]
            border_color = t[3]

            values = (name, grad_start_color, grad_end_color, border_color)
            
            cursor.execute('insert or ignore into types(name, grad_start_color,'
                           'grad_end_color, border_color) '
                           'values (?,?,?,?);', values)

def create_items(cursor):
    machines = '../data/machines.csv'

    cursor.execute('create table if not exists machines('
                   'id integer primary key, '
                   'name text not null, '
                   'move_id integer not null, '
                   'location text not null, '
                   'gen_id integer not null, '
                   'youtube_id text, '
                   'start_time integer, '
                   'end_time integer, '
                   'foreign key(gen_id) references gens(id), '
                   'foreign key(move_id) references moves(id)'
                   ');')

    with open(machines) as f:
        data = csv.reader(f)
        next(data) #skip header row

        for m in data:
            name = m[0]
            move = m[1]
            location = m[2]
            gen = m[3]
            youtube_id = m[4]
            start_time = m[5]
            end_time = m[6]

            cursor.execute('select id from moves where name=?', (move,))
            move_id = cursor.fetchone()[0]

            cursor.execute('select id from gens where name=?', (gen,))
            gen_id = cursor.fetchone()[0]

            values = (name, move_id, location, gen_id, youtube_id, start_time, end_time)
            cursor.execute('insert or ignore into machines('
                           'name, move_id, location, gen_id, youtube_id, start_time, end_time)'
                           'values (?,?,?,?,?,?,?);', values)
    
def create_moves(cursor):
    moves = '../data/moves.csv'

    cursor.execute('create table if not exists move_categories('
                   'id integer primary key, '
                   'name text not null unique);')
    
    cursor.execute('insert or ignore into move_categories(name)'
                   'values ("Special"), ("Physical"), ("Non-Damaging");')

    cursor.execute('create table if not exists moves('
                   'id integer primary key, '
                   'name text not null unique, '
                   'type_id integer not null, '
                   'accuracy integer not null, '
                   'power integer not null, '
                   'pp integer not null, '
                   'move_category_id integer not null, '
                   'description text not null, '
                   'foreign key(type_id) references types(id), '
                   'foreign key(move_category_id) references move_categories(id)'
                   ');')
    
    with open(moves) as f:
        data = csv.reader(f)
        #no header row

        for m in data:
            name = m[0]
            move_type = m[1]
            accuracy = m[2]
            power = m[3]
            pp = m[4]
            move_category = m[5]
            description = m[6]

            cursor.execute('select id from types where name=?', (move_type,))
            move_type_id = cursor.fetchone()[0]

            cursor.execute('select id from move_categories where name=?', (move_category,))
            move_category_id = cursor.fetchone()[0]

            values = (name, move_type_id, accuracy, power, pp, move_category_id, description)
            cursor.execute('insert or ignore into moves('
                           'name, type_id, accuracy, power, pp, move_category_id, description)'
                           'values (?,?,?,?,?,?,?);', values)

def create_pokemon(cursor):
    pokemon = '../data/pokemon.json'
    oras_megas = '../data/oras_megas.csv'
    pokemon_level_moves = '../data/oras_level-up_moves.txt'
    
    cursor.execute('create table if not exists pokemon('
                   'id integer primary key, '
                   'name text unique not null, '
                   'national_id integer unique not null, '
                   'hp integer not null, '
                   'atk integer not null, '
                   'def integer not null, '
                   'spatk integer not null, '
                   'spdef integer not null, '
                   'speed integer not null, '
                   'image_resource_name text not null'
                   ');')

    cursor.execute('create table if not exists pokemon_abilities('
                   'id integer primary key, '
                   'pokemon_id integer not null, '
                   'ability_id integer not null, '
                   'foreign key(pokemon_id) references pokemon(id), '
                   'foreign key(ability_id) references abilities(id)'
                   ');')

    cursor.execute('create table if not exists pokemon_types('
                   'id integer primary key, '
                   'pokemon_id integer not null, '
                   'type_id integer not null, '
                   'foreign key(pokemon_id) references pokemon(id), '
                   'foreign key(type_id) references types(id)'
                   ');')

    cursor.execute('create table if not exists pokemon_machine_moves('
                   'id integer primary key, '
                   'pokemon_id integer not null, '
                   'machine_id integer not null, '
                   'foreign key(pokemon_id) references pokemon(id), '
                   'foreign key(machine_id) references machines(id)'
                   ');')

    cursor.execute('create table if not exists pokemon_level_moves('
                   'id integer primary key, '
                   'pokemon_id integer not null, '
                   'move_id integer not null, '
                    'level integer not null, '
                   'foreign key(pokemon_id) references pokemon(id), '
                   'foreign key(move_id) references moves(id)'
                   ');')

    with open(pokemon) as f:
        data = json.load(f)

        for p in data:
            name = p['name'].title()
            hp = p['hp']
            national_id = p['national_id']
            patk = p['attack']
            pdef = p['defense']
            spatk = p['sp_atk']
            spdef = p['sp_def']
            spe = p['speed']

            if "Farfetch'D" == name:
                name = "Farfetch'd"

            #Hard-code resources for Pumpkaboo and Gourgeist sizes.
            if "Pumpkaboo" in name:
                res = 'ic_pokemon_710'
            elif "Gourgeist" in name:
                res = 'ic_pokemon_711'
            else:
                res = 'ic_pokemon_' + str(national_id)
                
            values = (name, national_id, hp, patk, pdef, spatk, spdef, spe, res)

            sprite_file = os.path.join('../../app/src/main/res/drawable/', res + '.png')
            if not os.path.isfile(sprite_file):
                print('WARNING: sprite does not exist for', name, national_id)
    
            cursor.execute('insert into pokemon('
                           'name, national_id, hp, atk, def, spatk, spdef, speed, image_resource_name)'
                           'values (?,?,?,?,?,?,?,?,?);', values)
            cursor.execute('select id from pokemon where name=?', (name,))
            pokemon_id = cursor.fetchone()[0]

            for a in p['abilities']:
                ability = (a['name'].replace('-',' ').title(),)

                cursor.execute('select id from abilities where name=?', ability)
                ability_id = cursor.fetchone()[0]

                values = (pokemon_id, ability_id)

                cursor.execute('insert or ignore into pokemon_abilities('
                               'pokemon_id, ability_id)'
                               'values (?,?);', values)
                
            for t in p['types']:
                poke_type = (t['name'].title(),)

                cursor.execute('select id from types where name=?', poke_type)
                type_id = cursor.fetchone()[0]

                values = (pokemon_id, type_id)
                cursor.execute('insert or ignore into pokemon_types('
                               'pokemon_id, type_id)'
                               'values (?,?);', values)               

    # read in pokemon that pokeapi is missing.
    with open(oras_megas) as f:
        data = csv.reader(f)
        next(data) #skip header row

        for m in data:
            name = m[0]
            national_id = m[1]
            ability = m[2]
            type1 = m[3]
            type2 = m[4]
            hp = m[5]
            patk = m[6]
            pdef = m[7]
            spatk = m[8]
            spdef = m[9]
            speed = m[10]
            res = 'ic_pokemon_' + str(national_id)
            values = (name, national_id, hp, patk, pdef, spatk, spdef, spe, res)
            
            sprite_file = os.path.join('../../app/src/main/res/drawable/', res + '.png')
            if not os.path.isfile(sprite_file):
                print('WARNING: sprite does not exist for', name, national_id)

            cursor.execute('insert or ignore into pokemon('
                           'name, national_id, hp, atk, def, spatk, spdef, speed, image_resource_name)'
                           'values (?,?,?,?,?,?,?,?,?);', values)
            cursor.execute('select id from pokemon where name=?', (name,))
            pokemon_id = cursor.fetchone()[0]

            cursor.execute('select id from abilities where name=?', (ability,))
            ability_id = cursor.fetchone()[0]

            values = (pokemon_id, ability_id)
            cursor.execute('insert or ignore into pokemon_abilities('
                            'pokemon_id, ability_id)'
                            'values (?,?);', values)
            
            cursor.execute('select id from types where name=?', (type1,))
            type_id = cursor.fetchone()[0]

            values = (pokemon_id, type_id)
            cursor.execute('insert or ignore into pokemon_types('
                            'pokemon_id, type_id)'
                            'values (?,?);', values)

            if type2 is not "":
                cursor.execute('select id from types where name=?', (type2,))
                type_id = cursor.fetchone()[0]

                values = (pokemon_id, type_id)
                cursor.execute('insert or ignore into pokemon_types('
                                'pokemon_id, type_id)'
                                'values (?,?);', values)

    with open(pokemon_level_moves) as f:
        delim = '========'
        lines = f.readlines()
        i = 0
        
        #really fragile parsing
        while i < len(lines):
            line = lines[i].rstrip('\n')
       
            if line == delim:
                #if the current line is a delimiter, the next lines are:
                # 1. pokemon name
                # 2. delimiter
                # 3-N. list of moves
                # N+1. delimiter
                poke_name = lines[i+1].rstrip('\n').split(' - ')[0]
                cursor.execute('select id from pokemon where name=?', (poke_name,))
                poke_id = cursor.fetchone()[0]

                # Ignore 4 lines (delimiter, poke name, delimiter, count).
                j = i + 4

                while j < len(lines) and lines[j].rstrip('\n') != delim:
                    move_line = lines[j].rstrip('\n')
                    split = move_line.split(':')
                    level = split[0].split(' ')[1]
                    move_name = split[1].lstrip()
                    
                    cursor.execute('select id from moves where name=?', (move_name,))
                    move_id = cursor.fetchone()[0]

                    values = (poke_id, move_id, level)
                    cursor.execute('insert into pokemon_level_moves('
                                   'pokemon_id, move_id, level) '
                                   'values(?,?,?)', values)
                    j = j + 1
                i = j
            else:
                i = i + 1

    # Warn for missing movesets
    cursor.execute('select pokemon.name from pokemon where pokemon.id not in '
                   '(select pokemon_id from pokemon_level_moves);')
    missing = cursor.fetchall()
    if len(missing) > 0:
        print('WARNING: missing Level up sets:',cursor.fetchall())
    

        
os.remove(path)
conn = sqlite3.connect(path)
c = conn.cursor()
create_gens(c)
create_abilities(c)
create_types(c)
create_moves(c)
create_items(c)
create_pokemon(c)
conn.commit()

