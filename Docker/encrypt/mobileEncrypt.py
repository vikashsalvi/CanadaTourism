from flask import Flask, request
import string
from flask_cors import CORS
import json

special = string.punctuation + " " + "’"

app = Flask(__name__)
cors = CORS(app, resources={r"/search/*": {"origins": "*"}})


@app.route("/encrypt")
def encrypt():
    text = request.args.get("text")
    key = "HelloWorldWarZStarts19"

    key_list = []
    input_list = []
    cipher = ""

    for i in range(65, 91):
        input_list.append(chr(i))

    for i in range(97, 123):
        input_list.append(chr(i))

    for i in range(48, 58):
        input_list.append(chr(i))

    for i in key:
        if (i not in key_list):
            key_list.append(i)

    for i in range(65, 91):
        if (chr(i) not in key_list):
            key_list.append(chr(i))

    for i in range(97, 123):
        if (chr(i) not in key_list):
            key_list.append(chr(i))

    for i in range(48, 58):
        if (chr(i) not in key_list):
            key_list.append(chr(i))

    for i in text:
        if (i in special):
            cipher += i
        else:
            for j in range(len(input_list)):
                if (i == input_list[j]):
                    cipher += key_list[j]
    dict = {}
    dict["output"] = cipher
    return json.dumps(dict)


@app.route("/decrypt")
def decrypt():
    text = request.args.get("text")
    key = "HelloWorldWarZStarts19"

    key_list = []
    input_list = []
    plain = ""

    for i in range(65, 91):
        input_list.append(chr(i))

    for i in range(97, 123):
        input_list.append(chr(i))

    for i in range(48, 58):
        input_list.append(chr(i))

    for i in key:
        if (i not in key_list):
            key_list.append(i)

    for i in range(65, 91):
        if (chr(i) not in key_list):
            key_list.append(chr(i))

    for i in range(97, 123):
        if (chr(i) not in key_list):
            key_list.append(chr(i))

    for i in range(48, 58):
        if (chr(i) not in key_list):
            key_list.append(chr(i))

    for i in text:
        if (i in special):
            plain += i
        else:
            for j in range(len(key_list)):
                if (i == key_list[j]):
                    plain += input_list[j]
    dict = {}
    dict["output"] = plain
    return json.dumps(dict)


app.run(debug=True, port=5005)
