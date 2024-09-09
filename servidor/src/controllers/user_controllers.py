"""
Documentación del Módulo: controllers

Este módulo proporciona controladores para manejar la autenticación de usuarios y la gestión de sesiones en una aplicación Flask.

Clases:
- BaseController: Clase base abstracta para definir la funcionalidad básica del controlador.
- LoginController: Controlador para manejar la funcionalidad de inicio de sesión de usuario.
- LogoutController: Controlador para manejar la funcionalidad de cierre de sesión de usuario.
- GetAllUsersController: Controlador para manejar la funcionalidad de cierre de sesión de usuario.
- CreateUserController: Controlador para manejar la funcionalidad de creación de usuario.
- DeleteUserController: Controlador para manejar la funcionalidad de eliminación de usuario.
- UpdateUserController: Controlador para manejar la funcionalidad de  de actualización usuario.
- UserNotLoggedInError: Excepción personalizada generada cuando un usuario intenta realizar acciones sin haber iniciado sesión.
- UserNotAdminInError: Excepción personalizada generada cuando un usuario no admin intenta realizar acciones que necesitan acceso de administrador.
"""
from abc import ABC, abstractmethod, abstractclassmethod

from utils.async_utils import run_task_in_background
from utils.auth_utils import create_token
from events.events import TokenVerifiedEventListener
from services import database as db
from models.models import DBModel, User


class BaseController(ABC):
    """BaseController: Clase base abstracta para definir la funcionalidad básica del controlador.

    Métodos:
    - __init__(self, body, config=None): Método constructor.
    - run(self) -> tuple: Método abstracto que debe ser implementado por subclases para ejecutar la lógica del controlador.
    """
    def __init__(self, config=None):
        pass


    def run(self) -> tuple:
        """
        Método para ejecutar la lógica del controlador.

        Retorna:
        - tuple: Una tupla que contiene los datos de respuesta del controlador y el código de estado HTTP.
        """
        message = DBModel # Some kind of DBModel
        run_task_in_background(self.__persist, 
                               message=message)

    def __example(self):
        pass        
    
    def _persist(self, model: DBModel):
        message_id = model.store()
        
class LoginController(BaseController):
    logged_in_user_tokens = []
    def __init__(self, body, config=None):
        self.user = User.read_from_email(email=body['email'],
                                         password=body['password'])


    def run(self) -> tuple:
        token = create_token(self.user._id, 
                             self.user.email)
        LoginController.logged_in_user_tokens.append(token)
        data = {'msg': 'Login succesful', 
                'user_name': self.user.user_name,
                'first_name': self.user.first_name,
                'last_name': self.user.last_name,
                'is_admin': self.user.is_admin,
                'user_type': self.user.user_type,
                'token': token}
        return data, 200
class LogoutController(BaseController):
    def __init__(self, body, config=None):
        self.token = body['token']


    def run(self) -> tuple:
        if not self.token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        
        data = {'msg': 'Logout succesful',}
        LoginController.logged_in_user_tokens.remove(self.token)
        return data, 200

class GetAllUsersController(TokenVerifiedEventListener, BaseController):
    def __init__(self, body, config=None):
        self.token = body['token']
        super(GetAllUsersController, self).__init__()

    def run(self) -> tuple:
        if not self.token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        self.user = User.read(GetAllUsersController._user_id)
        if not self.user.is_admin:
            raise UserNotAdminError()
        results =  db.get_all_documents_from_database('user','users')
        data = {'users': []}
        for result in results:
            result.pop('password')
            data['users'].append(result)
        return data, 200

class CreateUserController(BaseController):
    def __init__(self, body, config=None):
        self.user = body

    def run(self) -> tuple:
        user = User.new_user(**self.user)
        data = {'msg': 'Usuario creado con éxito',
                'user_id': user._id}
        return data, 200

class DeleteUserController(TokenVerifiedEventListener, BaseController):
    def __init__(self, body, config=None):
        self.body = body
        super(DeleteUserController, self).__init__()


    def run(self) -> tuple:
        if not DeleteUserController._token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        self.user = User.read(DeleteUserController._user_id)
        if not self.user.is_admin:
            raise UserNotAdminError()
        user = User.read(self.body['user_id'])
        if user.delete():
            data = {'msg': 'Usuario borrado con éxito'}
            code = 200
        else:
            data = {'msg': 'El usuario no se ha podido borrar'}
            code = 500
        return data, code
    
class UpdateUserController(TokenVerifiedEventListener, BaseController):
    def __init__(self, body, config=None):
        self.user_info = body.copy()
        self.user_info.pop('token')
        super(UpdateUserController, self).__init__()

    def run(self) -> tuple:
        if not DeleteUserController._token in LoginController.logged_in_user_tokens:
            raise UserNotLoggedInError()
        user = User.read(DeleteUserController._user_id)
        if user.is_admin:
            user = User.read(self.user_info['user_id'])
        if 'user_id' in self.user_info.keys():
            self.user_info.pop('user_id')
        if 'password' in self.user_info.keys():
            self.user_info.pop('password')
        user.update(**self.user_info)
        data = {'msg': 'Usuario actualizado con éxito'}
        return data, 200

class UserNotLoggedInError(Exception):
    def __init__(self, message="The user is not logged in"):
        self.message = message
        super().__init__(self.message)

class UserNotAdminError(Exception):
    def __init__(self, message="The user is not admin"):
        self.message = message
        super().__init__(self.message)
