This is a small Telegram/IRC bot that listens to IRC channels and notifies the user on Telegram 
whenever his name is mentioned. It is also possible to chat in an IRC using Telegram, or to listen
to IRC channels. 

Every telegram message from the 'owner' that starts with a comma is interpreted as a command. The
following commands are available:

,join [channel]       joins channel
,part [channel]       leaves channel 

,listen               turns on listening mode (in listening mode, all irc messages are redirected to TG)
,nolisten             turns off listening mode (only messages containing IRC_OWNER_NICKS are redirected)

,channel [channel]    Sets the channel the bot is currently writing to to channel
,[message]            Sends a message to the currently set channel

Make sure all constants are set to proper values.
Also, this bot logs your irc messages. Logs are stored in irclogs/, make sure that directory exists (or
turn off logging by commenting the corresponding lines in the source).

This bot requires PyTg by luckydonald. It can be found on github: 
https://github.com/luckydonald/pytg
Make sure PyTg and all its dependencies are installed. I recommend to run telegram-cli at least
once before using this script, and register a phone number. Otherwise, things might break.

Author: Alinea