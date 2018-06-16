MainMiner
Program Arguments: [ port RMI, boolean to indicate if is the leader, id ]

Miner 1
Program Arguments: 8801 true 1
VM Arguments: -Djava.net.preferIPv4Stack=true

Miner2
Program Arguments: 8802 false 2
VM Arguments: -Djava.net.preferIPv4Stack=true

Miner3
Program Arguments: 8803 false 3
VM Arguments: -Djava.net.preferIPv4Stack=true

MainClient
Program Arguments: [ port RMI, first port RMI miners, last port RMI miners ]

Client1
Program Arguments: 2001 8801 8803
VM Arguments: -Djava.net.preferIPv4Stack=true

Client2
Program Arguments: 2002 8801 8803
VM Arguments: -Djava.net.preferIPv4Stack=true

MainTimeout
Program Arguments: [ first port RMI miners, last port RMI miners ]
Program Arguments: 8801 8803
VM Arguments: -Djava.net.preferIPv4Stack=true
