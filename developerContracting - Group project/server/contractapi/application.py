from flask import Flask
from flask_cors import CORS

def create_app(app_name='CONTRACT_API'):
  app = Flask(app_name)
  app.config.from_object('contractapi.config.BaseConfig')
  
  CORS(app, resources={r"/api/*": {"origins": "*"}})

  from contractapi.api import api
  app.register_blueprint(api, url_prefix="/api")
  
  from contractapi.models import db
  db.init_app(app)

  return app