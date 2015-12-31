#!/usr/bin/env python

#python script that registers a node with the pi home automation server so that the server knows that its there
#should be called at Pi (or other mini-computer) startup
#usage: ./nodestartup.py [docussandra-url] [node-name]
#author: Jeffrey DeYoung
#create date: 30 Dec 15

import requests
from requests.auth import HTTPDigestAuth
import json
import sys

def register():
    print "Node startup script started. Using server endpoint: " + sys.argv[1]
    base_url = sys.argv[1] + "/pihomeautomation/sensor_nodes"
    query_url = base_url + "/queries"
    where = "{\"where\":\"name = '"+sys.argv[2]+"'\"}"
    print "Posting: " + where + " to: " + query_url
    myResponse = requests.post(query_url, data=where)
    print myResponse


#run the register method by default
if __name__ == "__main__":
    register()
