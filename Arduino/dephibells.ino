  #include <PubSubClient.h>
  #include <ESP8266WiFi.h> 
  
  const char* ssid = "Michel";
  const char* password = "sedesenv";
  const int pinoBotao = D7; //PINO DIGITAL UTILIZADO PELO PUSH 
  WiFiServer servera(80);
  const int pinoLed = D3; //PINO DIGITAL UTILIZADO PELO LED
  WiFiClient client;
  
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
      delay(500);
      Serial.print(".");
    }
    Serial.println("");
    Serial.println("WiFi connectado");
   
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
    if(digitalRead(pinoBotao) == LOW){ //SE A LEITURA DO PINO FOR IGUAL A LOW, FAZ
        digitalWrite(pinoLed, HIGH); //ACENDE O LED
         publishSensorData();
        Serial.println("Tocaram a campainha!!!");
        
        delay(2000);
    }else{ //SENÃO, FAZ
      digitalWrite(pinoLed, LOW); //APAGA O LED
    }
  }
  
  // IP address of the MQTT broker
  char server[] = {"iot.eclipse.org"};
  int port = 1883;
  char topic[] = {"codifythings/dephybells"};
  
  void callback(char* topic, byte* payload, unsigned int length)
  {
  }
  
  PubSubClient pubSubClient(server, port, 0, client);
  
  void publishSensorData()
  {
    // Connect MQTT Broker
    Serial.println("[INFO] Connecting to MQTT Broker");
  
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
      pubSubClient.publish(topic, "A campainha da sua casa foi acionada!");
      Serial.println("[INFO] Publish to MQTT Broker Complete");
    }
    else
    {
      Serial.println("[ERROR] Publish to MQTT Broker Failed");
    }
    
    pubSubClient.disconnect(); 
  }
  
  /*****************************************************************************
   * Standard Arduino Functions - setup(), loop()
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
    
    // Calibrate motion sensor  
  }
  
  void loop() 
  {  
    //Read sensor data
    readSensorData();
  }
