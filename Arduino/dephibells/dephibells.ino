#include <PubSubClient.h>
#include <ESP8266WiFi.h>

const char* ssid = "GVT-Michel";
const char* password = "314159265359piMath";
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
  // Check if lights are currently on or off
  if (digitalRead(pinoLed) == LOW)
  {
    //Turn lights on
    //Serial.println("[INFO] Turning lights on");
    digitalWrite(pinoLed, HIGH);
  }
  else
  {
    // Turn lights off
    //Serial.println("[INFO] Turning lights off");
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
  turnLightsOnOff();
}

PubSubClient pubSubClient(server, port, callback, client);

PubSubClient pubSubClient2(server, port, callback, client);

void publishSensorData()
{

  if (pubSubClient.connect("arduinoIoTClient"))
  {

  }
  else
  {
  }

  if (pubSubClient.connected())
  {
      pubSubClient.publish(topic, "A campainha da sua casa foi ");
      pisca(600, 2);
  }
  else
  {
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

  printConnectionInformation();

  if (pubSubClient2.connect("arduinoIoTClient"))
  {
      pubSubClient2.subscribe(topicLed);
      pisca(200, 8);

  }
  else
  {
      pisca(100, 10);
  }
}

void loop()
{
  //Read sensor data
    readSensorData();
    pubSubClient2.loop();
}
