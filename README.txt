***************************************************************
FIRST FTC TEAM #8379 the Parity Bits
SEASON 2015-2016 
*Insert copyright message lk other software developers* ...Not for commecial use... *Insert Logo*
NOTE TO READERS: A valid attempt was made to make this document useful and humorous, read through as much as you can 
***************************************************************
# HELLO WORLD
> Welcome to this wonderful document that guides you through the trek of software development
> Please follow these guidelines if you do not wish to be eaten by other programmers
> As we are a group that functions like any other normal, social group of people, plz follow the same moral standards. Aka
  > Give proper credit to people who have worked on things
  > Be respectful of other ideas and keep an open mind, software is flexible and allows for different ways of implementation
  > Write READABLE code, add comments when appropriate
  > Moderate trolling is allowed, but don't go overboard
  > When in doubt, stay classy

# CREATING A CLASS
> Please commit using your personal accounts, not the team account
> For each function, comment in name or contributer and date of contribution
> Give a brief description of the class. Include:
  > Function of class
  > General framework, methods involved if applicable
> Add comments if appropriate
  > In general, each method should have comments specifying parameters and return value, as well as method use 

# EDITING A CLASS
> Change the date of last edit
> Before editing, consult contibutor

# USING GITHUB
> In general, the VCS in Android Studio is a useful tool
> Always pull before making any edits
> Give descriptive commit messages
> Read through merge messages before making decisions, don't overwrite other people's hardwork
> When pushing a new project, make sure git doesn't eat up some files and make it not work for other people

# TROUBLESHOOTING
  # Code
  > Project does not load correctly
    > Check that you have all of the files
    > Restart Android studio
  > Unrecognized symbols: check import statement. 
    > If everything is imported correctly but the corresponding import statement is greyed out
    > Backup changes. Go to File - Invalidate Cache and Restart
    
  # CONNECTION
  > Power Cycling: first thing to try when there are issues finding devices
    > Disconnect Robot Controller from robot
    > Close the app
    > Restart power module
    > Plug in phone to open the app naturally
    > If still doesn't work, load configuration file and Restart Robot
  > Battery Level: Robot Controller still not recognizing devices after power cycling
    > Accompanied by symptoms including, but not limited to: robot hysteria, robot fatigue, and robot nausea
    > Involves difficut procedure of taking out battery and putting a fresh one in
    > Fresh batteries usually grow in dry patches of sand under direct sunlight
    > Please avoid plugging potatoes and poison ivy into robot, they DO NOT provide sufficient energy
  > Hardware: Trying to scapegoat hardware *cough* I meant, when above steps fail
    > Close the robot controller app and plug in to robot
    > If the app does not pop up, congrats, it is hardware's problem and just switch up the cables or related devices
    > Make sure you are using the one with a tumor (ferrite choke) that is provided by our dear sponsor Qualcomm
    > If the app pops up, see next section
    > If following the next section doesn't work, it may still be the cable's problem, switch it up just to check
  > Configuration: 
    > Stop being lazy and reconfigure everything into a new file
  > Potentially contact Josh or Tom for help

  # SCREEN FLASHING
  > If you didn't do anything crazy to our robot and our devices such as scaring it at midnight or forget to feed it when it needs to, it might be a valuable incident for Josh to investigate on


***************************************************************
SECTION II
# When the project doesn't load properly in our dear AS
  > No matter what happens, stay classy, and blame Android
  > Google is your best friend
  > Might want to check the following
    > Under SDK Manager, all of the files under folder "Android 4.4.2" or "API 19" are downloaded
    > The project is loaded from the gradle.build file under FtcRobotController folder, not the one under ftc_app-master w/e
    > WILL BE CONTINUED
