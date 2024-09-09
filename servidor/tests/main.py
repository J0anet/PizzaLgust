import unittest
import requests
from unittest import TestCase
from tests_encrypt_utils import encrypt_body, decrypt_body

URL = 'http://localhost:5002/pizzalgust/'
HEADERS = {'Content-Type': 'application/json'}

class PizzalgustTests(TestCase):
    """
    PizzalgustTests: Clase para realizar pruebas de unidad.
    """
    def test_login(self):
        """
        Método para probar la funcionalidad de inicio de sesión.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert 'msg' in response_body.keys()
        assert 'user_name' in response_body.keys()
        assert 'first_name' in response_body.keys()
        assert 'last_name' in response_body.keys()
        assert 'is_admin' in response_body.keys()
        assert 'token' in response_body.keys()
        assert 'user_type' in response_body.keys()

    def test_bad_password(self):
        """
        Método para probar el manejo de una contraseña incorrecta.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'fake_password'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        assert response.status_code == 401
    
    def test_logout(self):
        """
        Método para probar la funcionalidad de cierre de sesión.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']

        body = {'token': token}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/logout',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200

    def test_bad_token(self):
        """
        Método para probar el manejo de un token incorrecto.
        """
        token = 'Not a token'
        body = {'token': token}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/logout',
                                 json=body,
                                 headers=HEADERS)
        assert response.status_code == 500

    def test_get_all_users(self):  
        """
        Método para probar la solicitud de obtener todos los usuarios.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']

        body = {'token': token}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/get-all-users',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert 'users' in response_body.keys()
        assert isinstance(response_body['users'], list)
    
    def test_get_all_users_no_admin(self):  
        """
        Método para probar el endpoint `get-all-users` sin ser admin.
        """
        body = {"email": "pparker@newyork.com",
                "password": "spiderman",}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']

        body = {'token': token}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/get-all-users',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 401
    
    def test_create_user(self):
        """
        Método para probar la creación de usuario.
        """
        body = dict(user_name="homer",
            email="hsimpson@springfield.com",
            first_name="Homer",
            last_name="Simpson",
            password="stupidflanders",)
        body = encrypt_body(body)
        response = requests.post(url=URL + '/create-user',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert response_body['msg'] == 'Usuario creado con éxito'

    def test_delete_user(self):
        """
        Método para probar la eliminación de usuario.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']
        body = dict(user_name="homer",
            email="hsimpson@springfield.com",
            first_name="Homer",
            last_name="Simpson",
            password="stupidflanders",)
        body = encrypt_body(body)
        response = requests.post(url=URL + '/create-user',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        body = {'token': token,
                'user_id': response_body['user_id']}
        body = encrypt_body(body)
        response = requests.delete(url=URL + '/delete-user',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert response_body['msg'] == 'Usuario borrado con éxito'
    
    def test_update_user(self):
        """
        Método para probar la actualización de usuario.
        """
        body = dict(user_name="homer",
            email="hsimpson@springfield.com",
            first_name="Homer",
            last_name="Simpson",
            password="stupidflanders",)
        body = encrypt_body(body)
        response = requests.post(url=URL + '/create-user',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        
        body = {'email': 'hsimpson@springfield.com',
                'password': 'stupidflanders'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']

        body = {'token': token,
                'user_name': "max_power",
                'first_name': "Max",
                'last_name': "Power"}
        
        body = encrypt_body(body)
        response = requests.put(url=URL + '/update-user',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert response_body['msg'] == 'Usuario actualizado con éxito'

    def test_create_pizza(self):
        """
        Método para probar la creación de pizzas.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']
        body = dict(
            token=token,
            name="Margarita",
            price=10.5,
            masa_id="6638bb6e778b2abe345c88e5",
            ingredient_ids=["663a721258b1ab5149370490", "6638bef81a1e99075284912d", "6638bf791a1e990752849138"],
            description="Pizza con tomate, queso mozzarela y albahaca"
            )
        body = encrypt_body(body)
        response = requests.post(url=URL + '/create-pizza',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert response_body['msg'] == 'Pizza creada con éxito'

    def test_get_all_pizzas(self):  
        """
        Método para probar la solicitud de obtener todos los pizzas.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']

        body = {'token': token}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/get-all-pizzas',
                                 json=body,
                                 headers=HEADERS)
        assert response.status_code == 200
        assert 'pizzas' in response.json().keys()
        assert isinstance(response.json()['pizzas'], list)

    def test_delete_pizza(self):
        """
        Método para probar la eliminación de pizzas.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']
        body = dict(
            token=token,
            name="Margarita",
            price=10.5,
            masa_id="6638bb6e778b2abe345c88e5",
            ingredient_ids=["663a721258b1ab5149370490", "6638bef81a1e99075284912d", "6638bf791a1e990752849138"],
            description="Pizza con tomate, queso mozzarela y albahaca"
            )
        body = encrypt_body(body)
        response = requests.post(url=URL + '/create-pizza',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        body = {'token': token,
                'pizza_id': response_body['pizza_id']}
        body = encrypt_body(body)
        response = requests.delete(url=URL + '/delete-pizza',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert response_body['msg'] == 'Pizza borrada con éxito'
    
    def test_update_pizza(self):
        """
        Método para probar la actualización de Pizza.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']
        body = dict(
            token=token,
            name="Margarita",
            price=10.5,
            masa_id="6638bb6e778b2abe345c88e5",
            ingredient_ids=["663a721258b1ab5149370490", "6638bef81a1e99075284912d", "6638bf791a1e990752849138"],
            description="Pizza con tomate, queso mozzarela y albahaca"
            )
        body = encrypt_body(body)
        response = requests.post(url=URL + '/create-pizza',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        body = {'token': token,
                'name': 'Hawai',
                'pizza_id': response_body['pizza_id'],
                'description': "Pizza con piña, pollo y queso"}
        
        body = encrypt_body(body)
        response = requests.put(url=URL + '/update-pizza',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert response_body['msg'] == 'Pizza actualizada con éxito'

    def test_create_masa(self):
        """
        Método para probar la creación de masas.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']
        body = dict(
            token=token,
            name="Con queso",
            description="Masa deliciosa rellena de queso bien rico"
            )
        body = encrypt_body(body)
        response = requests.post(url=URL + '/create-masa',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert response_body['msg'] == 'Masa creada con éxito'

    def test_get_all_masas(self):  
        """
        Método para probar la solicitud de obtener todos los masas.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']

        body = {'token': token}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/get-all-masas',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert 'masas' in response_body.keys()
        assert isinstance(response_body['masas'], list)

    def test_create_ingredient(self):
        """
        Método para probar la creación de ingredients.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']
        body = dict(
            token=token,
            name="Anchoas",
            description="Anchoas de los mares del Norte"
            )
        body = encrypt_body(body)
        response = requests.post(url=URL + '/create-ingredient',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert response_body['msg'] == 'Ingrediente creado con éxito'

    def test_get_all_ingredients(self):  
        """
        Método para probar la solicitud de obtener todos los ingredients.
        """
        body = {'email': 'bwayne@gotham.com',
                'password': 'batman'}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/login',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        token = response_body['token']

        body = {'token': token}
        body = encrypt_body(body)
        response = requests.post(url=URL + '/get-all-ingredients',
                                 json=body,
                                 headers=HEADERS)
        response_body = decrypt_body(response.json())
        assert response.status_code == 200
        assert 'ingredients' in response_body.keys()
        assert isinstance(response_body['ingredients'], list)

if __name__ == '__main__':
    unittest.main()