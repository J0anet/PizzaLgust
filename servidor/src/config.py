

import os
from dotenv import load_dotenv

load_dotenv()

# DATABASE
DB_URI = os.getenv('db_uri')

DOCUMENT_NOT_FOUND_CODE = 410
UNAUTHORIZED_CODE = 401

# JWT AUTHORIZATION
JWT_SECRET_KEY = os.getenv('JWT_SECRET_KEY')
INVALID_TOKEN_CODE = 403
N_REQ_CLAIMS = 3

ENCRYPT_KEY=os.getenv('ENCRYPT_KEY')

KEY_TYPES = {
    "email": str,
    "password": str,
    "msg": str,
    "user_name": str,
    "first_name": str,
    "last_name": str,
    "is_admin": bool,
    "user_type": str,
    "token": str,
    "_id": str,
    "user_id": str,
    "description": str,
    "name": str,
    "price": float,
    "pizza_id": str,
}

LIST_KEYS = ["users", "pizzas", "masas", "ingredient_ids", "ingredients"]

