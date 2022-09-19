import os
import numpy as np
import pandas as pd
import random
import matplotlib.pyplot as plt

def runTest():
    programs = ["MedianFilterSerial","MedianFilterParallel","MeanFilterSerial","MeanFilterParallel"]
    filtSizes = [3,5,9,11,13]
    images = ["image1.jpeg","image2.jpg","image4.png","image5.png"]
    
    for program in programs:
        
        programRunTimes = [] #list containing a lists (runTimes[])
        for size in filtSizes:
            minRunTimes = [] #run time for each img for a particular filtSize 'java -cp bin 
            
            for img in images:
                #5 x
                runTimes = []
                for i in range(5):
                    outImgName = img+"_"+program+"_filt_"+str(size)
                    command = 'java -cp bin '+program+' '+img+' '+outImgName+' '+str(size)+' > temp.txt'
                    os.system(command)

                    fil = open("./temp.txt",'r')
                    lines = fil.readlines()
                    splitLine = lines[0].split()
                    time = float(splitLine[4])
                    runTimes.append(time)
                minRunTimes.append(min(runTimes))
                
                 
            programRunTimes.append(minRunTimes)
            

        my_xticks = images
        x = [1,2,3,4]
        plt.xticks(x,my_xticks)
        i = 0
        for y in programRunTimes:
            plt.plot(x,y,label ='filtSize Of: '+str(filtSizes[i]))
            plt.legend()

            i+=1

        plt.title(program+' speedup graph')
        plt.xlabel('Image sizes')
        plt.ylabel('Run time in secs')
        
        plt.savefig('./graphs/'+program+'graph.png')
        plt.close()
    

if __name__ == "__main__":
    runTest()