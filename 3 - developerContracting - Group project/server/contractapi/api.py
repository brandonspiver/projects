from functools import wraps
from datetime import datetime, timedelta

from flask import Blueprint, jsonify, request, current_app

from random import randint, random

import jwt

from flask import Blueprint, jsonify, request
from numpy import True_
from .models import db, Developers, Companies, Contracts, ApplicationsInterface

from sqlalchemy.orm.attributes import flag_modified

api = Blueprint('api', __name__)

def random_digits(N):
  start = 10 ** (N - 1)
  end = (10 ** N) - 1
  return randint(start, end)

@api.route('/hello/<string:name>/')
def say_hello(name):
    response = { 'msg': "Hello {}".format(name) }
    return jsonify(response)

@api.route('/contracts/')
def contracts():
  contracts = Contracts.query.all()
  return jsonify({ 'contracts': [c.to_dict() for c in contracts]})

@api.route('/companies/')
def companies():
  companies = Companies.query.all()
  return jsonify({ 'companies': [c.to_dict() for c in companies]})

@api.route('/applications/')
def applications():
  application = ApplicationsInterface.query.all()
  return jsonify({ 'applications': [a.to_dict() for a in application]})

@api.route('/developers/')
def developers():
  developers = Developers.query.all()
  return jsonify({ 'developers': [d.to_dict() for d in developers]})

@api.route('/contracts/<int:id>/')
def contract(id):
  contract = Contracts.query.filter_by(ContractID=id).first()
  return jsonify({ 'contract': contract.to_dict() })

@api.route('/get-developer/', methods=('POST',))
def get_developer():
  data = request.get_json()
  DeveloperID = data['DeveloperID']
  
  developer = Developers.query.filter_by(DeveloperID=DeveloperID).first()
  
  return jsonify(developer.to_dict()), 201

@api.route('/get-company/', methods=('POST',))
def get_company():
  data = request.get_json()
  CompanyID = data['CompanyID']
  
  company = Companies.query.filter_by(CompanyID=CompanyID).first()
  
  return jsonify(company.to_dict()), 201

@api.route('/accept-contract/', methods=('PUT',))
def accept_contract():
  data = request.get_json()
  ContractID = data['ContractID']
  DeveloperID = data['DeveloperID']
  
  contract = Contracts.query.filter_by(ContractID=ContractID).first()
  
  contract.Open = False
  contract.DeveloperID = DeveloperID
  
  db.session.commit()
  
  return jsonify(contract.to_dict()), 201

@api.route('/apply/', methods=('POST',))
def apply():
  data = request.get_json()
  ApplicationID = random_digits(9)
  DeveloperID = data['DeveloperID']
  ContractID = data['ContractID']
  
  apply = ApplicationsInterface(ApplicationID=ApplicationID, DeveloperID=DeveloperID, ContractID=ContractID)
  db.session.add(apply)
  db.session.commit()
  
  return jsonify(apply.to_dict()), 201

@api.route('/register-developer/', methods=('POST',))
def register_developer():
  data = request.get_json()
  DeveloperID = random_digits(9)
  DeveloperEmail = data['DeveloperEmail']
  Password = data['Password']
  Name = data['Name']
  Surname = data['Surname']
  Country = data['Country']
  DOB = data['DOB']
  Description = data['Description']
  developer = Developers(DeveloperID, DeveloperEmail, Password, Name=Name, Surname=Surname, Country=Country, DOB=DOB, Description=Description)
  db.session.add(developer)
  db.session.commit()

  return jsonify(developer.to_dict()), 201

@api.route('/register-company/', methods=('POST',))
def register_company():
  data = request.get_json()
  CompanyID = random_digits(9)
  CompanyName = data['CompanyName']
  Password = data['Password']
  GeneralIndustry = data['GeneralIndustry']
  CompanyAddress = data['CompanyAddress']
  YearEstablished = data['YearEstablished']
  CompanyEmail = data['CompanyEmail']
  CompanyPhone = data['CompanyPhone']
  company = Companies(CompanyID=CompanyID, CompanyName=CompanyName, Password=Password, GeneralIndustry=GeneralIndustry, CompanyAddress=CompanyAddress, YearEstablished=YearEstablished, CompanyEmail=CompanyEmail, CompanyPhone=CompanyPhone)
  db.session.add(company)
  db.session.commit()
  
  return jsonify(company.to_dict()), 201

