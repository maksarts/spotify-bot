from vkpymusic import Service
import argparse

script_name = "vk_audio_script.py"

parser = argparse.ArgumentParser(description="Arguments for search tracks",
                                 formatter_class=argparse.ArgumentDefaultsHelpFormatter)
parser.add_argument("-q", "--query", help="search query")
parser.add_argument("-t", "--token", help="access token")
parser.add_argument("-a", "--user_agent", help="VK user_agent info")
args = parser.parse_args()

query = args.query
token = args.token
user_agent = args.user_agent

print(f"[{script_name}]: Start search in VK library with query={query}")

service = Service(token=token, user_agent=user_agent)
songs = service.search_songs_by_text(query, 1)
url = songs[0].url
print(url)

print(f"[{script_name}]: Finish search in VK library")