#Copyright (C) 2015 Aline Abler
#
#This program is free software; you can redistribute it and/or
#modify it under the terms of the GNU General Public License
#as published by the Free Software Foundation; either version 2
#of the License, or (at your option) any later version.
#
#This program is distributed in the hope that it will be useful,
#but WITHOUT ANY WARRANTY; without even the implied warranty of
#MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
#GNU General Public License for more details.
#
#You should have received a copy of the GNU General Public License
#along with this program; if not, write to the Free Software
#Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.

import sys
import socket
import string
from pytg import Telegram
from pytg.utils import coroutine
import threading
from time import strftime, gmtime


IRC_HOST='irc.snoonet.org' 
IRC_PORT=6667                       
IRC_NICK='telircbot'                # The bot's nickname
IRC_IDENT='telircbot'               # The bot's identity
IRC_REALNAME='telircbot'            # The bot's 'real name'
IRC_OWNER_NICKS=['MyNick', 'MyOtherNick']    # The bot's owner's IRC nick(s)

IRC_HOMECHANNEL='#linuxmasterrace'  # The bot's default channel it will autoconnect to
IRC_NICKSERVPW='pazzw0rd'           # The bot's nickserv identification password. To disable identification, comment the corresponding lines in the source code.

TELEGRAM_OWNER="My_Name"            # The owner's telegram name, format Firstname_Lastname
TELEGRAM_OWNER_PHONE="41710123456"  # The owner's telegram phone number, including country code, but without leading 00 or +
TELEGRAM_DIR="/usr/bin/telegram-cli"    # The directory where telegram-cli is installed (this is default)
TELEGRAM_PUBKEY="/etc/telegram-cli/server.pub" 

class IrssiListener(threading.Thread):

    # IRC server socket
    s = socket.socket()
    listen = False
    listenlock = threading.Lock()
    
    def setlisten(self, l):
        self.listenlock.acquire()
        self.listen = l
        self.listenlock.release()

    def getlisten(self):
        self.listenlock.acquire()
        returnval = self.listen
        self.listenlock.release()
        return returnval

    # interpret a PRIVMSG message and act accordingly
    def parsemsg(self, msg):
        complete=msg[1:].split(':', 1)

        info=complete[0].split(' ')
        msgpart=complete[1]
        sender=info[0].split('!')
        channel=info[2]

        # Log all of the everything
        self.log(sender[0], channel, msgpart)

        if (self.nickIsMentioned(msgpart) or self.getlisten() ):
            send_message(channel+':\n<'+sender[0]+'> '+msgpart)
    # end parsemsg


    def nickIsMentioned(self, message):
        for nick in IRC_OWNER_NICKS:
            if message.casefold().find(nick.casefold()) != -1:
                return True
        return False
    # end nickIsMentioned


    
    # Write message to specific channel's log
    def log(self, sender, channel, message):
        time = strftime("%y-%m-%d %H:%M", gmtime())
        entry = time+' < '+sender+' > '+message+'\n'

        log = open('irclogs/'+channel, 'a')
        log.write(entry)
        log.close
    # end log

    def logevent(self, channel, message):
        time = strftime("%y-%m-%d %H:%M", gmtime())
        entry = "*** "+message  

        if self.getlisten():
            send_message(entry)

        entry = time+' '+entry+'\n'
        log = open('irclogs/'+channel, 'a')
        log.write(entry)
        log.close
    # end logevent

    def send_message(self, channel, message):
        if channel[0] != '#':
            channel = '#'+channel

        self.s.send(bytes('PRIVMSG '+channel+' :'+message+'\n', 'UTF-8'))
        self.log(IRC_NICK, channel, message)
    #end send_message

    def join(self, channel):
        if channel[0] != '#':
            channel = '#'+channel

        self.s.send(bytes('JOIN '+channel+'\n', 'UTF-8'))
        self.logevent(channel, 'joined channel '+channel)
    # end join
        
    def part(self, channel):
        if channel[0] != '#':
            channel = '#'+channel

        self.s.send(bytes('PART '+channel+'\n', 'UTF-8'))
        self.logevent(channel, 'left channel '+channel)
    # end part



    # connect and start listening
    def run(self): 

        # connecting to the server

        self.s.connect((IRC_HOST, IRC_PORT))
        print('connected')

        self.s.send(bytes('NICK '+IRC_NICK+'\n', 'UTF-8'))
        print('nick sent')

        self.s.send(bytes('USER '+IRC_IDENT+' '+IRC_IDENT+' '+IRC_IDENT+' :'+IRC_REALNAME+'\n', 'UTF-8'))
        print('identified')
    # end run



                    
        # Listener main loop
        while True:
            line=self.s.recv(500) # recieve server messages
            line = str(line, 'UTF-8')

            # remove trailing \r\n
            line = line.rstrip()

            lines = line.split('\n')

            for item in lines:
                if (item != ''):
                    print(item)


                if item.find("Welcome to the Snoonet Chat Network") != -1 :
                    self.join(IRC_HOMECHANNEL)
                    print('JOIN SENT')
                    # Identify with NickServ
                    self.s.send(bytes('PRIVMSG NickServ :IDENTIFY '+IRC_NICKSERVPW+'\n', 'UTF-8'))


                # Send every PRIVMSG to message parser
                if (item.find("PRIVMSG") != -1 ):
                    self.parsemsg(item)

                elif (item.find("JOIN") != -1 ):
                    nick = item.split('!', 1)[0]
                    channel = item.split('#')[1]
                    self.logevent(channel, nick+' has joined '+channel)

                elif (item.find("PART") != -1 ):
                    nick = item.split('!', 1)[0]
                    channel = item.split('#')[1]
                    self.logevent(channel, nick+' has left '+channel)

                elif (item.find("QUIT") != -1 ):
                    nick = item.split('!', 1)[0]
                    quitmsg = item[1:].split(':', 1)[1]
                    self.logevent(sendchannel, nick+' has quit ('+quitmsg+')')
                    

                # Return PINGs
                splitline = item.split()
    
                if (splitline[0] == 'PING'):
                    self.s.send(bytes('PONG '+splitline[1]+'\n', 'UTF-8'))
                    print('sent pong')

        # end main loop

