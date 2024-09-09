from flask import request, jsonify

from config import (UNAUTHORIZED_CODE, 
                    DOCUMENT_NOT_FOUND_CODE, 
                    INVALID_TOKEN_CODE)
from events.events import TOKEN_VERIFIED_EVENT
from utils.async_utils import run_task_in_background
from utils.encrypt_utils import encrypt_body, decrypt_body
from utils.auth_utils import (verify_token, 
                              InvalidTokenError,
                              InvalidPasswordError)
from services.database import DocumentNotFoundError
from .user_controllers import *
from .pizza_controllers import *


crypto = False
print(f"IN MAIN CONTROLLER: {crypto}")
def test():
    nombre = request.json['nombre']
    return jsonify({'msg': f'Hola {nombre}, todo ok!'}), 200

def __make_response(controller, verify=True):
    try:
        if crypto:
            body = decrypt_body(request.json)
        else:
            body = request.json
        controller = controller(body)
        if verify:
            verify_token(request.json['token'])  
        data, code = controller.run()      
        
    except InvalidTokenError as e:
        data = {'msg': str(e.message)}
        code = INVALID_TOKEN_CODE
    except UserNotLoggedInError as e:
        data = {'msg': str(e.message)}
        code = UNAUTHORIZED_CODE
    except DocumentNotFoundError as e:
        data = {'msg': str(e.message)}
        code = DOCUMENT_NOT_FOUND_CODE
    except UserNotAdminError as e:
        data = {'msg': str(e.message)}
        code = UNAUTHORIZED_CODE
    except Exception as e:
        data = {'msg': f"{e.__class__.__name__}: {str(e)}"}
        code =  500
    finally:
        TOKEN_VERIFIED_EVENT.clear_observers()
        if crypto:
            data = encrypt_body(data)
        response =  jsonify(data), code
        return response

# ##############################################################
# #                                                            #
# #                 LOGIN ENDPOINTS                            #
# #                 ``````````````````````                     #
# ##############################################################
def login():
    try:
        body = request.json
        if crypto:
            body = decrypt_body(body)
        controller = LoginController(body)
        data, code = controller.run()      
    except InvalidPasswordError as e:
        data = {'msg': str(e.message)}
        code = UNAUTHORIZED_CODE
    except DocumentNotFoundError as e:
        data = {'msg': str(e.message)}
        code = DOCUMENT_NOT_FOUND_CODE
    except Exception as e:
        data = {'msg': f"{e.__class__.__name__}: {str(e)}"}
        code = 500
    finally:
        TOKEN_VERIFIED_EVENT.clear_observers()
        if crypto:
            data = encrypt_body(data)
        response =  jsonify(data), code
        return response

def logout():
    return __make_response(LogoutController)

# ##############################################################
# #                                                            #
# #                 USER ENDPOINTS                             #
# #                 ``````````````                             #
# ##############################################################
def get_all_users():
    return __make_response(GetAllUsersController)

def create_user():
    return __make_response(CreateUserController, verify=False)

def delete_user():
    return __make_response(DeleteUserController)

def update_user():
    return __make_response(UpdateUserController)


# ##############################################################
# #                                                            #
# #                 PIZZA ENDPOINTS                            #
# #                 ``````````````                             #
# ##############################################################
def get_all_pizzas():
    return __make_response(GetAllPizzasController)

def create_pizza():
    return __make_response(CreatePizzaController)

def delete_pizza():
    return __make_response(DeletePizzaController)

def update_pizza():
    return __make_response(UpdatePizzaController)

# ##############################################################
# #                                                            #
# #                 MASA ENDPOINTS                             #
# #                 ``````````````                             #
# ##############################################################
def get_all_masas():
    return __make_response(GetAllMasasController)

def create_masa():
    return __make_response(CreateMasaController)

# ##############################################################
# #                                                            #
# #              INGREDIENT ENDPOINTS                          #
# #                 ``````````````                             #
# ##############################################################
def get_all_ingredients():
    return __make_response(GetAllIngredientsController)

def create_ingredient():
    return __make_response(CreateIngredientController)