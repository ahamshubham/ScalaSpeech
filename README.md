# scala-speech

[![Join the chat at https://gitter.im/scala-speech/Lobby](https://badges.gitter.im/scala-speech/Lobby.svg)](https://gitter.im/scala-speech/Lobby?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)    
A speech recognition software based out of scala [The code is experimental and bound to change. Please use at your own risk.]

In order to run the code (Instructions for linux):    
1) Install sbt    
2) Clone this repository    
3) In terminal, run the following command while inside the scala-speech folder (This will start the scala interpreter):    
  ```
  path/to/scala-speech$ sbt console
  ```
4) Now, run the Master class with the following commands inside the scala interpreter:    
  ```
  scala> import main.scala.Master
  scala> Master.main(Array())
  ```
  
This is how the result should look:

![alt tag](https://github.com/ahamshubham/scala-speech/blob/master/docs/scala-speech.png)
