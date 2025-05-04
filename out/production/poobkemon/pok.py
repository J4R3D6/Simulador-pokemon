import pandas as pd

# Cargar los archivos CSV desde el repositorio
moves = pd.read_csv('https://raw.githubusercontent.com/xerxicodex/pokedex-data/master/csv/moves.csv')
move_names = pd.read_csv('https://raw.githubusercontent.com/xerxicodex/pokedex-data/master/csv/move_names.csv')
move_flavor_text = pd.read_csv('https://raw.githubusercontent.com/xerxicodex/pokedex-data/master/csv/move_flavor_text.csv')
types = pd.read_csv('https://raw.githubusercontent.com/xerxicodex/pokedex-data/master/csv/types.csv')
type_names = pd.read_csv('https://raw.githubusercontent.com/xerxicodex/pokedex-data/master/csv/type_names.csv')
move_damage_classes = pd.read_csv('https://raw.githubusercontent.com/xerxicodex/pokedex-data/master/csv/move_damage_classes.csv')
move_damage_class_prose = pd.read_csv('https://raw.githubusercontent.com/xerxicodex/pokedex-data/master/csv/move_damage_class_prose.csv')

# Filtrar movimientos de las generaciones 1 a 3
moves_filtered = moves[moves['generation_id'].isin([1, 2, 3])]

# Filtrar nombres de movimientos en español (language_id = 7)
move_names_es = move_names[move_names['local_language_id'] == 7].drop_duplicates(subset=['move_id'])

# Filtrar la descripción más reciente de cada movimiento (eliminar duplicados)
move_flavor_text_es = move_flavor_text[
    (move_flavor_text['language_id'] == 7) & 
    (move_flavor_text['version_group_id'] == 15)  # Usar la versión más reciente
].drop_duplicates(subset=['move_id'])

# Filtrar nombres de tipos en español (local_language_id = 7)
type_names_es = type_names[type_names['local_language_id'] == 7].drop_duplicates(subset=['type_id'])

# Filtrar descripciones de clases de daño en español (local_language_id = 7)
move_damage_class_prose_es = move_damage_class_prose[
    move_damage_class_prose['local_language_id'] == 7
].drop_duplicates(subset=['move_damage_class_id'])

# Unir datos para obtener nombres de movimientos
moves_merged = moves_filtered.merge(
    move_names_es[['move_id', 'name']], 
    left_on='id', 
    right_on='move_id', 
    how='left'
).drop(columns=['move_id'])

# Unir datos para obtener descripciones de movimientos
moves_merged = moves_merged.merge(
    move_flavor_text_es[['move_id', 'flavor_text']], 
    left_on='id', 
    right_on='move_id', 
    how='left'
).drop(columns=['move_id'])

# Unir datos para obtener nombres de tipos
types_merged = types.merge(
    type_names_es[['type_id', 'name']], 
    left_on='id', 
    right_on='type_id', 
    how='left'
).drop(columns=['type_id'])
types_merged = types_merged.rename(columns={'name': 'type_name'})
moves_merged = moves_merged.merge(
    types_merged[['id', 'type_name']], 
    left_on='type_id', 
    right_on='id', 
    how='left'
).drop(columns=['id_y'])

# Unir datos para obtener clases de daño
move_damage_classes_merged = move_damage_classes.merge(
    move_damage_class_prose_es[['move_damage_class_id', 'name']], 
    left_on='id', 
    right_on='move_damage_class_id', 
    how='left'
).drop(columns=['move_damage_class_id'])
move_damage_classes_merged = move_damage_classes_merged.rename(columns={'name': 'damage_class_name'})
moves_merged = moves_merged.merge(
    move_damage_classes_merged[['id', 'damage_class_name']], 
    left_on='damage_class_id', 
    right_on='id', 
    how='left'
).drop(columns=['id'])

# Limpiar y formatear los datos
moves_merged['flavor_text'] = moves_merged['flavor_text'].str.replace('\n', ' ')
moves_merged['damage_class_name'] = moves_merged['damage_class_name'].replace({
    'special': 'Especial',
    'physical': 'Físico',
    'status': 'Estado'
})

# Seleccionar y renombrar las columnas finales
final_df = moves_merged[[
    'id_x', 'name', 'flavor_text', 'type_name', 
    'damage_class_name', 'power', 'accuracy', 'pp'
]].rename(columns={
    'id_x': 'id',
    'name': 'nombre',
    'flavor_text': 'descripcion',
    'type_name': 'tipo',
    'damage_class_name': 'clase',
    'power': 'potencia',
    'accuracy': 'precision',
    'pp': 'pp'
})

# Eliminar filas duplicadas
final_df = final_df.drop_duplicates(subset=['id'])

# Guardar el archivo CSV con codificación UTF-8
final_df.to_csv('movimientos_pokemon_gen1-3.csv', index=False, encoding='utf-8-sig')

print("Archivo CSV generado correctamente con", len(final_df), "movimientos.")