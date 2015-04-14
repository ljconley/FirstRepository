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
 * If different databases are used "LoadingValue" on line 59 must be changed for loading bar to function
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
 * Use parameters to get filename
 * look into external libraries
*/

//Import statements
import scala.io.Source
import java.io._

// ListPediatriciansScalaV1
object ListPediatriciansScalaV1
{ 
  //values
  val filename = "npidata_20050523-20150308.csv"
  val LoadingValue = 84062
  //variables
  var zip =""
  var state =""
  var FinalZips = scala.collection.mutable.ListBuffer.empty[String]
  var AllZips = scala.collection.mutable.ListBuffer.empty[String]  
  var v = 1   
  /* Write pediatricians array sorted by zip code 
   * titled FinalZips to text file additional with additional file header
   */
  def main(args: Array[String]) 
  {
   /* if line contains a taxonomy code that corresponds to pediatrician, 
   *  a state code that is two characters long 
   *  and a zip code that is a number of at least a 5 digits in the business or mailing address
   *  then add the information to an array. Also create separate array containing zip codes and
   *  the number of times they appeared in the first array
   */
    for {line: String <- Source.fromFile(filename).getLines() 
      if 4<=line.split(",")(47).replace("\"", "").length 
      if line.split(",")(47).replace("\"", "").substring(0, 4) == "2080"
      if line.split(",")(23).replace("\"", "").length == 2
      if line.split(",")(24).replace("\"", "").matches("\\d+")  
      if line.split(",")(24).length() > 5 
      }
    {
      //variables
      var Zip = line.split(",")(24).replace("\"", "")
      var state= line.split(",")(23).replace("\"", "")
      var TaxonomyCode = line.split(",")(47).replace("\"", "")
      var shortZip = Zip.substring(0, 5)
      var TotalZips = AllZips.count(_ == shortZip+"|?|"+state)
      
      AllZips+=shortZip+"|?|"+state
      FinalZips+=shortZip++"|"+(TotalZips)+"|"+state+"\n"
      FinalZips-=shortZip++"|"+(TotalZips-1)+"|"+state+"\n"
      v+=1
      println((v * 100 / LoadingValue)+"%"+"    ")
      //print statement below is for testing purposes
      //println(v +" of "+LoadingValue+"   "+(v * 100 / LoadingValue)+"%"+"    "
      //+shortZip+"|"+TotalZips+"|"+state+"    taxonomy code:"+TaxonomyCode+"    zip:"+Zip)
      }
    println("-----------end file--------------")
    val pw = new PrintWriter(new File("PediatriciansByZip.txt" ))
    pw.write("Zip Code|Count|State Code"+"\n")
    FinalZips.sorted.foreach{pw.write}
    pw.close
    }
  }
