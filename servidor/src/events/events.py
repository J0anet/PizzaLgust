from abc import ABC, abstractmethod

class Event:
    """
    Clase Evento: Representa un evento que puede ser observado por objetos EventListener.

    Métodos:
    - __init__(self): Constructor de la clase.
    - suscribe(self, observer): Método para suscribir un observador al evento.
    - unsuscribe(self, observer): Método para cancelar la suscripción de un observador al evento.
    - clear_observers(self): Método para eliminar todos los observadores suscritos al evento.
    - trigger(self, **kwargs): Método para desencadenar el evento, notificando a todos los observadores.
    """
    def __init__(self):
        self.observers = []

    def suscribe(self, observer):
        """
        Método para suscribir un observador al evento.

        Parámetros:
        - observer: El objeto observador a suscribir al evento.
        """
        if isinstance(observer, EventListener) \
                and observer not in self.observers:
            self.observers.append(observer)
    
    def unsuscribe(self, observer):
        """
        Método para cancelar la suscripción de un observador al evento.

        Parámetros:
        - observer: El objeto observador a cancelar su suscripción al evento.
        """
        if observer in self.observers:
            self.observers.remove(observer)
    
    def clear_observers(self):
        """
        Método para eliminar todos los observadores suscritos al evento.
        """ 
        self.observers.clear()

    def trigger(self, **kwargs):
        """
        Método para desencadenar el evento, notificando a todos los observadores.

        Parámetros:
        - **kwargs: Argumentos adicionales para pasar a los observadores al notificar el evento.
        """
        for observer in self.observers:
            observer.notify(**kwargs)

TOKEN_VERIFIED_EVENT = Event() 

class EventListener(ABC):
    """
    Clase EventListener: Clase base abstracta para objetos que escuchan eventos.

    Métodos:
    - __init__(self, event: Event): Constructor de la clase.
    - notify(self, **kwargs): Método abstracto para manejar la notificación de un evento.
    """
    def __init__(self, event: Event):
        """
        Constructor de la clase EventListener.

        Parámetros:
        - event: El evento al que se suscribe el observador.
        """
        event.suscribe(self)

    @abstractmethod
    def notify(self, **kwargs):
        """
        Método abstracto para manejar la notificación de un evento.

        Parámetros:
        - **kwargs: Argumentos adicionales pasados al notificar el evento.
        """
        pass

class TokenVerifiedEventListener(EventListener):
    """
    Clase TokenVerifiedEventListener: Observador específico para el evento de verificación de token.

    Atributos:
    - _user_id: Identificador de usuario verificado.
    - _token: Token verificado.

    Métodos:
    - __init__(self, *args, **kwargs): Constructor de la clase.
    - notify(self, user_id, token): Método para manejar la notificación del evento.
    """
    _user_id = ""
    _token = ""
    def __init__(self, *args, **kwargs):
        super().__init__(TOKEN_VERIFIED_EVENT)

    def notify(self, user_id, token):
        TokenVerifiedEventListener._user_id = user_id
        TokenVerifiedEventListener._token = token

