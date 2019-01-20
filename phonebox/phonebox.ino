/**
 * Code for the forks companion cube.
 * LDR:
 *            pin 34
 *             |
 * 3v3 -- LDR -+- 10kohm -- GND
 * 
 * Display (1.3inch 240x240 ST7789 3wire-SPI display):
 * SCL: 23
 * SDA: 18
 * RES: 22
 * DC:  5
 * BLK: unused
 */

#include <WiFi.h>
#include "FirebaseESP32.h"

#define LOCK_VERSION 2
// This is a c library - it's not String aware
#include <qrcode.h>

#include <TFT_eSPI.h> // Graphics and font library for ST7789 driver chip
#include <SPI.h>
TFT_eSPI tft = TFT_eSPI();  // Invoke library, pins defined in User_Setup.h

#include <TFT_eFEX.h> // Jpg support
TFT_eFEX fex = TFT_eFEX(&tft);

#define FIREBASE_HOST "forksupphonesdown.firebaseio.com"
#define FIREBASE_AUTH ""
#define WIFI_SSID "NachoWifi"
#define WIFI_PASSWORD "abetterworldbymemes"

#define LDR_ADC_PIN 34
#define LDR_ADC_DARK_THRESHOLD 1000
#define LDR_ADC_LIGHT_THRESHOLD 3000

#define ADC_BELOW_THRESHOLD -1
#define ADC_ABOVE_THRESHOLD 1


enum GameState {
  START,    // On reset
  LOBBY,    // Show qr code, wait for game to start
  PLAYING,  // Game started
  END       // Game ended, show stats, set timer to reset
};
/*volatile*/z enum GameState state;

String active_doc;
String active_path;
int endgame_timer;

void setup() {
  // Unconnected analog pin
  randomSeed(analogRead(13));

  Serial.begin(115200);

  //  char randstring[20];
  //  random_b64(randstring, 20);
  //  Serial.print("Random string: ");
  //  Serial.println(randstring);

  // Setup the display
  tft.init();
  tft.invertDisplay(1);
  tft.setRotation(0);
  // Colours seem to be inverted here
  tft.fillScreen(TFT_BLACK);

  tft.setTextColor(TFT_WHITE, TFT_WHITE);
  tft.drawString("Connecting to wifi...", 10, 30, 4); // Draw text centre at position 80, 24 using font 4
  tft.drawString("Looking for ssid:", 10, 90, 4);
  tft.drawString(WIFI_SSID, 10, 120, 4);


  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(300);
  }
  Serial.println();
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  tft.drawString(WiFi.localIP().toString(), 10, 180, 4);

  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);

  //  Firebase.stream("/temp/ch01/sensorValue", streamCallback);

}

void loop() {
  switch (state) {
    case START:
      firebase_setup();
      show_qrcode(active_doc);
      state = LOBBY;
      break;
    case LOBBY:
      // wait for the ADC reading
      wait_for_adc(LDR_ADC_DARK_THRESHOLD, ADC_BELOW_THRESHOLD);
      Serial.println("Playing game!");

      // Update firebase
      active_path = "/games/" + active_doc;
      Firebase.set(active_path + "/mode", String("normal"));
      Firebase.set(active_path + "/start", true);
      state = PLAYING;
      break;
    case PLAYING:
      // Draw the "no phones" image
      tft.fillScreen(TFT_BLACK);
      fex.drawJpeg("/no_phone.jpg", 0, 0);    // Doesn't work??
      tft.drawString("Hands off your", 10, 100, 4);
      tft.drawString("phones!", 10, 130, 4);

      // Wait for the ADC to increase
      wait_for_adc(LDR_ADC_LIGHT_THRESHOLD, ADC_ABOVE_THRESHOLD);

      // Update firebase
      Firebase.set(active_path + "/mode", String("end"));
      Firebase.set(active_path + "/start", false);

      endgame_timer = 10;
      state = END;
      break;
    case END:
      if (endgame_timer <= 0) {
        Firebase.remove(active_path);
        state = START;
      }

      // Display summary stats
      tft.fillScreen(TFT_BLACK);
      tft.drawString("Game is over", 10, 80, 4);
      tft.drawString("Reset in " + String(endgame_timer) + "s...", 10, 140);
      delay(1000);
      endgame_timer--;

      break;
    default:
      state = START;
      break;
  }
}

void streamCallback(streamResult event) {
  String eventType = event.eventType();
  eventType.toLowerCase();
  if (eventType == "put") {
    Serial.println("The stream event path: " + event.path() + ", value: " + String(event.getFloat()));
    Serial.println();
  }

}

//void firebase_setup(void){
//  // Generate a random game name
//  char rand_buf[11];
//  random_b64(rand_buf, 11);
////  String random_name = String(buf);
//
//  // Almost certainly doesn't exist. (Hey, this is a hackathon)
//  String rand_name = "/games/";
//  rand_name.concat(rand_buf);
//  Serial.print("Checking: ");
//  Serial.println(rand_name);
//
//  // Save the active doc path
//  active_doc = rand_name;
//
//  // Set start value...
//
//  JsonVariant result = Firebase.put(active_doc);
//
//  if (!(result["start"].success())) {
//    Serial.println("Not existent");
//  } else {
//    Serial.println("Exists");
//  }
//
//}

void firebase_setup(void) {
  // Build the new game document
  StaticJsonBuffer<JSON_OBJECT_SIZE(2)> json_buffer;
  JsonObject& json_object = json_buffer.createObject();
  json_object["mode"] = "lobby";
  json_object["start"] = false;

  active_doc = Firebase.push("/games", json_object);
  Serial.print("active_doc = ");
  Serial.println(active_doc);
}

void show_qrcode(String str) {
  if (str.length() > 25) {
    Serial.println("[qrcode string too long]");
    return;
  }

  // Raw char array of string
  char buf[26];
  str.toCharArray(buf, sizeof(buf));

  QRCode qrcode;
  // version 2 qr code
  uint8_t qrcode_bytes[qrcode_getBufferSize(2)];
  //uint8_t qrcode_bytes[79];

  // Version 2, low ecc
  qrcode_initText(&qrcode, qrcode_bytes, 2, ECC_LOW, buf);

  // Clear the screen first!
  tft.fillScreen(TFT_WHITE);

  // Quiet zones: only draw between (20,20) and (220, 220)
  for (int y = 20; y < 220; y += 8) {
    for (int x = 20; x < 220; x += 8) {
      // Draw each module of the qr code...
      bool module = qrcode_getModule(&qrcode, (x - 20) >> 3, (y - 20) >> 3);

      tft.fillRect(x, y, 8, 8,
                   module ? TFT_BLACK : TFT_WHITE);
    }
  }

}

// compare_op < 0: return if ldo value < threshold
void wait_for_adc(int threshold, int compare_op) {
  while (1) {
    int adc_value = analogRead(LDR_ADC_PIN);

    if (compare_op < 0) {
      if (adc_value < threshold) {
        return;
      }
    } else {
      if (adc_value > threshold) {
        return;
      }
    }

    delay(500);
  }
}

void draw_logo() {

}

const char b64lookup[] = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+_";
char uint2b64(const char in) {
  return b64lookup[in & 63];
}

// Remember to leave the last position for null termination
void random_b64(char *buf, const int len) {
  int i;
  for (i = 0; i < len - 1; i++) {
    buf[i] = uint2b64((char)random(64));
  }
  buf[len - 1] = 0;
}
