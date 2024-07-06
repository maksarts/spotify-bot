from vkpymusic import TokenReceiver
import argparse

script_name = "vk_audio_auth.py"

parser = argparse.ArgumentParser(description="Arguments for auth in VK",
                                 formatter_class=argparse.ArgumentDefaultsHelpFormatter)
parser.add_argument("-l", "--login", help="email login")
parser.add_argument("-p", "--password", help="password")
args = parser.parse_args()

login = args.login
password = args.password

print(f"[{script_name}]: Start auth in VK")

tokenReceiver = TokenReceiver(login, password)

if tokenReceiver.auth():
    tokenReceiver.get_token()
    tokenReceiver.save_to_config()
    print("{\"token\":\"" + tokenReceiver.get_token() + "\",\"userAgent\":\"" + tokenReceiver.client.user_agent + "\"" + "}")
    print(f"[{script_name}]: Finish auth in VK")
else:
    print(f"[{script_name}]: Cannot auth in VK, invalid credentials: tokenReceiver.get_token()={tokenReceiver.get_token()}")