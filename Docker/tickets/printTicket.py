import boto3
from flask import Flask, request
from flask_cors import CORS
import encryption as enc

import json

app = Flask(__name__)
cors = CORS(app, resources={r"*": {"origins": "*"}})

dynamodb = boto3.resource('dynamodb', 'us-east-1')
table = dynamodb.Table('details')

cip = enc.cloudCipher()


@app.route('/getDetails/')
def search():
    id_val = str(request.args.get("id"))
    id_val = str(cip.decrypt(id_val))

    response = table.get_item(
        Key={
            'id': id_val
        }
    )
    return cip.encrypt(json.dumps(response["Item"]))


if __name__ == "__main__":
    app.run(host='0.0.0.0', debug=True, port=5010)
