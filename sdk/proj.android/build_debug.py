import os

# ndk build
command = 'ndk-build -B -j4 NDK_DEBUG=1'
os.system(command)

# ant build
command = 'ant clean debug -f ./build.xml -Dsdk.dir=C:/Dev/Libs/Android/android-sdk'
os.system(command)

# raname jar file
os.rename('./bin/classes.jar', './bin/perplesdk.jar')

os.system('sdkcopy.bat')

os.system('pause')
