from tokenize import Name
from MySQLdb import Date
from flask_sqlalchemy import SQLAlchemy
from werkzeug.security import generate_password_hash, check_password_hash
from flask_marshmallow import Marshmallow
import datetime

db = SQLAlchemy()
ma = Marshmallow()

class Developers(db.Model):
  __tablename__ = 'Developers'
  DeveloperID = db.Column(db.Integer, primary_key=True)
  DeveloperEmail = db.Column(db.String(125))
  Password = db.Column(db.String(512))
  Name = db.Column(db.String(45))
  Surname = db.Column(db.String(45))
  DOB = db.Column(db.String(60))
  Country = db.Column(db.DateTime)
  Description = db.Column(db.String(3000))
  
  def __init__(self, DeveloperID, DeveloperEmail, Password, Name, Surname, Country, DOB, Description):
    self.DeveloperID = DeveloperID
    self.DeveloperEmail = DeveloperEmail
    self.Password = generate_password_hash(Password, method='sha256')
    self.Name = Name
    self.Surname = Surname
    self.Country = Country
    self.DOB = DOB
    self.Description = Description
  
  @classmethod
  def authenticate(cls, **kwargs):
    DeveloperEmail = kwargs.get('DeveloperEmail')
    Password = kwargs.get('Password')
    
    if not DeveloperEmail or not Password:
      return None

    user = cls.query.filter_by(DeveloperEmail=DeveloperEmail).first()
    if not user or not check_password_hash(user.Password, Password):
      return None

    return user

  def to_dict(self):
    return dict(DeveloperID=self.DeveloperID, DeveloperEmail=self.DeveloperEmail, Name=self.Name, Surname=self.Surname, DOB=self.DOB, Country=self.Country, Description=self.Description)

  def all_dict(self):
    return {c.name: getattr(self, c.name) for c in self.__table__.columns}

class Contracts(db.Model):
  __tablename__ = 'Contracts'
  ContractID  = db.Column(db.Integer, primary_key=True)
  CompanyID = db.Column(db.Integer)
  ContractLength = db.Column(db.Integer)
  ContractValue = db.Column(db.Float)
  ContractDescription = db.Column(db.String(300))
  InOffice = db.Column(db.Boolean)
  DateIssued = db.Column(db.DateTime)
  Open = db.Column(db.Boolean)
  OfficeAddress = db.Column(db.String(100))
  ContractName = db.Column(db.String(50))
  DeveloperID = db.Column(db.Integer)
  
  def __init__(self, ContractID, CompanyID, ContractLength, ContractValue, ContractDescription, InOffice, DateIssued, Open, OfficeAddress, ContractName, DeveloperID):
      self.ContractID = ContractID
      self.CompanyID = CompanyID
      self.ContractLength = ContractLength
      self.ContractValue = ContractValue
      self.ContractDescription = ContractDescription
      self.InOffice = InOffice
      self.DateIssued = DateIssued
      self.Open = Open
      self.OfficeAddress = OfficeAddress
      self.ContractName = ContractName
      self.DeveloperID = DeveloperID
  
  def to_dict(self):
    return dict(ContractID=self.ContractID, CompanyID=self.CompanyID, ContractLength=self.ContractLength, ContractValue=self.ContractValue, ContractDescription=self.ContractDescription, InOffice=self.InOffice, DateIssued=self.DateIssued, Open=self.Open, OfficeAddress=self.OfficeAddress, ContractName=self.ContractName, DeveloperID=self.DeveloperID)

  def as_dict(self):
    return {c.name: getattr(self, c.name) for c in self.__table__.columns}

class ApplicationsInterface(db.Model):
  __tablename__ = 'ApplicationsInterface'
  ApplicationID = db.Column(db.Integer, primary_key=True)
  DeveloperID = db.Column(db.Integer)
  ContractID = db.Column(db.Integer)
  
  def __init__(self, ApplicationID, DeveloperID, ContractID):
    self.ApplicationID = ApplicationID
    self.DeveloperID = DeveloperID
    self.ContractID = ContractID
  
  def to_dict(self):
    return dict(ApplicationID=self.ApplicationID, DeveloperID=self.DeveloperID, ContractID=self.ContractID)

class Companies(db.Model):
  __tablename__ = 'Companies'
  CompanyID = db.Column(db.Integer, primary_key=True)
  CompanyName = db.Column(db.String(63))
  Password = db.Column(db.String(512))
  GeneralIndustry = db.Column(db.String(215))
  CompanyAddress = db.Column(db.String(100))
  YearEstablished = db.Column(db.Integer)
  CompanyEmail = db.Column(db.String(100))
  CompanyPhone = db.Column(db.String(45))
  
  def __init__(self, CompanyID, CompanyName, Password, GeneralIndustry, CompanyAddress, YearEstablished, CompanyEmail, CompanyPhone):
    self.CompanyID = CompanyID
    self.CompanyName = CompanyName
    self.Password = generate_password_hash(Password, method='sha256')
    self.GeneralIndustry = GeneralIndustry
    self.CompanyAddress = CompanyAddress
    self.YearEstablished = YearEstablished
    self.CompanyEmail = CompanyEmail
    self.CompanyPhone = CompanyPhone
  
  @classmethod
  def authenticate(cls, **kwargs):
    CompanyName = kwargs.get('CompanyName')
    Password = kwargs.get('Password')
    
    if not CompanyName or not Password:
      return None

    user = cls.query.filter_by(CompanyName=CompanyName).first()
    if not user or not check_password_hash(user.Password, Password):
        return None

    return user
  
  def to_dict(self):
    return dict(CompanyID=self.CompanyID, CompanyName=self.CompanyName, GeneralIndustry=self.GeneralIndustry, CompanyAddress=self.CompanyAddress, YearEstablished=self.YearEstablished, CompanyEmail=self.CompanyEmail, CompanyPhone=self.CompanyPhone)

class ContractSchema(ma.SQLAlchemyAutoSchema):
  class Meta:
    model = Contracts

class CompanySchema(ma.SQLAlchemyAutoSchema):
  class Meta:
    model = Companies

class DeveloperSchema(ma.SQLAlchemyAutoSchema):
  class Meta:
    model = Developers