# spotify-bot
## Telegram bot for easy sharing songs from Spotify in telegram chats
*This bot was developed for personal purposes first of all 
and there is a reason for that:
integration with VK API carries like a real person and it leads to extreamly small
throughput so using this bot in public to sending mp3 files not such good idea unfortunatelly.
On the other hand, sharing of just Spotify links works good and easy scalable in production.
To sum up, you can simply try this bot (chat me in telegram @hehehelilili and I let you know how), but
for regular using I advice install it on personal machine and use with your personal Spotify and VK accounts
(all properties you need to configure located in ```application.yaml``` and can be configured using 
```.env``` file on your machine, whole other code needs no changes)*
### How to use:
#### 1. Basic case
- write '*@Botname  artist songname* in Telegram chat
- pick up one and send :)
#### 2. Only Spotify links
- write *@Botname ```/link```  artist songname* in Telegram chat
- pick up one and send :)


### *Workflow*
#### Done:
 - Interaction with Telegram API
 - Interaction with Spotify API
 - Sending search requests from telegram inline mode
 - Sending Spotify songs links in telegram chats 
 - Sending mp3 files with links
 - Dockerfile
#### // TODO:
 - Optimizing
 - Caching with Redis
