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

aws_access_key_id="ASIAUUVR4XJX3S7LUHB5"
aws_secret_access_key="V/AFjhuONhRxzBdSmwQeS8seGvPgRgB5fFIG8jpT"
aws_session_token="FwoGZXIvYXdzEAkaDMn+qgKZ1B+ElDQfSyK+AV18rU8mK65XqkGv7FFO3KZxjz7r+pHtvgb/dCajYK1BrOkpzSazm/+ghX2weMGt/lyzhRXtRayGCpZPcvgzrx/BpCD/VSTQo2SLByfJL37ftzJpFPaLNErT8q7G94KJn7dCRV2Kq3kM0e0QASNhuoJQjTYAfecymFqK83nx+2U5qnNzArELCKWz+TqKnlw2a4b7i0Z38HEyBDYKmJAerNnloZFUY3sG8lMeuYYRSlZEeo5D8eJSjO3wEGQYuEQowp6I9AUyLd0Z+z5O4Dy8Iubq/CbY2kpfHiFVdLZmYcIGMQ5bFD/I6+hitoYfmy8R84boDA=="




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