
************************Instructions for compiling and running the Simulation Project***************************************************************

The Zip file has a folder named Code files which has all the four .java files and .txt files in it.

1.Customer.java
2.Event.java
3.RunSimulation.java
4.SimulationControl.java
5.input.txt
6.output.txt



**************************************** Format of input.txt file ************************************************************************************
All the input parameters are written in the format of a string
For example:  1 0.1 0.2 0.5 30000 30  (Arrival rate  Mean service time 1/µP  Mean service time 1/µS  Mean service time 1/µAS  Total number of departures  The number of batches).

Note: Initial batch of 100 customers is hardcoded in the code as these 100 are not part of our batch model. These 100 are used to eliminate the effects of the initial
condition, after which batch model is executed.

*************************************************** Compiling and Running *****************************************************************************

I have mentioned two methods here

1. Complie and run the code using Eclipse software.
2. Compile and run the code using command prompt.


****************************************Compiling and running the program in Eclipse(platform for JAVA)************************************************

Create a project in eclipse with name IMS.
Add the above four.java files to that project.( These will be the four classes in that IMS project folder).

Place the input.txt file inside src folder of the project folder. Alternatively you can place it in your preferred location but would have to make some changes in the code while running.

SimulationControl.java has our main function.

********************************************** Changes to be made in SimulationControl.java***************************************************************

Go to SimulationControl.java.

Go to line 11, give the path of the location where you have placed the input.txt file
Go to line 12, specify the path, where you prefer your output.txt file to be created.

Save the changes.

Run the program. The simulation is taking some good 10 minutes. So please be patient.

In the Console, you can see when the simulation is done. Go and check your output.txt file to see the results.

Results can be seen in the output console in Eclipse also.

********************************************************** Compliling and running the program using command prompt ******************************************

Go to the folder where you placed your project. In that click on the src folder and open the command prompt( shift+right click).

SimulationControl.java is our main function file. So run that file using command prompt. Here you have to take care of the input.txt file location. Better keep the input.txt file in the same
folder as that of src.

[ Command line: Compile javac *.java. Then Run java SimulationControl]

After simulation is done, check output.txt for results.












