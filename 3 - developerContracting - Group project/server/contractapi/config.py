from dotenv import load_dotenv
import os

load_dotenv()

class BaseConfig(object):
    DEBUG = True
    SQLALCHEMY_DATABASE_URI = 'mysql://' + os.environ['DB_USERNAME'] + ':' + os.environ['DB_PASSWORD'] + '@127.0.0.1:3306/' + os.environ['DB_DATABASE']
    SQLALCHEMY_TRACK_MODIFICATIONS = False
    # used for encryption and session management
    SECRET_KEY = 'f3cfe9ed8fae309f02079dbf'