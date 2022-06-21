# scrambledFragments
My attempt at the scrambledFragments solution

# Challenge introduction:

### Background 

Imagine you have 5 copies of the same page of text. You value this text and have no hard or soft copies of it. Your two year old nephew visits and, while you are not looking, rips each page up into fragments and gleefully plays in the “snow” he has just created. 

You need at least one copy of that page of text back ASAP. As punishment to your niece, who should have been supervising your nephew at the time of the incident, you set her the painstaking task of keying in all the paper text fragments to a text file on your shiny MacBook Pro. Now the task is yours. Can you reassemble a soft copy of the original document? 

### The Challenge 

Write a program to reassemble a given set of text fragments into their original sequence. For this challenge your program should have a main method accepting one argument – the path to a well-formed UTF-8 encoded text file. Each line in the file represents a test case of the main functionality of your program: read it, process it and println to the console the corresponding defragmented output. 

Each line contains text fragments separated by a semicolon, ‘;’. You can assume that every fragment has length at least 2. 
