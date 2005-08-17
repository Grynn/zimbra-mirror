QUICK START:

0) Install activeperl
1) Run "ppm" from a command-prompt
2) install Statistics-Basic (just type that from inside ppm).  quit.
3) mkmail.pl <NUMBER>
4) put the following onto classpath:
   LiquidArchive\build\classes
   LiquidArchive\jars\commons-cli-1.0.jar
   LiquidArchive\jars\mail.jar
   LiquidArchive\jars\activation.jar
   LiquidArchive\jars\commons-codec-1.2.jar
5) java com.liquidsys.coco.tools.Journalize -i <path_to_mkmail>\out -o c:\opt\liquid\mqueue\new
6) Run the archiver



