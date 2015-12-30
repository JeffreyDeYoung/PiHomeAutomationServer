#!/usr/bin/env python

#python script that registers a node with the pi home automation server so that the server knows that its there
#should be called at Pi (or other mini-computer) startup
#usage: ./nodestartup.py [docussandra-url]
#author: Jeffrey DeYoung
#create date: 30 Dec 15

import requests
from requests.auth import HTTPDigestAuth
import json
import sys

def register():
    print "Node startup script started. Using server endpoint: " + sys.argv[1]
    myResponse = requests.get(url, verify=True)


#run the register method by default
if __name__ == "__main__":
    register()