#end IrssiListener

def send_message(message):
    sender.send_msg(TELEGRAM_OWNER, message)   


listener = IrssiListener()
listener.start()

# Current default channel that telegram will send to:
sendchannel = IRC_HOMECHANNEL

# Get the Telegram instance
tg = Telegram(
    telegram=TELEGRAM_DIR, 
    pubkey_file=TELEGRAM_PUBKEY)

receiver = tg.receiver
sender = tg.sender

def setchannel(channel):
    global sendchannel

    if channel[0] != '#':
        channel = '#'+channel
    sendchannel = channel
    send_message('Set channel to '+sendchannel)
#end setchannel

def parse_command(command):
    print('Parsing command '+command)

    splitcmd = command.split(' ', 1)
    commandword = splitcmd[0].casefold()
    if len(splitcmd) > 1:
        args = splitcmd[1]

    print('commandword: '+commandword)
    
    if commandword == 'listen':
        listener.setlisten(True)

    elif commandword == 'nolisten':
        listener.setlisten(False)

    elif commandword == 'part':
        listener.part(args)

    elif commandword == 'join':
        listener.join(args)

    elif commandword == 'channel':
        print('channel command')
        setchannel(args)

    # default: command is a message
    else:
        listener.send_message(sendchannel, command)
#end parse_command
    



# Set up the callback function for received messages

@coroutine
def main_loop():
    while True:
        msg = (yield)

        if (msg.event == 'message'):
            if (msg.sender.phone == TELEGRAM_OWNER_PHONE):
                text = msg.text
                if text[0] == ',':
                    parse_command(text[1:].strip())
                

# Start receiver and set its message callback
receiver.start()
receiver.message(main_loop())
receiver.stop
