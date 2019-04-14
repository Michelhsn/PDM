#include <PubSubClient.h>
#include <ESP8266WiFi.h>

const char* ssid = "Michel";
const char* password = "sedesenv";
const int pinoBotao = D7; //PINO DIGITAL UTILIZADO PELO PUSH
WiFiServer servera(80);
const int pinoLed = D3; //PINO DIGITAL UTILIZADO PELO LED
WiFiClient client;

void pisca(int duracao, int vezes) {
  for (int i = 0; i <= vezes; i++) {
    digitalWrite(pinoLed, HIGH);
    delay(duracao);
    digitalWrite(pinoLed, LOW);
    delay(duracao);
  }
}

void turnLightsOnOff()
{
if(digitalRead(pinoLed) == LOW)
{
//Turn lights on
digitalWrite(pinoLed, HIGH);
}
else
{
// Turn lights off
digitalWrite(pinoLed, LOW);
}
}

void printConnectionInformation()
{
  Serial.begin(115200);
  delay(10);

  // Conecta a rede wifi
  Serial.println();
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(ssid);

  WiFi.begin(ssid, password);

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    pisca(200, 3);
  }
  Serial.println("");
  Serial.println("WiFi connectado");
  pisca(800, 3);

  // Start the server
  servera.begin();
  Serial.println("Server inicializado");

  // Print the IP address
  Serial.print("IP na rede do Node ");
  Serial.print("http://");
  Serial.print(WiFi.localIP());
  Serial.println("/");


}


void readSensorData()
{
  if (digitalRead(pinoBotao) == LOW) { //SE A LEITURA DO PINO FOR IGUAL A LOW, FAZ
    digitalWrite(pinoLed, HIGH); //ACENDE O LED
    publishSensorData();
    Serial.println("Tocaram a campainha!!!");

    delay(2000);
  } else { //SENÃO, FAZ
    //Serial.println("voltou pra cá");
    //digitalWrite(pinoLed, LOW); //APAGA O LED
  }
}

// IP address of the MQTT broker
char server[] = {"iot.eclipse.org"};
int port = 1883;
char topic[] = {"codifythings/dephybells"};
char topicLed[] = {"codifythings/led"};

void callback(char* topic, byte* payload, unsigned int length)
{
  //Serial.println("[INFO] Tá chegando");
  String payloadContent = String((char *)payload);
  Serial.println("[INFO] Payload: " + payloadContent);
  // Turn lights on/off
  turnLightsOnOff();
}

PubSubClient pubSubClient(server, port, callback, client);

PubSubClient pubSubClient2(server, port, callback, client);

void publishSensorData()
{
  // Connect MQTT Broker
  //Serial.println("[INFO] Connecting to MQTT Broker");

  if (pubSubClient.connect("arduinoIoTClient"))
  {
    Serial.println("[INFO] Connection to MQTT Broker Successfull");

  }
  else
  {
    Serial.println("[INFO] Connection to MQTT Broker Failed");
  }

  // Publish to MQTT Topic
  if (pubSubClient.connected())
  {
    Serial.println("[INFO] Publishing to MQTT Broker");
     pubSubClient.publish(topic, "A campainha da sua casa foi ");
      Serial.println("[INFO] Publish to MQTT Broker Complete");
    pisca(600, 2);
  }
  else
  {
    Serial.println("[ERROR] Publish to MQTT Broker Failed");
    pisca(100, 4);
  }

  
}

/*****************************************************************************
   Standard Arduino Functions - setup(), loop()
 ****************************************************************************/

void setup()
{
  Serial.begin(115200);
  delay(10);
  pinMode(pinoBotao, INPUT_PULLUP); //DEFINE O PINO COMO ENTRADA / "_PULLUP" É PARA ATIVAR O RESISTOR INTERNO
  //DO ARDUINO PARA GARANTIR QUE NÃO EXISTA FLUTUAÇÃO ENTRE 0 (LOW) E 1 (HIGH)
  pinMode(pinoLed, OUTPUT); //DEFINE O PINO COMO SAÍDA
  digitalWrite(pinoLed, LOW); //LED INICIA DESLIGADO

  while (!Serial);

  // Print connection information
  printConnectionInformation();

  if (pubSubClient2.connect("arduinoIoTClient"))
  {
   // Serial.println("[INFO] Connection to MQTT Broker Successfull");
    pubSubClient2.subscribe(topicLed);
    pisca(200,8);

  }
  else
  {
    //Serial.println("[INFO] Connection to MQTT Broker Failed");
  }
}

void loop()
{
  //Read sensor data
  readSensorData();
  pubSubClient2.loop();
}
