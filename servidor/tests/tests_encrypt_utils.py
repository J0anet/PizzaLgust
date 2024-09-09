from Crypto.Cipher import AES
import base64
import os
from config import ENCRYPT_KEY, KEY_TYPES, LIST_KEYS

def __pad(byte_array):
    BLOCK_SIZE = 16
    pad_len = BLOCK_SIZE - len(byte_array) % BLOCK_SIZE
    return byte_array + (bytes([pad_len]) * pad_len)

# Remove padding at end of byte array
def __unpad(byte_array):
    last_byte = byte_array[-1]
    return byte_array[0:-last_byte]

def encrypt(message):
    """
    Input String, return base64 encoded encrypted String
    """
    if not isinstance(message, str):
        message = str(message)
    byte_array = message.encode("UTF-8")
    padded = __pad(byte_array)
    # generate a random iv and prepend that to the encrypted result.
    iv = os.urandom(AES.block_size)
    # The recipient then needs to unpack the iv and use it.
    key = ENCRYPT_KEY
    cipher = AES.new(key.encode('UTF-8'), AES.MODE_CBC, iv )
    encrypted = cipher.encrypt(padded)
    # Note we PREPEND the unencrypted iv to the encrypted message
    return base64.b64encode(iv+encrypted).decode("UTF-8")

def decrypt(message):
    """
    Input encrypted bytes, return decrypted bytes, using iv and key
    """

    byte_array = base64.b64decode(message)

    iv = byte_array[0:16] # extract the 16-byte initialization vector
    key = ENCRYPT_KEY

    messagebytes = byte_array[16:] # encrypted message is the bit after the iv

    cipher = AES.new(key.encode("UTF-8"), AES.MODE_CBC, iv )

    decrypted_padded = cipher.decrypt(messagebytes)

    decrypted = __unpad(decrypted_padded)
    return decrypted.decode("UTF-8")


def decrypt_body(body:dict):
    decrypted = {}
    for k,v in body.items():
        if k in LIST_KEYS:
            decrypted[k] = [decrypt_body(sub_body) for sub_body in body[k]]
        else:
            decrypted[k] = v if k == 'token' else KEY_TYPES.get(k,str)(decrypt(v)) 
    return decrypted

def encrypt_body(body:dict):
    encrypted = {}
    for k,v in body.items():
        if k == 'ingredient_ids':
            encrypted[k] = [encrypt(item) for item in body[k]]
        elif k in LIST_KEYS:
            encrypted[k] = [encrypt_body(sub_body) for sub_body in body[k]]
        else:
            encrypted[k] = v if k == 'token' else encrypt(v)
    return encrypted 