from flask import Flask
from flask_cors import CORS
import encryption as enc

app = Flask(__name__,static_folder='templates')
cors = CORS(app, resources={r"*": {"origins": "*"}})

import json
cip = enc.cloudCipher()

@app.route('/search/<place>')
def search(place):
    place = cip.decrypt(place)
    fp = open("static/new_tourism.csv", "r")
    lines = fp.readlines()
    name_list = []
    counter = 0
    dict_place = {}
    search_list=[]
    for line in range(1, len(lines)):
        words = lines[line].split(",")
        tup = (words[1], words[2],"--__--"+str(words[0]))
        name_list.append(tup)


    for i in name_list:
        if (place.lower() in i[0].lower() or place.lower() in i[1].lower()):
            whole = i[int(0)] + "," + i[1]
            dict_place[counter] = whole
            search_list.append(i[int(0)] + ", " + i[1].title()+i[2])
            counter += 1
    final_json = json.dumps(search_list)
    return cip.encrypt(final_json)

if __name__ == "__main__":
    app.run(host='0.0.0.0',debug=True,port=5050)