# messenger

It is a simple messenger web-service which is being actively developed at this time. The web-service allows a client to manipulate with such entities as chats,messages and users.

### Details

The service is developed following REST-API standards. The web-service application protocols are HTTP and WebSockets. 
The web-service is divided into two parts.
The first part is used for manipulating with the web-service's resources.
Access to the resources is performed using HTTP protocol.
The second one acts as a notification service that workds view web-socket protocol.
When a user performs an operation on a resource(adding a new message), all users are notificated via that notification service.

### API

documentation/users.html:documentation/chats.html:documentation/messages.html
