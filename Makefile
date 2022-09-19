# Makefile for Assignment_2
# Thabang Thubane
# 17 April 2021

JAVAC=/usr/bin/javac
.SUFFIXES= .java .class
SRCDIR=src
BINDIR=bin
IMGS=images
FILT=filtered

$(BINDIR)/%.class:$(SRCDIR)/%.java
	$(JAVAC) -d $(BINDIR)/ -cp $(BINDIR) $<

CLASSES=MedianFilterSerial.class MeanFilterSerial.class MedianFilterParallel.class \
		 MeanFilterParallel.class

CLASS_FILES=$(CLASSES:%.class=$(BINDIR)/%.class)

default: $(CLASS_FILES)

clean:
	rm $(BINDIR)/*.class $(FILT)/* filtered/* graphs/*.png *.txt


medSer:
	java -cp $(BINDIR) MedianFilterSerial $(inImg) $(outImg) $(size)

meanSer:
	java -cp $(BINDIR) MeanFilterSerial $(inImg) $(outImg) $(size)
medPar:
	java -cp $(BINDIR) MedianFilterParallel $(inImg) $(outImg) $(size)

meanPar:
	java -cp $(BINDIR) MeanFilterParallel $(inImg) $(outImg) $(size)

test:
	python3 ./testscript/test.py
