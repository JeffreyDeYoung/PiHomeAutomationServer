#!/usr/bin/python

#listens for our flood two way switches to change state
#requirements: apt-get install python-requests

import RPi.GPIO as GPIO
import time
import datetime
import requests
from requests.auth import HTTPDigestAuth
import json
import sys


GPIO.setmode(GPIO.BOARD)

switch_one_state=0;
switch_two_state=0;

def main():
	print "Flood switch listener starting up..." + str(datetime.datetime.now())
	GPIO.setmode(GPIO.BOARD)
	GPIO.setup(11, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
	GPIO.setup(13, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
	global switch_one_state
	global switch_two_state
	switch_one_state=GPIO.input(11)
	switch_two_state=GPIO.input(13)
	print "Switch one is: " + str(switch_one_state)
	print "Switch two is: " + str(switch_two_state)
	print "Flood switch listener started successfully."
	sys.stdout.flush()
	GPIO.add_event_detect(11, GPIO.BOTH, callback=stateChange, bouncetime=1800)
	GPIO.add_event_detect(13, GPIO.BOTH, callback=stateChange, bouncetime=1800)
	while True:
		time.sleep(1)

	GPIO.cleanup()

def stateChange(switch_state):
	print "["+str(datetime.datetime.now())+"]Switch state: " + str(switch_state)
	sys.stdout.flush()
	#make sure we didn't just pick up some random interfearence
        global switch_one_state
        global switch_two_state
	counter=0
	while(counter < 3):
		print "Counter: " + str(counter)
		if(switch_state == 11):
			if(GPIO.input(switch_state) == switch_one_state):
				print "Just detected noise on switch one; ignoring."
				return
		if(switch_state == 13):
                        if(GPIO.input(switch_state) == switch_two_state):
                                print "Just detected noise on switch two; ignoring."
                                return
		counter+=1
		time.sleep(0.1)
	
	#ok, we've confirmed it's not noise
	#set the new state
	if(switch_state == 11):
                switch_one_state = GPIO.input(switch_state)
		print "["+str(datetime.datetime.now())+"]Switch one turning on."
        if(switch_state == 13):
		switch_two_state = GPIO.input(switch_state)
		print "["+str(datetime.datetime.now())+"]Switch two turning on."
	
	current_state = checkCurrentState()
        new_state = "OFF"
        if current_state == "OFF":
	        new_state = "ON"
        print "New state: " + new_state
	postNewState(new_state)
	sys.stdout.flush()

def postNewState(new_state):
	this_node_state_url="http://10.0.0.9:8081/databases/pihomeautomation/tables/actor_ability_status/documents/0661a2f2-16bb-4a81-8dcf-747e730a0a6b" #TODO: remove h$
        res = requests.put(this_node_state_url, '{\"name\": \"HouseActor_Floods\", \"state\": \"'+new_state+'\"}')
	if(res.ok):
		print "successfully turned lights: " + new_state
	else:
		print "could not turn lights: " + new_state
		print str(res)


def checkCurrentState():
	this_node_state_url="http://10.0.0.9:8081/databases/pihomeautomation/tables/actor_ability_status/documents/0661a2f2-16bb-4a81-8dcf-747e730a0a6b" #TODO: remove hard code
	res = requests.get(this_node_state_url)
	if(res.ok):
		jData = json.loads(res.content)
		#print str(jData)
		current_state=jData['object']['state']
		#print current_state
		return current_state;
	else:
		print "Cannot connect to docussandra or cannot find myself registered."


if __name__ == "__main__": main()

