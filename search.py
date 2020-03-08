from flask import Flask
from flask_cors import CORS

app = Flask(__name__)
cors = CORS(app, resources={r"/search/*": {"origins": "*"}})

import json



@app.route('/search/<place>')
def search(place):
    fp = open("C:\\Users\\Hardik\\Desktop\\new_tourism.csv", "r")
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
    return final_json

if __name__ == "__main__":
    app.run(debug=True,port=5000)