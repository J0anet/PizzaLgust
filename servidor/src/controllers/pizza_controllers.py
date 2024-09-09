"""
Documentación del Módulo: pizza_controllers

Este módulo proporciona controladores para manejar la gestión de pizzas en una aplicación Flask.

Clases:
- GetAllpizzasController: Controlador para obtener un listado de todas las pizzas.
- CreatepizzaController: Controlador para manejar la funcionalidad de creación de usuario.
- DeletepizzaController: Controlador para manejar la funcionalidad de eliminación de usuario.
- UpdatepizzaController: Controlador para manejar la funcionalidad de  de actualización usuario.
"""
from abc import ABC, abstractmethod, abstractclassmethod

from utils.async_utils import run_task_in_background
from events.events import TokenVerifiedEventListener
from services import database as db
from models.models import User, DBModel, Pizza, Masa, Ingredient
from .user_controllers import (BaseController,
                              LoginController,
                              UserNotLoggedInError,
                              UserNotAdminError)

def class_to_dict(clase):
    new_dict = {}
    for key, value in clase.__dict__.items():
        if not key.startswith('__') and not callable(key):
            if key == 'ingredients':
                value = [class_to_dict(val) for val in value]
            if isinstance(value, Masa) or isinstance(value, Ingredient):
                value = class_to_dict(value)
            new_dict[key] = value
    return new_dict
        
def is_ingredient_list(possible_list):
    return isinstance(possible_list, list) and \
           len(possible_list) > 0 and \
           isinstance(possible_list[0], list) 
          
class GetAllPizzasController(TokenVerifiedEventListener, BaseController):
    def __init__(self, body, config=None):
        self.token = body['token']
        super(GetAllPizzasController, self).__init__()

    def run(self) -> tuple:
        if not self.token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        results =  db.get_all_documents_from_database('pizza','pizzas')
        pizzas = [class_to_dict(Pizza(**result)) for result in results]
        # pizzas = [class_to_dict(Pizza(**result)) for result in results[-2:]]
        data = {'pizzas': pizzas}
        return data, 200

class CreatePizzaController(TokenVerifiedEventListener, BaseController):
    def __init__(self, body, config=None):
        self.token = body['token']
        self.pizza = body
        super(CreatePizzaController, self).__init__()


    def run(self) -> tuple:
        if not self.token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        self.user = User.read(CreatePizzaController._user_id)
        if not self.user.is_admin:
            raise UserNotAdminError()
        self.pizza.pop('token')
        pizza = Pizza.new_pizza(**self.pizza)
        data = {'msg': 'Pizza creada con éxito',
                'pizza_id': pizza._id}
        return data, 200

class DeletePizzaController(TokenVerifiedEventListener, BaseController):
    def __init__(self, body, config=None):
        self.body = body
        super(DeletePizzaController, self).__init__()


    def run(self) -> tuple:
        if not DeletePizzaController._token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        self.user = User.read(DeletePizzaController._user_id)
        if not self.user.is_admin:
            raise UserNotAdminError()
        pizza = Pizza.read(self.body['pizza_id'])
        if pizza.delete():
            data = {'msg': 'Pizza borrada con éxito'}
            code = 200
        else:
            data = {'msg': 'La pizza no se ha podido borrar'}
            code = 500
        return data, code
    
class UpdatePizzaController(TokenVerifiedEventListener, BaseController):
    def __init__(self, body, config=None):
        self.pizza_info = body.copy()
        super(UpdatePizzaController, self).__init__()

    def run(self) -> tuple:
        if not UpdatePizzaController._token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        user = User.read(UpdatePizzaController._user_id)
        if user.is_admin:
            pizza = Pizza.read(self.pizza_info['pizza_id'])
        if 'pizza_id' in self.pizza_info.keys():
            self.pizza_info.pop('pizza_id')
            self.pizza_info.pop('token')
        pizza.update(**self.pizza_info)
        data = {'msg': 'Pizza actualizada con éxito'}
        return data, 200
    
class GetAllMasasController(TokenVerifiedEventListener, BaseController):
    def __init__(self, body, config=None):
        self.token = body['token']
        super(GetAllMasasController, self).__init__()

    def run(self) -> tuple:
        if not self.token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        results =  db.get_all_documents_from_database(Masa._database, Masa._collection)
        data = {'masas': results}
        return data, 200

class CreateMasaController(TokenVerifiedEventListener, BaseController):
    def __init__(self, body, config=None):
        self.token = body['token']
        self.masa = body
        super(CreateMasaController, self).__init__()


    def run(self) -> tuple:
        if not self.token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        self.user = User.read(CreateMasaController._user_id)
        if not self.user.is_admin:
            raise UserNotAdminError()
        self.masa.pop('token')
        masa = Masa.new_masa(**self.masa)
        data = {'msg': 'Masa creada con éxito',
                'masa_id': masa._id}
        return data, 200
    
class GetAllIngredientsController(TokenVerifiedEventListener, BaseController):
    def __init__(self, body, config=None):
        self.token = body['token']
        super(GetAllIngredientsController, self).__init__()

    def run(self) -> tuple:
        if not self.token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        results =  db.get_all_documents_from_database(Ingredient._database, Ingredient._collection)
        data = {'ingredients': results}
        return data, 200

class CreateIngredientController(TokenVerifiedEventListener, BaseController):
    def __init__(self, body, config=None):
        self.token = body['token']
        self.ingredient = body
        super(CreateIngredientController, self).__init__()


    def run(self) -> tuple:
        if not self.token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        self.user = User.read(CreateIngredientController._user_id)
        if not self.user.is_admin:
            raise UserNotAdminError()
        self.ingredient.pop('token')
        ingredient = Ingredient.new_ingredient(**self.ingredient)
        data = {'msg': 'Ingrediente creado con éxito',
                'ingredient_id': ingredient._id}
        return data, 200