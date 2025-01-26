# CSEP Template Project

This repository contains the template for the CSE project. Please extend this README.md with sufficient instructions that will illustrate for your TA and the course staff how they can run your project.

To run the template project from the command line, you either need to have [Maven](https://maven.apache.org/install.html) installed on your local system (`mvn`) or you need to use the Maven wrapper (`mvnw`). You can then execute

	mvn clean install

to package and install the artifacts for the three subprojects. Afterwards, you can run ...

	cd server
	mvn spring-boot:run

to start the server or ...

	cd client
	mvn javafx:run

to run the client. Please note that the server needs to be running, before you can start the client.

Once this is working, you can try importing the project into your favorite IDE.
-----------------------------------------------------------------------------------------

Instructions:  

Go to server and run the Main class (you can find it in services).
The server start sequence might look a bit weird, but it is because we modified application properties. It will say at the end of the sequence if the server started successfully.
Now go to client and find the MainClient class. It should be below utils. Run this to start the app. 
A starting page will display the markdown rendering properties, but to actually use the app please add a note (or choose one from your list if you already have saved notes).  


Disclaimer!!!!  

If any changes you make dont appear automatically, please use the refresh button.
When you add a file and then switch to another note and then switch back, you have to press a key longer in order to be able to write again in that note.
In order for everything to work smoothly when you want to delete a note on multiple clients, you should delete on client1, refresh on client2 and select a different note on client2.
Disclaimer!!!!
Due to the title check for duplicates, the autosave works differently for titles in order to not check while the user is actively typing. Go to content box (focus away from title) to signal that you finished typing.  



Shortcuts:
UP form title will go to search.  

DOWN from title will go to content.  

LEFT from title will go to notes list.  

UP from content will go to title.  

LEFT from content will go to notes list.  

RIGHT from notes list will go to title.  

UP and DOWN navigate the notes title list  

CTRL D deletes a note.  

CTRL N adds a note  


Add scene  

CTRL A accepts and attempts to add a new note  

CTRL C cancels  

CTRL R resets the fields  


Search scene  

ENTER on the search bar confirms a serch  

UP and DOWN navigate the search results  

ENTER confirms a result
