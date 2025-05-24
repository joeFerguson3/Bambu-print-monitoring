from flask import Flask, request
import paho.mqtt.client as mqtt
import ssl
import json
import time


# Gets printer details
with open("printer-details.txt", "r") as file:
    PRINTER_IP = (file.readline()).strip()
    ACCESS_CODE = (file.readline()).strip()
    SERIAL_NUMBER = (file.readline()).strip()

print("IP: " + PRINTER_IP)
print("ACCESS CODE: " + ACCESS_CODE)
print("SERIAL NUMBER: " + SERIAL_NUMBER)
REQUEST_TOPIC = f"device/{SERIAL_NUMBER}/request"


def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected successfully.")
        # Subscribe to status reports
        client.subscribe(f"device/{SERIAL_NUMBER}/report")

        # File to print
        payload = {
            "print": {
                "command": "project_file",
                "url": "file:///sdcard/test.3mf",
                "param": "Metadata/plate_1.gcode",
                "subtask_id": "0",
                "use_ams": False,
                "timelapse": False,
                "flow_cali": False,
                "bed_leveling": True,
                "layer_inspect": True,
                "vibration_cali": False,
            }
        }

        # Send request
        client.publish(REQUEST_TOPIC, json.dumps(payload))
        print("Command sent.")
    else:
        print(f"Connection failed: {rc}")


# Gets printer status
def on_message(client, userdata, msg):
    print(f"Message from {msg.topic}: {msg.payload.decode()}")


# Set up client
client = mqtt.Client()
client.username_pw_set("bblp", ACCESS_CODE)
client.tls_set(cert_reqs=ssl.CERT_NONE)
client.tls_insecure_set(True)
client.on_connect = on_connect
client.on_message = on_message

# Connect and loop
client.connect(PRINTER_IP, 8883)
client.loop_forever()
