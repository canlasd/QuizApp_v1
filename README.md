# QuizApp_v1
Coding Project

App Highlights

a)   Data from the API provided is downloaded using the DownloadData asynchronous task and returns the complete string.
b)  String is parsed on the Main Activity and is saved into a SQLITE database.
c) When the user clicks the CLICK TO START QUIZ button, the 1st row of data from the database is retrieved.  
The countdown timer also starts.
d)  The row data from the db is passed to an alert dialog with radio buttons.  The chosen answer is passed back 
to the main activity to be tabulated.
e)  The following row is then retrieved after the user chooses from the alert dialog list.
f)  Another alert dialog displays the results once the user is finished with the questions.  
If the user runs out of time (60 secs), the app will terminate and a toast message will pop up indicating the 
number of questions answered correctly.

Problems Encountered:
a) SQlite database is inserting one set of data into multiple rows.  This was fixed by removing extra Database 
helper objects .  Only one Database helper object is used during the insertData method.
b) The app goes back to the first question when the screen orientation changes.  Also, the countdown timer disappears. 
I added the onSaveInstanceState and theonRestoreInstanceState to retain the app's current position / state.  
I also restarted the timer and used the saved time in milliseconds to continue the timer.

c) The countdown timer does not stop when the user clicks the back button.  Added setonKeyListener to the 
AlertDialogButton class to detect the back button being pressed.  This cancels the timer and exits the application.


