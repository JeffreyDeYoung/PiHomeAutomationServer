#!/usr/bin/python

#listens for our smoke detector to go off and sends emails based on that
#requirements: apt-get install python-requests

import RPi.GPIO as GPIO
import time
import datetime
import smtplib
import sys
from email.MIMEMultipart import MIMEMultipart
from email.MIMEText import MIMEText

GPIO.setmode(GPIO.BOARD)

switch_one_state=0
alerting=False

def main():
	print "Fire watcher starting up..." + str(datetime.datetime.now())
        channel=15
	GPIO.setmode(GPIO.BOARD)
	GPIO.setup(channel, GPIO.IN, pull_up_down=GPIO.PUD_DOWN)
	global switch_one_state
	switch_one_state=GPIO.input(channel)
	print "State is: " + str(switch_one_state)
	print "Fire watcher started successfully."
	sys.stdout.flush()
	GPIO.add_event_detect(channel, GPIO.BOTH, callback=stateChange, bouncetime=50)
	while True:
		time.sleep(1)

	GPIO.cleanup()

def stateChange(switch_state):
	global alerting
	if not alerting:
		alerting=True
		print "["+str(datetime.datetime.now())+"]Switch state: " + str(switch_state)
        	print "FIRE!"
		sendEmail()
		sys.stdout.flush()
		time.sleep(10) #at most send emails every 10 seconds
		alerting=False
	else:
		print "Already presently alerting; will not re-send a message."
        
def sendEmail():
	email_from = "FROM"
	server = smtplib.SMTP('smtp.gmail.com', 587)
	server.starttls()
	server.login(email_from, "PASSWORD")
        msg = MIMEMultipart()
 	msg_str = str("HOUSE FIRE! " + str(datetime.datetime.now()))
	msg.attach(MIMEText(msg_str, 'plain'))
	msg['Subject'] = msg_str
	to = []
	to.append('one@exaple.com')
	to.append('two@example.com')
	server.sendmail(email_from, to, msg.as_string())
	server.quit()

if __name__ == "__main__": main()

