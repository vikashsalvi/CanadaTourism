from flask import Flask
from flask_cors import CORS

app = Flask(__name__)
cors = CORS(app, resources={r"/search/*": {"origins": "*"}})

import json
import boto3
import json

dynamodb = boto3.resource('dynamodb','us-east-1')
table = dynamodb.Table('CSCI5409_tourism_places')

@app.route('/description/<id>')
def search(id):
    response = table.get_item(
        Key={
            'place_id': int(id)
        }
    )
    item = response['Item']
    item["place_id"] = int(item["place_id"])
    return json.dumps(item)
if __name__ == "__main__":
    app.run(debug=True,port=5050)