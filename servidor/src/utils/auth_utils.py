
import bcrypt
from time import time
from jwt import (decode, 
                 encode,
                 InvalidAlgorithmError, 
                 InvalidSignatureError,
                 ExpiredSignatureError, 
                 MissingRequiredClaimError)

from config import JWT_SECRET_KEY, N_REQ_CLAIMS
from events.events import TOKEN_VERIFIED_EVENT
from services.database import is_document_in_collection



def hash_password(password: str):
    """
    Función para generar el hash de una contraseña.

    Parámetros:
    - password (str): La contraseña a hashear.

    Retorna:
    - str: El hash de la contraseña.

    Esta función toma una contraseña como entrada, la hashea con una sal aleatoria y retorna el hash resultante.
    """
    bpassword = password.encode()
    salt = bcrypt.gensalt()
    return bcrypt.hashpw(bpassword, salt)

def check_password(password: str, hashed_pwd):
    """
    Función para verificar una contraseña con su hash.

    Parámetros:
    - password (str): La contraseña a verificar.
    - hashed_pwd: El hash de la contraseña almacenada.

    Retorna:
    - bool: True si la contraseña es válida, False si no lo es.

    Esta función verifica si una contraseña coincide con su hash almacenado.
    """
    bpassword = password.encode()
    if bcrypt.checkpw(bpassword, hashed_pwd):
        return True
    raise InvalidPasswordError()

def create_token(user_id, user_mail, ):
    """
    Función para crear un token JWT.

    Parámetros:
    - user_id: ID del usuario.
    - user_mail: Correo electrónico del usuario.

    Retorna:
    - str: El token JWT creado.

    Esta función crea un token JWT codificando el ID de usuario, el correo electrónico y el tiempo actual.
    """
    token = encode(
            payload={'id': user_id,
                    'email': user_mail,
                    'iat':int(time())},
            key= JWT_SECRET_KEY,
            algorithm='HS256')
    return token

def verify_token(token: str) -> str:
    """
    Obtener el ID de usuario a partir de un token proporcionado.

    Parámetros:
    - token (str): El token de usuario.

    Esta función decodifica el token de usuario proporcionado utilizando una clave secreta, verifica las reclamaciones requeridas y verifica la existencia del usuario en la base de datos. Si el token es inválido o falta reclamaciones requeridas, se genera un error InvalidTokenError.
    """
    try:
        decoded_token = decode(token, 
                            JWT_SECRET_KEY, 
                            algorithms='HS256', 
                            options={'require': ['id', 'email', 'iat']})
        if len(decoded_token.keys()) > int(N_REQ_CLAIMS):
            raise InvalidTokenError("Too many claims in payload")
        if not is_document_in_collection('user', 'users', _id=decoded_token['id']):
            raise InvalidTokenError("User not found in database")
    except (InvalidAlgorithmError, 
            InvalidSignatureError,
            ExpiredSignatureError, 
            MissingRequiredClaimError) as e:
        raise InvalidTokenError(e)
    
    TOKEN_VERIFIED_EVENT.trigger(user_id=decoded_token['id'], token=token)

class InvalidTokenError(Exception):
    def __init__(self, message="Token is invalid"):
        self.message = message
        super().__init__(self.message)

class InvalidPasswordError(Exception):
    def __init__(self, message="Password is invalid"):
        self.message = message
        super().__init__(self.message)