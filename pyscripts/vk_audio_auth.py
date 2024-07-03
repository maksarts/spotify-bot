from vkpymusic import TokenReceiver

script_name = "vk_audio_auth.py"

print(f"[{script_name}]: Start auth in VK")

tokenReceiver = TokenReceiver("kaktusko2000@gmail.com", "SpotifyBot2024!")

if tokenReceiver.auth():
    tokenReceiver.get_token()
    tokenReceiver.save_to_config()

# print("client_id=" + tokenReceiver.client.client_id)
# print("client_secret=" + tokenReceiver.client.client_secret)
print("{\"token\":\"" + tokenReceiver.get_token() + "\",\"userAgent\":\"" + tokenReceiver.client.user_agent + "\"" + "}")

print(f"[{script_name}]: Finish auth in VK")