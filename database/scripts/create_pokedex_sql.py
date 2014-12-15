import json
import csv
import sqlite3

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

conn = sqlite3.connect(path)
c = conn.cursor()
create_gens(c)
create_abilities(c)
create_types(c)
create_pokemon(c)
conn.commit()

