This is the code for the project I did for my Master's thesis in 2017. A pattern matching algorithm extracts position and content data from a melody file, and this is information is recombined to construct new melodies. Read all about it in the Thesis_MFA_Final.pdf

To get this working
The application outputs 3 different ways:
	- direct injection into an Ableton Live set - this happens via UDP on port 7800. This can be changed in the RepetitionAnalysisGameFrame class, and in the max patch in the 'UDPReceiver' channel in the included Live set.
	- export of .musicxml files - 
	.musicxml files can be opened in a score editor (I used MuseScore, so cant comment on behaviour in others, although I did notice some difference between MuseScore and Sibelius in the interpretation of the position of text annotations). These .musicxml files represent the output but are by no means complete in terms of formatting. They were used as notated examples in the thesis and so any formatting required was done by hand.
	- export of .liveclip files - .liveclip files are project specific and of no particular casual interest. 

The save path for .musicxml and .liveclip files is set in the resources/repetition_analysis.properties file. Any absolute path in the properties file should be changed to one that is relevant to the computer running the application.

The application only sends the generated melody to Live. Any accompaniement required will have to be generated manually if it is not one of the options currentlyh in the project.

To generate more chord and melody material to use in the project requires this info to be converted into .liveclip files. This can be done using the CorpusCapturer project which may or may not have been uploaded by the time this is being read. Once generated, these files can be copied into the resource/melody files or resource/chord_progression files folder, after which it will be readily available in the relevant file browser in the application.
