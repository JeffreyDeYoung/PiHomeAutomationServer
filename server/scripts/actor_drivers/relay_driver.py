#!/usr/bin/python
#prototype; not done, committing for now

import RPi.GPIO as GPIO
import time
GPIO.setmode(GPIO.BOARD)

def main():
	print "Relay command started"
	GPIO.setmode(GPIO.BOARD)
	GPIO.setup(11, GPIO.OUT)
	print "Relay control setup sucessfully."
	GPIO.output(11, True)
	time.sleep(10)
	GPIO.output(11, False)
	time.sleep(10)
	GPIO.cleanup()

if __name__ == "__main__": main()

