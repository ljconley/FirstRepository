/*---------------------------------------------------------------------------------
 * Assignment #1.5
 * 
 * The next assignment is #1.5 and it is to redo assignment #1 but with using Python as the programming language.
 * As you come up to speed with Python make sure you become aware of the many libraries and feel free to use as many as you can on the task.
 * This assignment is to be done by you working in teams of two or more.
 * 
 * Programming assignment #1 for reference:
 * 
 * Given the data file found here -> http://nppes.viva-it.com/NPI_Files.html
 * 
 * Create a C++ program to produce a count of the number of Pediatricians per zip code.
 * 
 * The output should look like the following:
 * 
 * Zip Code|Count|State Code
 * 73034|5|OK
 * 29201|33|SC
 * 
 *     .
 *     .
 *     .
 */

/* This program is only meant to function with the databases given in the assignment, 
 * If different databases are used "LoadingValue" on line 48 must be changed for loading bar to function
 * Output text file should still be created
 * 
 * This program has the following features not in the assignment description:
 * Output is organized by zip code
 * Records for other countries included in the database have been omitted
 * A percentage complete message was added
 * 
 * Author: Leo Conley
 */

/* To Do: 
 * Improve readability
 * Better Comments
 * Use case statements instead of if statements
 * Use parameters to get filename
*/

//Import statements
import scala.io.Source
import java.io._

// ListPediatriciansScalaV1
object ListPediatriciansScalaV1
{ 
  /* main
   * Write pediatricians sorted by zip code array 
   * titled FinalZips to text file additional with additional file header
   */
  def main(args: Array[String]) 
  {
    //values
    val filename = "npidata_20050523-20150308.csv"
    val LoadingValue = 84062
    //variables
    var zip =""
    var state =""
    var AllZips = scala.collection.mutable.ListBuffer.empty[String]
    var FinalZips = scala.collection.mutable.ListBuffer.empty[String]
    var v = 1
     
      // for each line in file
      for (line <- Source.fromFile(filename).getLines()) 
      {
        
        val CurrentLine = line.split(",")
        
        /* if line contains a taxonomy code that corresponds to pediatrician, 
         * a state code that is two characters long 
         * and a zip code that is a number of at least a 5 digits in the business or mailing address
         * then add the information to an array. Also create separate array containing zip codes and
         * the number of times they appeared in the first array.
         * 
         * Note: Need to rewrite this if statement as a case statement
        */
        if (
            4<=CurrentLine(47).replace("\"", "").length
            &&CurrentLine(47).replace("\"", "").substring(0, 4) == "2080"
            &&CurrentLine(23).replace("\"", "").length == 2
            &&CurrentLine(24).replace("\"", "").matches("\\d+")
            &&CurrentLine(24).length() > 5 
            )
        {
          zip =(CurrentLine(24).replace("\"", "").substring(0, 5))
          state =(CurrentLine(23).replace("\"", ""))
          AllZips+=(zip+"|?|"+state)
          FinalZips+=(zip+"|"+(AllZips.count(_ == zip+"|?|"+state))+"|"+state+"\n")
          FinalZips-=(zip+"|"+((AllZips.count(_ == zip+"|?|"+state))-1)+"|"+state+"\n")
          v+=1
          println(v * 100 / LoadingValue+"%")
          //print statement below is for testing purposes
          /*
          println("    "+v +" of "+LoadingValue+
              "    "+v * 100 / LoadingValue+"%"+"    "+zip+"|"+(AllZips.count(_ == zip+"|?|"+state))+"|"+state
              +"    taxonomy code:"+CurrentLine(47)+"    zip:"+CurrentLine(24))
          */
        }
      }
    println("-----------end file--------------")
    val pw = new PrintWriter(new File("PediatriciansByZip.txt" ))
    pw.write("Zip Code|Count|State Code"+"\n")
    FinalZips.sorted.foreach{pw.write}
    pw.close
  }
}
