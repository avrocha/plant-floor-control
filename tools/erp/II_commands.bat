

echo 'sending command1.xml'
nc -u -w 1 127.0.0.1 54321 < command1.xml

sleep 3

echo 'sending command2.xml'
nc -u -w 1 127.0.0.1 54321 < command2.xml 

sleep 47

echo 'sending command3.xml'
nc -u -w 1 127.0.0.1 54321 < command3.xml 

sleep 50

echo 'sending command4.xml'
nc -u -w 1 127.0.0.1 54321 < command4.xml 

sleep 150

echo 'sending command5.xml'
nc -u -w 1 127.0.0.1 54321 < command5.xml 

echo 'DONE!!'
