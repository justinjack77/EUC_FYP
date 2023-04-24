# EUC_FYP
Upload to ESP8266MOD
User Guides
1.	Install Android studio and Arduino IDE
2.	In Arduino IDE add Additional boards manager URLs: http://arduino.esp8266.com/stable/package_esp8266com_index.json
3.	To add Additional bord go to File > Preferences > Additional boards manager URLs: copy and paste this “http://arduino.esp8266.com/stable/package_esp8266com_index.json”
4.	Upload code to ESP8266MOD
5.	Clone the project from github (https://github.com/justinjack77/EUC_FYP/tree/master)
6.	Make sure you download Virtual Device in Android Studio 
7.	Run and Test
8.	Also can build APK file and run on physical device


ESP8266MOD here: 

#include <ESP8266WiFi.h>
#include <ESP8266WebServer.h>

const char* ssid =  "WIFI SSID HERE";
const char* password = "WIFI PASSWORD";

ESP8266WebServer server(80);
void handleRoot() {
  server.send(200, "text/plain", "ESP8266 Web Server");
}
void handleGpioOn() {
  String pin = server.arg("pin");
  int gpioPin = pin.toInt();
  pinMode(gpioPin, OUTPUT);
  digitalWrite(gpioPin, HIGH);
  server.send(200, "text/plain", "GPIO " + pin + " turned on");
}
void handleGpioOff() {
  String pin = server.arg("pin");
  int gpioPin = pin.toInt();
  pinMode(gpioPin, OUTPUT);
  digitalWrite(gpioPin, LOW);
  server.send(200,"text/plain", "GPIO " + pin + " turned off");
}
void setup() {
  Serial.begin(115200);
  WiFi.begin(ssid, password);

  Serial.println("Connecting to hidden network...");
  while (WiFi.status() != WL_CONNECTED) {
    delay(1000);
    Serial.println("Connecting to WiFi...");
  }
  Serial.println("Connected to WiFi");
  Serial.print("IP Address: ");
  Serial.println(WiFi.localIP());
  server.on("/", handleRoot);
  server.on("/gpio_on", handleGpioOn);
  server.on("/gpio_off", handleGpioOff);
  server.begin();
  Serial.println("HTTP server started.");
}
void loop() {
  server.handleClient();
  digitalWrite(LED_BUILTIN, HIGH); // Turn on the built-in LED as an indicator
  delay(1000);
  digitalWrite(LED_BUILTIN, LOW); // Turn off the built-in LED as an indicator
  delay(1000);
}
