#!/usr/bin/python

import RPi.GPIO as GPIO
import time
GPIO.setmode(GPIO.BOARD)

def main():
	print "Vibration listener started."
	GPIO.setmode(GPIO.BOARD)
	GPIO.setup(3, GPIO.IN)
	print "Vibration listener setup sucessfully."
	GPIO.add_event_detect(3, GPIO.FALLING, callback=vibrationDetected, bouncetime=300)	
	while True:
		time.sleep(1)
	GPIO.cleanup()

def vibrationDetected(arg):
	print "Vibration detected: " + str(arg)

if __name__ == "__main__": main()

