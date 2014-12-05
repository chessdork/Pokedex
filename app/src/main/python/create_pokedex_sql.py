import json
import sqlite3

# output database
path = '../assets/databases/pokedex.db'

def create_gens(db):
    # create generations table  
    db.execute('create table if not exists gens('
              'id integer primary key, '
              'name text not null unique '
              ');')

    # add a few entries
    db.execute('insert or ignore into gens(id, name)'
              'values (NULL, "xy");')

    db.commit()

def create_abilities(db):
    abilities = '../res/raw/abilities.json'
    
    # create abilities table
    db.execute('create table if not exists abilities('
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

            c = db.execute('select id from gens where name=?', gen)
            gen_id = c.fetchone()[0]

            values = (name, description, gen_id)
    
            db.execute('insert or ignore into abilities(name, description, gen_id)'
                      'values (?,?,?);', values)
            
    db.commit();

db = sqlite3.connect(path)
create_gens(db)
create_abilities(db)

