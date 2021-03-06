#!/usr/bin/env python

#python script that de-registers a node with the pi home automation server so that the server knows that it is no longer running
#should be called at Pi (or other mini-computer) shutdown
#usage: ./nodeshutdown.py [docussandra-url] [node-name]
#author: Jeffrey DeYoung
#create date: 02 Jan 16

import requests
from requests.auth import HTTPDigestAuth
import json
import sys
import socket
import fcntl
import struct

def register():
    print "Node shutdown script started. Using server endpoint: " + sys.argv[1]
    base_url = sys.argv[1] + "/pihomeautomation/sensor_nodes"
    query_url = base_url + "/queries"
    where = "{\"where\":\"name = '"+sys.argv[2]+"'\"}"
    print "Posting: " + where + " to: " + query_url
    ip = get_ip('eth0')
    print "Node ip: " + ip
    node_json = {'name': sys.argv[2], 'ip': ip, 'running': False}
    res = requests.post(query_url, data=where)
    if(res.ok):    
        jData = json.loads(res.content)
        if(len(jData) == 0):
            print "This node has not been previously registered. Creating now (although, it will be created as a non-running node)."
            print "Posting: " + json.dumps(node_json) + " to: " + base_url + "/"
            create = requests.post(base_url + "/", json.dumps(node_json))
            if(create.ok):
                print "Created new node registration."
            else:
                print "Could not create new node registration."
                create.raise_for_status()
        else:
            print "Node was previously registered. Updating as no longer running."
            print json.dumps(jData[0]['id'])
            node_url = base_url + "/" + jData[0]['id']
            #node_json['id'] = jData[0]['id']
            print "Putting: " + json.dumps(node_json) + " to: " + node_url
            update = requests.put(node_url, json.dumps(node_json))
            if(update.ok):
                print "Updated node registration."
            else:
                print "Could not update node registration."
                update.raise_for_status()            
    else:
        print "Could not call out to Docussandra."
        res.raise_for_status()
    print "Done!"

def get_ip(ifname):
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    return socket.inet_ntoa(fcntl.ioctl(
        s.fileno(),
        0x8915,  # SIOCGIFADDR
        struct.pack('256s', ifname[:15])
    )[20:24])



#run the register method by default
if __name__ == "__main__":
    register()
