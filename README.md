# bingo90-stripes
Generator of bingo 90 stripes

REST API for generating bingo 90 strips

# Getting Started
Prerequisites:
* Maven
* Java 11

## Start the app
Clone project from github repo https://github.com/JussV/bingo90-stripes.git

Then in the root folder of the project execute:

 <code>mvn clean install</code>

# REST API
There is only one endpoint to generate bingo 90 strips.

The endpoint is <code>/api/bingostrips</code> which takes param <code>count</code> to generate the specified amount of bingo strips.

# ALGORITHM

The algorithm for generating bingo strips consists of several steps explained below (for more info please read code comments). 
Empty values in the strip are marked with 0s.

  * Initally a list of numbers from 1 to 90 is generated and copied in a list that store shuffled lists of numbers per column

  * Next the requirement for one number in ticket's column is satisifed 
(i.e. we distribute first 54 numbers in the bingo strip and we remove assigned numbers from the full list and column lists)

  * Shuffle the remaining numbers that are yet to be assigned
  * Next we are trying to distribute the remaining numbers in rows that have not reached the limit of 5 numbers per row
    * in case we cannot find such a row in the specific column where the number should be placed, we are fetching the first row from the bottom that has less than 5 items. This is the row (<code>rowIdForNumberToBeAssigned</code>) where a new number can be placed.
    * next we are searching for the first available row to swap values with. This row is searched from the bottom and satisfy: 
      * not to have a value in the desired column
      * has reached the max number of items
      * next we are searching for the column that has an item in the current row, but does not have a value for the same column in rowIdForNumberToBeAssigned. 
      * we are swapping the found value from the current row into <code>rowIdForNumberToBeAssigned</code>, and we are adding the new number to the current row.
  * we are sorting column tickets in ascending order
  
  This algorithm can be effectively used to generate large amounts of bingo strips at once, but the api request is limited to generate up to 100000 strips at once. 
  However, to test the performance of the algorithm the recommended way is to alter the test <code>generate10kBingoStripsInLessThan1s</code> with more than 10k strips.
