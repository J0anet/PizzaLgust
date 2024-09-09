"""Descripción: Este módulo define blueprints de Flask para organizar rutas y vistas en tu aplicación. Los blueprints se utilizan para agrupar rutas y vistas relacionadas, facilitando la gestión y organización de los puntos finales de tu aplicación.
Propósito: Definir y registrar blueprints que manejen diferentes partes de la funcionalidad de tu aplicación."""

from flask import Blueprint
from controllers import main_controller as controller

blueprint = Blueprint('blueprint', __name__, url_prefix='/pizzalgust')


blueprint.route('/test', methods=['POST'])(controller.test)

blueprint.route('/login', methods=['POST'])(controller.login)
blueprint.route('/logout', methods=['POST'])(controller.logout)

blueprint.route('/create-user', methods=['POST'])(controller.create_user)
blueprint.route('/get-all-users', methods=['POST'])(controller.get_all_users)
blueprint.route('/delete-user', methods=['DELETE'])(controller.delete_user)
blueprint.route('/update-user', methods=['PUT'])(controller.update_user)

blueprint.route('/create-pizza', methods=['POST'])(controller.create_pizza)
blueprint.route('/get-all-pizzas', methods=['POST'])(controller.get_all_pizzas)
blueprint.route('/delete-pizza', methods=['DELETE'])(controller.delete_pizza)
blueprint.route('/update-pizza', methods=['PUT'])(controller.update_pizza)

blueprint.route('/create-masa', methods=['POST'])(controller.create_masa)
blueprint.route('/get-all-masas', methods=['POST'])(controller.get_all_masas)

blueprint.route('/create-ingredient', methods=['POST'])(controller.create_ingredient)
blueprint.route('/get-all-ingredients', methods=['POST'])(controller.get_all_ingredients)