@api.route('/login-developer/', methods=('POST',))
def login_developer():
  data = request.get_json()
  DeveloperEmail = data['DeveloperEmail']
  Password = data['Password']
  developer = Developers.authenticate(DeveloperEmail=DeveloperEmail, Password=Password)
  
  if not developer:
    return jsonify({ 'message': 'Invalid credentials', 'authenticated': False })

  token = jwt.encode({
  'sub': developer.DeveloperEmail,
  'iat': datetime.utcnow(),
  'exp': datetime.utcnow() + timedelta(minutes=60)},
  current_app.config['SECRET_KEY'])

  return jsonify({ 'token': token, 'DeveloperID': developer.DeveloperID })

@api.route('/login-company/', methods=('POST',))
def login_company():
  data = request.get_json()
  CompanyName = data['CompanyName']
  Password = data['Password']
  company = Companies.authenticate(CompanyName=CompanyName, Password=Password)

  if not company:
    return jsonify({ 'message': 'Invalid credentials', 'authenticated': False }), 401

  token = jwt.encode({
  'sub': company.CompanyName,
  'iat': datetime.utcnow(),
  'exp': datetime.utcnow() + timedelta(minutes=60)},
  current_app.config['SECRET_KEY'])
  return jsonify({ 'token': token , 'CompanyID': company.CompanyID })

def developer_token_required(f):
  @wraps(f)
  def dev_verify(*args, **kwargs):
    auth = request.headers.get('Authorization', '').split()

    invalid = {
        'message': 'Invalid',
        'authenticated': False
    }

    if len(auth) != 2:
        return jsonify(invalid), 401

    try:
        token = auth[1]
        data = jwt.decode(token, current_app.config['SECRET_KEY'], algorithms=['HS256'])
        developer = Developers.query.filter_by(DeveloperEmail=data['sub']).first()
        if not developer:
            raise RuntimeError('Developer not found')
        return f(developer, *args, **kwargs)
    except jwt.ExpiredSignatureError:
        return jsonify(invalid), 401
    except (jwt.InvalidTokenError, Exception) as e:
        print(e)
        return jsonify(invalid), 401

  return dev_verify

def company_token_required(f):
  @wraps(f)
  def comp_verify(*args, **kwargs):
    auth = request.headers.get('Authorization', '').split()

    invalid = {
        'message': 'Invalid',
        'authenticated': False
    }

    if len(auth) != 2:
        return jsonify(invalid), 401

    try:
      token = auth[1]
      data = jwt.decode(token, current_app.config['SECRET_KEY'], algorithms=['HS256'])
      company = Companies.query.filter_by(CompanyName=data['sub']).first()
      if not company:
          raise RuntimeError('Company not found')
      return f(company, *args, **kwargs)
    except jwt.ExpiredSignatureError:
      return jsonify(invalid), 401
    except (jwt.InvalidTokenError, Exception) as e:
      print(e)
      return jsonify(invalid), 401
  return comp_verify

@api.route('/developer-profile-page/', methods=('GET',))
@developer_token_required
def developer_profile(developer):
  return jsonify(developer.to_dict()), 201

@api.route('/company-profile-page/', methods=('GET',))
@company_token_required
def company_profile(company):
  return jsonify(company.to_dict()), 201

@api.route('/new-contract/', methods=('POST',))
@company_token_required
def new_contract(company):
  data = request.get_json()
  ContractID = random_digits(9)
  CompanyID = company.CompanyID
  ContractLength = data['ContractLength']
  ContractValue = data['ContractValue']
  ContractDescription = data['ContractDescription']
  InOffice = data['InOffice']
  OfficeAddress = data['OfficeAddress']
  ContractName = data['ContractName']
  DateIssued = datetime.now()
  DateIssued = DateIssued.strftime("%Y/%m/%d %H:%M:%S")

  contract = Contracts(ContractID=ContractID, CompanyID=CompanyID, ContractLength=ContractLength, ContractValue=ContractValue, ContractDescription=ContractDescription, InOffice=int(InOffice), OfficeAddress=OfficeAddress, ContractName=ContractName, Open=True, DateIssued=DateIssued, DeveloperID='0')
  db.session.add(contract)
  db.session.commit()
  
  return jsonify(contract.to_dict()), 201