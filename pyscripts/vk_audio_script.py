from vkpymusic import Service
import argparse
import vk_audio_auth

parser = argparse.ArgumentParser(description="Arguments for search tracks",
                                 formatter_class=argparse.ArgumentDefaultsHelpFormatter)
parser.add_argument("-q", "--query", help="search query")
args = parser.parse_args()

query = args.query

print(f"Start search in VK library with query={query}")

service = Service.parse_config()
songs = service.search_songs_by_text(query, 1)
url = songs[0].url
print(url)

print("Finish search in VK library")