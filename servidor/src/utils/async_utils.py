"""
Descripción: Este módulo contiene funciones y herramientas de utilidad para trabajar con operaciones asíncronas, lo cual puede ser útil para gestionar tareas asíncronas en tu aplicación Flask.
Propósito: Proporcionar utilidades para gestionar operaciones asíncronas.
"""
from threading import Thread

def run_task_in_background(task, **kwargs):
    """
    Función para ejecutar una tarea en segundo plano.

    Parámetros:
    - task: La tarea a ejecutar.
    - **kwargs: Argumentos de palabras clave para la tarea.

    Esta función crea un hilo para ejecutar la tarea en segundo plano.
    """
    thread = Thread(target=task, 
                    kwargs=dict(**kwargs))
    thread.start()