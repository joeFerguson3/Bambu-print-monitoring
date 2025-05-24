from flask import Flask, request
import paho.mqtt.client as mqtt
import ssl
import json
import time

app = Flask(__name__)

# Gets printer details
with open("printer-details.txt", "r") as file:
    PRINTER_IP = (file.readline()).strip()
    ACCESS_CODE = (file.readline()).strip()
    SERIAL_NUMBER = (file.readline()).strip()

print("IP: " + PRINTER_IP)
print("ACCESS CODE: " + ACCESS_CODE)
print("SERIAL NUMBER: " + SERIAL_NUMBER)
REQUEST_TOPIC = f"device/{SERIAL_NUMBER}/request"

# When succcessfully connected to printer
def on_connect(client, userdata, flags, rc):
    if rc == 0:
        print("Connected successfully.")
        # Subscribe to status reports
        client.subscribe(f"device/{SERIAL_NUMBER}/report")

    else:
        print(f"Connection failed: {rc}")

# Gets printer status
def on_message(client, userdata, msg):
    global status
    status = (f"Message from {msg.topic}: {msg.payload.decode()}")
    print(f"Message from {msg.topic}: {msg.payload.decode()}")

#Setup MQTT client
client = mqtt.Client()
client.username_pw_set("bblp", ACCESS_CODE)
client.tls_set(cert_reqs=ssl.CERT_NONE)
client.tls_insecure_set(True)
client.on_connect = on_connect
client.on_message = on_message

client.connect(PRINTER_IP, 8883)
client.loop_start()  # Must be loop_start() if using Flask

# Requests a print from printer
def print_file():
    # File to print
    print("Printing file")
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

# Prints requested file
@app.route('/print-file', methods=['POST'])
def print_request():
    print_file()
    return "Printing file", 200

# Returns printer status
@app.route('/status', methods=['POST'])
def status():
    return status, 200

if __name__ == '__main__':
    app.run(port=5000)
