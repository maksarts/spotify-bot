from vkpymusic import TokenReceiver

print("Start auth in VK")

tokenReceiver = TokenReceiver("kaktusko2000@gmail.com", "SpotifyBot2024!")

if tokenReceiver.auth():
    tokenReceiver.get_token()
    tokenReceiver.save_to_config()

print("Finish auth in VK")