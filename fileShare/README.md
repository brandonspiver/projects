## Peer to peer file sharing

Allows for file sharing and messaging between multiple clients connected to the same server

Program done with java, and GUI done with JavaSwing

Before executing, ensure a files directory is in the same directory as client.java

### Execution

1. To run the server(in terminal):

   `$ java server.java <Port>`
   
   Port: Choose an available port to run ser on

2. To run Client (in new terminal, for each client):

   `$ java client.java <Server Address> <Server Port> <Client Port>`
   
   Server Address: InetAddress ser is running on.
   
   Server Port: Port server is running on.
   
   Client Port: Choose a port to run client on for file transfer.
