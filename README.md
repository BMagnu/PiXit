# PiXit
![Logo](https://raw.githubusercontent.com/SelfGamer/PiXit/master/res/net/bmagnu/pixit/client/icon.png)

## About
PiXit is a creative image-based game based on the popular board game DiXit.

Each round, one player picks a theme. Then, every player chooses one of their images that fits this theme. Finally, all players try to guess which image was played by the theme-setter.

For [detailed rules](#rules-and-gameplay) see here.

## Setup

Get the game:
1. Download the PiXit binaries from [here](https://github.com/SelfGamer/PiXit/releases/latest) (_every_ player needs the same version)
2. Extract them to a location where the game has write permission (i.e. not the Program Files directory)

Set up and start a server (only one player needs to do this, but once for every game):
1. Modify the [configuration file](#configuration-file) to your needs
2. Make sure that the port used by PiXit is opened (TCP 53415 by default)
    * Either enable UPnP in the router and in the game's config, then the game will manage everything by itself
    * Or manually enable the correct port forwarding in your router
3. Determine which [set of images](#image-sets) you want to play
4. Start the server by executing ```Start_Server.bat "<path-to-images>"```
5. Tell the other players your IP, as noted in the command prompt below "Global IP:"

Join a game:
1. Double click the ```Start_Client.bat``` file
2. Enter the IP of the Server into the corresponding field, followed by ```:<port>``` if you are not using the default port
3. Enter your Nickname into the corresponding field
4. If you expect to play this set of images multiple times, check the "Cache Images?" checkbox, as it will save the images on your HDD instead of downloading them again the second time
5. If your internet connection or the connection of your server is poor and you can download the used images in advance, click the "Pre-Cache Folder" Button and select the folder which contains the images that will be used to avoid having to download images during the game altogether
6. Click OK or press Enter

Rejoin a game:
* If you closed your game and want to rejoin a running game, just reopen the client, enter the same IP and the same username and click OK again

### Configuration File

In the ```config.json``` file, relevant settings for the game are stored. If you want to change some settings, modify the corresponding values desribed in the following:

Setting | Effect | Value Range | Default
--- | --- | --- | ---
numPlayers | The _exact_ number of players in your PiXit game | 3 - 7 | 4
imagesPerPlayer | The number of images in each players hand | 1 - 7 | 7
playFullRounds | If the game should end as soon as the remaining cards would not suffice for each player being the theme-setter once more (true), or if the game should run until all cards are gone (false) | true, false | true
maxRounds | The maximum number of rounds (one round means each player was theme-setter once) after which the game will end, even if cards are still left | 1 - 9999 | 3
allowEmptyTheme | If the theme-setter can submit an empty theme for a round (useful if all players are in a voice call and don't need the theme box) | true, false | true
pointsCorrectGuess | The points awarded to a player who correctly guessed the theme-setters card | 0 - 9999 | 3
pointsGoodCzar | The points awarded to the theme-setter if their card got guessed by some, but not by all players | 0 - 9999 | 2
pointsGotGuessed | The points awarded to any non-theme-setter player if their card got guessed | 0 - 9999 | 1
minimumCzarDelta | The minimum number of people who need to have guessed the theme-setters card (and in turn need to have guessed a different card) for the theme-setter to count as successful (values bigger than 1 are only useful for a large number of players) | 1 - 3 | 1
postRoundWait | The time (in milliseconds) that the game will wait after the correct answer has been revealed. High values allow for post-round discussion of the images, low values are good for quick games | 0 - 999999 | 5000
portServer | The port that the game runs on. Leave as is unless you know what you are doing | 1024 - 49151 | 43415
openPortUPnP | If the game server should try to automatically open the required port via UPnP | true, false | true

### Image Sets

One of the main benefits of a digital version of this game is that many different images can be used to play.

You can use either one of our prebuilt set of images (coming soon), or you can use your own collection of images.

To create your own sets, create a new directory for your set, and copy all images you want (all common formats are supported, including animated gifs) into it.
However, as the images are sent from the server to the client via internet during the game, the images should be (but don't have to be) below 2 MB. Larger images will work, but may take significant time to load for the other players. If you need large images or have a poor connection, use the pre-caching feature as described in [setup](#setup).

## Rules and Gameplay

In the game, there are four main windows.
In the top left, all players in this game and their respective points are shown. The current theme-setter is displayed in bold.
In the bottom left, the total amount of cards in your set and the amount of cards still available is shown.
In the bottom right, the current state of the game is shown.
In the top right, the currently relevant images are shown. If you need to enlarge an image, you can simply hover it with your cursor.

The rules of the game can be explained in phases:

**Phase 1: Selection of the rounds theme**\
In this phase, the images are shown are your hand. If you are the theme-setter, you will see a text box in which to enter this rounds theme and a button to submit it. If you are not the theme-setter, you simply need to wait.
As the theme-setter, you should choose a theme to which you have a corresponding image. However, it should neither be too generic so that other players may have more accurate images, nor too specific so that everyone immideately recognizes your image.

If you are the theme-setter, the state panel will read "You are the theme-setter! Set this round's theme!", otherwise it will read "Waiting for the theme-setter to set the round's theme!

**Phase 2: Selection of images**\
In this phase, every player should play an image they think fits that round's theme best by clicking on it.

In this phase, the state panel will read "Play a card that matches the round's theme!"

**Phase 3: Guessing the theme-setter's image**\
In this phase, every player who is not the theme-setter needs to guess which image was played by the theme-setter by clicking on it. A correct guess will yield points, a wrong guess will give points to whoever played that card. Guessing your own card will not yield any points.

To illustrate how points are gained: If you are not the theme-setter, you will get points for everyone who guesses your image, as well as for correctly guessing the theme-setter's image. As theme-setter, you only get points if some, but not all players guessed your image (so don't pick your theme too broad or too specific).

In this phase, for everyone but the theme-setter, the state panel will read "Guess which card was played by the theme-setter!", while the theme-setter sees "Wait for other players to guess your card!"

**Phase 4: End of round**\
Now, the correct image gets highlighted, and after a brief period of time, the players return to Phase 1 and see their hand again. The card they used this round was discarded and replaced with a new, random card.

