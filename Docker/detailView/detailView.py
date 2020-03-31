from flask import Flask
from flask_cors import CORS
from flask import request

app = Flask(__name__)
cors = CORS(app, resources={r"*": {"origins": "*"}})

import encryption as enc
import json
import boto3
import json

cip = enc.cloudCipher()


aws_access_key_id=""
aws_secret_access_key=""
aws_session_token=""

dynamodb = boto3.resource('dynamodb',region_name='us-east-1',
    aws_access_key_id=aws_access_key_id,
    aws_secret_access_key=aws_secret_access_key,
    aws_session_token=aws_session_token)

table = dynamodb.Table('CSCI5409_tourism_places')

@app.route('/description/<id>')
def search(id):
    id = cip.decrypt(id)
    response = table.get_item(
        Key={
            'place_id': int(id)
        }
    )
    item = response['Item']
    item["place_id"] = int(item["place_id"])
    return cip.encrypt(json.dumps(item,ensure_ascii=False))
    
@app.route('/setAWS') 
def serAwsKey():
    print('set')
    aws_access_key_id =request.headers['aws_access_key_id']
    aws_secret_access_key =request.headers['aws_secret_access_key']
    aws_session_token =request.headers['aws_session_token']

    dynamodb = boto3.resource('dynamodb',region_name='us-east-1',
    aws_access_key_id=aws_access_key_id,
    aws_secret_access_key=aws_secret_access_key,
    aws_session_token=aws_session_token)
    

    return "OK"


if __name__ == "__main__":
    app.run(host='0.0.0.0',debug=True,port=7080)
