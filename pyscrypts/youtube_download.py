# TODO надо перезапускать пока не получится!!!!

import youtube_dl

ffmpeg_path = "bin"
path = "/test_download/"
yt_url = 'https://www.youtube.com/watch?v=1ocUkgZn5S0'

# TODO надо вынести в конфиг etc/youtube_del.conf
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

# TODO мб будет работать на сервере TODO youtube-dl -x --audio-format mp3 https://www.youtube.com/watch?v=m_PmLG7HqbQ


# youtube-dl -f 'bestvideo[ext=mp4]+bestaudio[ext=m4a]/best[ext=mp4]/best' 'https://www.youtube.com/watch?v=1ocUkgZn5S0'
# youtube-dl -f 'bestvideo,bestaudio[ext=mp3]' -o 'test_download/%(title)s.%(ext)s' 'https://www.youtube.com/watch?v=1ocUkgZn5S0'