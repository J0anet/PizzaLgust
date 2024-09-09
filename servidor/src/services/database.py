"""
Descripción: Este módulo es responsable de gestionar las conexiones a la base de datos 
y los servicios relacionados con la base de datos, incluido el almacenamiento, la recuperación y la manipulación de datos.
Propósito: Proporcionar servicios relacionados con la base de datos para tu aplicación."""

from contextlib import contextmanager   
from pymongo.server_api import ServerApi
from pymongo.mongo_client import MongoClient
from bson import ObjectId
from config import DB_URI

def insert_one(database: str, collection: str, **fields) -> str:
    """
    Inserta un documento en una colección de una base de datos.

    Parámetros:
    - database (str): El nombre de la base de datos.
    - collection (str): El nombre de la colección.
    - **fields: Argumentos clave-valor representando los campos del documento a insertar.

    Retorna:
    - str: El ID del documento insertado.

    Esta función inserta un documento en la colección especificada de la base de datos dada. Asegura que el ID del documento sea un ObjectId válido.
    """
    fields = __ensure_object_id_in_fields(**fields) 
    with __get_db(database) as db:
        result = db[collection].insert_one(fields)
    return str(result.inserted_id)

def update_one(database:str, collection:str, _id:str, **updated_fields):
    """
    Actualiza un documento en una colección de una base de datos.

    Parámetros:
    - database (str): El nombre de la base de datos.
    - collection (str): El nombre de la colección.
    - _id (str): El ID del documento a actualizar.
    - **updated_fields: Argumentos clave-valor representando los campos actualizados del documento.

    Esta función actualiza un documento en la colección especificada de la base de datos dada. Asegura que el ID del documento sea un ObjectId válido.
    """
    _id = __ensure_object_id(_id)
    fields = __ensure_object_id_in_fields(**updated_fields)
    with __get_db(database) as db:
        db[collection].update_one({'_id': _id}, update={'$set':dict(**fields)})

def is_document_in_collection(db: str, 
                              collection: str,  
                              **fields) -> bool:
    """
    Comprueba si un documento existe en una colección de base de datos según los filtros proporcionados.

    Parámetros:
    - db (str): El nombre de la base de datos.
    - collection (str): El nombre de la colección.
    - **fields: Argumentos de palabras clave que representan filtros adicionales.

    Retorna:
    - bool: True si se encuentra el documento en la colección, False en caso contrario.

    Esta función comprueba si un documento existe en la colección de base de datos especificada según los filtros proporcionados. Retorna True si se encuentra el documento, de lo contrario, retorna False.
    """
    fields = {k: ObjectId(v) 
              if k.endswith('_id') else v 
              for k,v in dict(**fields).items()}
    with __get_db(db) as db:
        result = db[collection].find_one(dict(**fields))
    return result != None

def get_document_from_database(database:str, collection:str, **query) -> dict:
    """
    Recupera un documento de una colección de base de datos según los parámetros de consulta especificados.

    Parámetros:
    - database (str): El nombre de la base de datos.
    - collection (str): El nombre de la colección.
    - **query: Argumentos de palabras clave que representan parámetros de consulta.

    Retorna:
    - dict: Un diccionario que contiene el documento recuperado.

    Esta función recupera un documento de la colección de base de datos especificada según los parámetros de consulta proporcionados. Los parámetros de consulta se utilizan para coincidir con el documento, y el documento recuperado se devuelve como un diccionario.
    """

    query = {k: ObjectId(v) if k.endswith('_id') else v for k,v in dict(**query).items()} 
    with __get_db(database) as db:
        result = db[collection].find_one(query)
    if result == None:
        raise DocumentNotFoundError()
    result = {k: str(v) if isinstance(v, ObjectId) else v for k,v in result.items()}
    return result
    
def delete_document_from_db(db_name: str, collection: str, **filters) -> bool:
    """
    Elimina documentos de una colección de base de datos según los filtros especificados.

    Parámetros:
    - db_name (str): El nombre de la base de datos en la que operar.
    - collection (str): El nombre de la colección de la que se eliminarán los documentos.
    - **filters: Argumentos de palabras clave que representan filtros para la eliminación de documentos.

    Retorna:
    - bool: True si se eliminó uno o más documentos, False si no se eliminó ningún documento.

    Esta función elimina documentos de la colección de base de datos especificada según los filtros proporcionados.
    Los filtros se aplican para coincidir con los documentos para la eliminación, y retorna True si se eliminó uno o más documentos, de lo contrario, retorna False.
    """
    filters = {k: __ensure_object_id(v) if k.endswith('_id') else v for k,v in filters.items()}
    with __get_db(db_name) as db:
        result = db[collection].delete_many(dict(**filters))
    return result.deleted_count > 0


def get_all_documents_from_database(database:str, collection: str, projection=None, **query) -> list:
    """
    Obtiene todos los documentos de una colección de una base de datos.

    Parámetros:
    - database (str): El nombre de la base de datos.
    - collection (str): El nombre de la colección.
    - projection: La proyección de los campos a incluir en los documentos recuperados.
    - **query: Argumentos clave-valor representando los filtros de consulta.

    Retorna:
    - list: Una lista de diccionarios que representan los documentos recuperados.

    Esta función obtiene todos los documentos que coinciden con los filtros de consulta especificados de la colección dada en la base de datos dada.
    """
    query = {k: ObjectId(v) if k.endswith('_id') else v for k,v in dict(**query).items()} 
    with __get_db(database) as db:
        results = db[collection].find(query, projection=projection)
        results = [{k: str(v) 
                if isinstance(v, ObjectId) else v 
                for k,v in result.items()} 
                for result in results]
    return results

def get_many_documents(database: str, collection: str, ids_array:list):
    ids = [ObjectId(id) for id in ids_array]
    with __get_db(database) as db:
        messages = db.messages.find({'_id': {'$in': ids}})
        messages = list(messages)
    messages =[{k:str(v) if isinstance(v, ObjectId) else v for k,v in message.items()} for message in messages]
    return messages


# –––––––––––––– PRIVATE METHODS ––––––––––––––––––––––    
def __ensure_object_id(id:str) -> ObjectId:
    if not isinstance(id, ObjectId):
        return ObjectId(id)
    return id

def __ensure_object_id_in_fields(**fields) -> dict:
    return {k: ObjectId(v) 
              if k.endswith('_id') else v 
              for k,v in dict(**fields).items()} 

@contextmanager
def __get_db(db_name: str):
    """
    Gestor de contexto para conectarse a la base de datos de conversaciones MongoDB.

    Args:
        db_name: El nombre de la base de datos deseada.

    Yields:
        La base de datos.

    Nota:
        La conexión se cierra automáticamente al salir del contexto.
    """
    client = MongoClient(DB_URI, server_api=ServerApi('1'))
    try:
        yield client[db_name]
    finally:
        client.close()

class DocumentNotFoundError(Exception):
    def __init__(self, message="Document not found."):
        self.message = message
        super().__init__(self.message)



