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
    cursor.execute('insert or ignore into gens(id, name)'
              'values (NULL, "xy");')

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

            cursor.execute('select id from moves where name=?', (move,))
            move_id = cursor.fetchone()[0]

            cursor.execute('select id from gens where name=?', (gen,))
            gen_id = cursor.fetchone()[0]

            values = (name, move_id, location, gen_id)
            cursor.execute('insert or ignore into machines('
                           'name, move_id, location, gen_id)'
                           'values (?,?,?,?);', values)
    
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
        next(data) #skip header row

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
    
    cursor.execute('create table if not exists pokemon('
                   'id integer primary key, '
                   'name text unique not null, '
                   'hp integer not null, '
                   'atk integer not null, '
                   'def integer not null, '
                   'spatk integer not null, '
                   'spdef integer not null, '
                   'speed integer not null'
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

    with open(pokemon) as f:
        data = json.load(f)

        for p in data:
            name = p['name'].title()
            hp = p['hp']
            patk = p['attack']
            pdef = p['defense']
            spatk = p['sp_atk']
            spdef = p['sp_def']
            spe = p['speed']
            values = (name, hp, patk, pdef, spatk, spdef, spe)

            cursor.execute('insert or ignore into pokemon('
                           'name, hp, atk, def, spatk, spdef, speed)'
                           'values (?,?,?,?,?,?,?);', values)
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

