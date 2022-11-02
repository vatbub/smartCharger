# Smart Charger
Smart charger protects your laptop battery by only charging it when it needs to be charged.

## Why?
Many people have a laptop which they use in a stationary manner.
In most cases, these laptops are then plugged in all the time as no one cares about charging a laptop battery when the laptop barely moves.
This is not healthy for the battery, though.
Smart charger monitors the battery level of your laptop and stops the charging process once the battery reaches 80%.
Smart charger then lets your laptop run on battery until the battery level reaches 25%, at which point it will start charging again.

## How it works
Smart charger requires you to own a smart plug.
All smart plugs will work, as long as they have integration into IFTTT.
Smart charger then sends events to IFTTT using the [Webhooks service](https://ifttt.com/maker_webhooks).
You therefore need to create applets on IFTTT which react to those events and tell your smart plug to switch itself on/off.

## Download
We are working on a better solution, but for now, you need to...

1. Meet the prerequisites mentioned under [Compilation prerequisites](#compilation-prerequisites).
2. Clone this repository.
3. Open a shell and run `mvn package`
You will find the compiled jar under `target/smartCharger-1.0-SNAPSHOT-jar-with-dependencies.jar`.

## Runtime prerequisites
- Java JRE 9 or later

## Setup
### IFTTT setup
1. Open your browser and go to [https://ifttt.com/](https://ifttt.com/)
2. Either log in or create an account.
3. Click on your profile picture in the upper right corner and then click on `Create`.
4. Click on `This` .
5. Search for `Webhooks` and then click on `Webhooks`.
6. Click on `Receive a web request`.
7. Under `Event Name` paste the following: `laptopStartCharging`
8. Click on `Create trigger`.
9. Click on `That`.
10. Search for the manufacturer of your smart plug, click it and follow the set-up process to switch the plug ON.
11. Disable `Receive notifications...` if you do not wish to receive notifications when Smart charger is doing something.
12. Click `Finish`.
13. Repeat steps 3 through 12, but with the event name being `laptopStopCharging` and the smart plug turning OFF instead.

### Smart Charger setup
After being done with the IFTTT setup, you may set Smart Charger up on your laptop:

1. Double-click the `jar`-file you downloaded earlier.
2. Click on `Log in to IFTTT`.
3. Log in using the same account you used before.
4. Most likely, you want Smart Charger to start when you start your computer. Therefore, tick the box next to `Start this app with Windows`.

### Using SmartCharger on multiple devices
By default, SmartCharger will send the events `laptopStartCharging` and `laptopStopCharging` to IFTTT.
If you use it on multiple devices, you will need to customize this, as IFTTT will otherwise not be able to distinguish the devices to control.
To do so, simply define different event names in the appropriate settings fields of SmartCharger and use the same event names in the set-up steps above.

## Charging modes
Smart Charger supports different charging modes:
- Optimized charging: Switches the charger on and off automatically in order to keep the battery level between 25% and 80%. Recommended for normal day-to-day work which is does not require high performance.
- Full charge: Switches the charger on all the time. Recommended if you need all the performance of your laptop (some laptops will slow down if they are on battery) or if you want your laptop to be charged to 100%.
- Stop charging: Will discharge your laptop completely.

You can switch modes by simply right-clicking the tray-icon or by opening the GUI and switching there.
 
## Compilation prerequisites
- Java 9 or newer
- Maven 3.8 or newer
