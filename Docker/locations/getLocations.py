from flask import Flask
from flask_cors import CORS
from boto3.dynamodb.conditions import Key, Attr
import simplejson as json


app = Flask(__name__)
cors = CORS(app, resources={r"*": {"origins": "*"}})

import encryption as enc
import boto3
# import json

cip = enc.cloudCipher()

aws_access_key_id=""
aws_secret_access_key=""
aws_session_token=""



dynamodb = boto3.resource('dynamodb', region_name='us-east-1',
                          aws_access_key_id=aws_access_key_id,
                          aws_secret_access_key=aws_secret_access_key,
                          aws_session_token=aws_session_token)

table = dynamodb.Table('CSCI5409_tourism_places')


@app.route('/getLocation/')
def search():

    response = table.scan(
        FilterExpression=Attr('place_id').gte(0)
                          )
    places_list = []
    for x in response['Items']:
        places_list.append(x)

    return (json.dumps(places_list))


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True, port=8085)
