import re
from flask import Flask,request, jsonify
from flask_cors import CORS
import encryption as enc
from datetime import datetime
import uuid
import json
import boto3
from boto3.dynamodb.conditions import Key, Attr
import simplejson as js

app = Flask(__name__)
cors = CORS(app, resources={r"*": {"origins": "*"}})
cip = enc.cloudCipher()

PATTERN = '1111111111111111'


@app.route('/validate', methods=['POST'])
def validate():
    name=request.json['name']
    source=request.json['source']
    cardNumber=request.json['cardNumber']
    destination = request.json['destination']
    mm=request.json['mm']
    cvv=request.json['cvv']
    yy=request.json['yy']
    dot=request.json['dot']
    price = request.json['price']
    
    present = datetime.now()
    
    errors = ""

    if(name==""):
        errors += "Invalid name,"
    if(source== ""):
        errors +=  "Invalid source,"
    if(cardNumber !=  PATTERN):
        errors +=  "Invalid Card number,"
    if(mm == "" or int(mm) > 13 or int(mm) < 0):
        errors +=  "Invalid month entered,"
    if(cvv == "" or len(cvv) >3 or len(cvv) < 0 ):
        errors +=  "Invalid CVV number entered,"
    if(yy == ""):
        errors +=  "Invalid year entered,"
    if(dot == ""):
        errors += "Invalid date,"
    elif(datetime.strptime(dot,'%Y-%m-%d').date() <= present.date()):
        errors += "Date should be greater than today's date,"
    if(price == ""):
        errors += "Invalid price,"

    if errors == "": 
        return "1"
    else: return errors




aws_access_key_id=""
aws_secret_access_key=""
aws_session_token=""

dynamodb = boto3.resource('dynamodb',region_name='us-east-1',
    aws_access_key_id=aws_access_key_id,
    aws_secret_access_key=aws_secret_access_key,
    aws_session_token=aws_session_token)

table = dynamodb.Table('CSCI5409_booking_details')

@app.route('/insertTicket',methods=['GET','POST'])
def insert():

    name=request.json['name']
    source=request.json['source']
    cardNumber=request.json['cardNumber']
    destination = request.json['destination']
    mm=request.json['mm']
    cvv=request.json['cvv']
    yy=request.json['yy']
    dot=request.json['dot']
    price = request.json['price']
    total_seats = int(int(price)/10)
    Id = str(uuid.uuid4())
    
    response = table.put_item(
        Item={
        'booking_id': Id, 
        'book_date': str(datetime.now().date()),
        'name': name,
        'from': source,
        'to': destination,
        'dep_date': dot,
        'nseats': total_seats,
        'total':price
    }
    )
    return  Id;

    

@app.route('/getTicket',methods=['GET','POST'])
def selectTicket():
    id = request.json['Id']
    res = table.query(KeyConditionExpression=Key('booking_id').eq(id))
    print(res['Items'][0])
    return js.dumps(res['Items'][0])
if __name__ == "__main__":
    app.run(host='0.0.0.0',debug=True,port=5052)
