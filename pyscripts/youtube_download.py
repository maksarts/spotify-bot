import youtube_dl
import argparse

parser = argparse.ArgumentParser(description="Arguments for download tracks",
                                 formatter_class=argparse.ArgumentDefaultsHelpFormatter)
parser.add_argument("-l", "--link", help="youtube url")
parser.add_argument("-p", "--path", help="download path")
parser.add_argument("-f", "--ffmpeg", help="ffmpeg location")
args = parser.parse_args()

ffmpeg_path = args.ffmpeg
path = args.path
yt_url = args.link

if not ffmpeg_path: ffmpeg_path = "../bin"
if not path: path = "../test_download/"
if not yt_url: yt_url = 'https://www.youtube.com/watch?v=1ocUkgZn5S0'

options = {
                # PERMANENT options
                'format': 'bestaudio',
                'ffmpeg_location': f'{ffmpeg_path}/ffmpeg.exe',
                'keepvideo': False,
                'outtmpl': f'{path}/%(title)s.%(ext)s',
                'postprocessors': [{
                    'key': 'FFmpegExtractAudio',
                    'preferredcodec': 'mp3',
                    'preferredquality': '320'
                }],

                #(OPTIONAL options)
                'noplaylist': True
            }

with youtube_dl.YoutubeDL(options) as mp3:
    mp3.download([yt_url])
    print("Download Completed!")