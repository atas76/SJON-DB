SJON-DB
=======

A prototype of a NoSQL database based on a JSON like notation.

I had several motivations for starting a project like that.

First of all, I needed an interface/framework for reading and writing data for my projects in flat files. CSV format would be too simplistic as I wanted to add semantics to the data. Using the Java classes for reading and writing the same format would be a pain.

Since, in parallel, I am reviewing the NoSQL paradigm, it would be good to start a similar project so as to appreciate the features of the various databases and follow along from a practical perspective.

In this framework, you will see various concepts mixed: flat files, relational terminology (if you think 'table' is relational terminology which is not), and NoSQL. This is a work in progress adjusting it as I see fit for my projects' and hopefully more general needs. I will use semantic versioning and full documentation will come along with each release. In other words, this project will be treated as a first-class citizen and not just a utility like its predecessor you see in the SJON repository.

In this first release, only Unicode text is supported for the data, but I already wrote some prototypes for supporting binary data as well. It would be great if this framework provided a standard for reading and writing binary data. And why stop there: XML, relational, etc. But first we should start simple. 
