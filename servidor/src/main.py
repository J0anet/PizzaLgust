"""
Punto de entrada de la aplicación Flask. Es donde se crea la instancia de la aplicación Flask, se configura
y ejecuta la aplicación.
Propósito: Inicializar y ejecutar la aplicación Flask.
"""

from flask import Flask
from routes.blueprints import blueprint
from flask_cors import CORS
import sys

import controllers.main_controller as c

app = Flask(__name__) 
CORS(app)

app.register_blueprint(blueprint)

if __name__ == '__main__': 
    c.crypto = 'no-crypto' not in sys.argv
    print(f"CRYPTO = {c.crypto}")
    app.run(host='localhost', port=5002)