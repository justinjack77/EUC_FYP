# EUC_FYP
Upload to ESP8266MOD

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
