# CS 446: Group 5

Jukebox is a “silent disco” app with a well-organized and efficient system design. We fully implemented the UI with multiple screen activities for both guests and hosts, creating a seamless experience of joining or creating a room to search, add and manage songs. 

After careful consideration of common database services, we ultimately chose to use Firebase database to keep track of the room states such as guests, song queues and song upvotes. 

Because ads are unpredictable and we wanted to stream copyrighted songs, each user must have a Spotify premium account. To connect their Spotify account, the user is able to login through Spotify authorization directly, after which their authorization token will be securely stored in our database, allowing us to control songs on their behalf. With the token, the app can skip to next or previous songs as well as perform other Spotify-related API calls.

The song queue is divided into four data structures: Approved, Pending, Denied, and Previous queues. These data structures are stored in our Firebase database, and get rendered on the client-end by the order that each song was added to each queue. 

The app uses the SongControl class which invokes the Spotify API to control songs such as playing next song, playing previous song, pause, play and change song timestamp during a track. The current timestamp is then stored in the Firebase database, ensuring synchronization among users and helping maintain the correct playback position displaying across host and guest devices. We face challenges syncing the song when the guests do not have a Spotify premium account, or if the guest tamper with the playing media by changing the songs on the Spotify app.

We also completed the “Open in Spotify” feature to allow users to open songs that they like and have more flexibility with the song in the Spotify app such as adding it to their own playlist. 

The host settings page allows hosts to change their display name, toggle on/off the option to rate limit guest song upvoting and song suggesting, select the exact limit to limit either upvotes or suggestions, and toggle on/off to auto remove a denied song from the queue. All of these changes are immediately reflected to users and in the database.


### Team Members:
##### Arsh Ahluwalia
##### Darren Lao
##### Braden Morley
##### Grace Nguyen
##### Maximus Niu
##### Kenneth Yang

## Home Screen
<img src="https://github.com/arshahluwalia/CS446/assets/91099321/615ea811-f91e-4903-a9ba-de8fae86fbc9" width="200" />

## Creating Room
<img src="https://github.com/arshahluwalia/CS446/assets/91099321/6d788438-1aeb-4a76-a123-a6c1c3ba0f3c" width="200" />
<img src="https://github.com/arshahluwalia/CS446/assets/91099321/8e56a3af-7d93-4afe-a0c6-435cb6f4edd8" width="200" />

## Host Song Queue
<img src="https://github.com/arshahluwalia/CS446/assets/91099321/343004a1-5e02-4766-863e-7767f35a92c4" width="200" />
<img src="https://github.com/arshahluwalia/CS446/assets/91099321/6dd3452f-738d-42fa-b6a9-855cf5ef57c8" width="200" />
<img src="https://github.com/arshahluwalia/CS446/assets/91099321/724e1b15-65d2-4842-8668-77f26ffdf80c" width="200" />

## Searching
<img src="https://github.com/arshahluwalia/CS446/assets/91099321/8ae72a30-347f-49c4-827e-98d8b6e114c2" width="200" />

## Guest Song Queue
<img src="https://github.com/arshahluwalia/CS446/assets/91099321/d3061c62-7f4a-4dd6-a7ff-f753d47cb743" width="200" />

## Settings
<img src="https://github.com/arshahluwalia/CS446/assets/91099321/b6d47df2-f037-42d8-b9a4-3270d8501576" width="200" />
