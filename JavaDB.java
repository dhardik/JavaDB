import java.io.FileNotFoundException; 
import java.io.FileWriter; 
import java.io.FileReader;
import java.io.IOException;
import java.io.File;
import java.util.Scanner;
import java.util.Arrays; 

class QueryProcessor {
  private String query;
  private File target;
  private int display_errm = 1;

  public QueryProcessor(String str) {
    setQuery(str);
  }

  public void setQuery(String str) {
    this.query = str;
    lTrim();
    rTrim();
    target = new File("/home/hardik/Desktop/JavaDB/tables");
    display_errm = 1;
    if(identifyCommand().equalsIgnoreCase("create")) {
      if(checkForCreate() == true) {
        String[] words = this.query.split(" ");
        String table_name = words[2];
        String[] col_name = words[4].split(",");
        createTable(table_name,col_name);
      }
      else {
        errorMessage("Invalid 'create' syantax !\nsyntax : create table 'table_name' (column1,column2...)");
      }
    }
    else if(identifyCommand().equalsIgnoreCase("insert")) {
      if(checkForInsert() == true) {
        String table_name = this.query.split(" ",4)[2];
        int start = this.query.indexOf("(")+2;
        int end = this.query.indexOf(")")-1;
        String[] columns = this.query.substring(start,end).split(",");
        start = this.query.indexOf("(",end)+2;
        end = this.query.indexOf(")",start)-1;
        String[] values = this.query.substring(start,end).split(",");
        insertInto(table_name,columns,values);
      }
      else if(display_errm == 1){
        errorMessage("Invalid 'insert' syantax !\nsyntax : insert into 'table_name' (column1,column2...) values (value1,value2...)");
      }
    }
  }

  public String getQuery() {
    return this.query;
  }

  private void lTrim() {
    String temp = new String(this.query);
    int i=0;
    for(i=0;i<temp.length();i++) {
      if(temp.charAt(i) != ' ') {
        break;
      }
    }
    temp = temp.substring(i);
    this.query = temp;
  }

  private void rTrim() {
    String temp = this.query;
    int i=0;
    for(i=temp.length()-1;i>=0;i--) {
      if(temp.charAt(i) != ' ') {
        break;
      }
    }
    temp = temp.substring(0,(i+1));
    this.query = temp;
  }

  private String identifyCommand() {
    String temp = this.query;
    int i=0;
    for(i=0;i<temp.length();i++) {
      if(temp.charAt(i) == ' ') {
        break;
      }
    }
    String command = temp.substring(0,i);
    return command;
  }

  private boolean checkForCreate() {
    String temp = this.query;
    String str = new String();
    int in_word = 0,word_count = 0,i = 0;
    int start = 0,end = 0;
    for(i=0;i<temp.length();i++) {
      if(temp.charAt(i) != ' ' && in_word == 0) {
        in_word = 1;
        start = i;
      }
      else if(temp.charAt(i) == ' ' && in_word == 1) {
        in_word = 0;
        end = i-1;
        word_count++;
        if(word_count == 1 && temp.substring(start,end+1).equalsIgnoreCase("create")) {
          str += "create";
        }
        else if(word_count == 2 && temp.substring(start,end+1).equalsIgnoreCase("table")) {
          str += " table";
          break;
        }
        else {
          return false;
        }
      }
    }
    in_word = 0;
    for(i=end+1;i<temp.length();i++) {
      if(temp.charAt(i) != ' ' && in_word == 0) {
        if(temp.charAt(i) == '(' || temp.charAt(i) == ')') {
          return false;
        }
        start = i;
        in_word = 1;
      }
      else if(temp.charAt(i) == ' ' && in_word == 1) {
        end = i-1;
        in_word = 0;
        str += " "+temp.substring(start,end+1);
        break;
      }
    }
    for(i = end+1;i<temp.length();i++) {
      if(temp.charAt(i) != ' ') {
        if(temp.charAt(i) != '(') {
          return false;
        }
        else {
          break;
        }
      }
    }
    start = temp.indexOf("(");
    end = temp.indexOf(")");
    if(start == -1 || end == -1 || start > end || (end-start == 1)) {
      return false;
    }
    str += " ( "+sanitizeString(temp,start,end)+" )";
    this.query = str;
    return true;
  }

  private void createTable(String table_name,String[] col_name) {
    long startTime = System.currentTimeMillis();
    String str = new String();
    for(int i = 0;i<col_name.length;i++) {
      if(i == col_name.length-1) {
        str+=col_name[i]+";";
      }
      else {
        str+=col_name[i]+",";
      }
    }
    try {
      FileWriter fw = new FileWriter(new File(target,table_name+".txt"));
      for(int i = 0;i<str.length();i++) {
        fw.write(str.charAt(i));
      }
      fw.close();
    } catch(IOException ex) { }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime-startTime;
    System.out.println("Table create successfully!! (0.00"+elapsedTime+" s)");
  }

  private boolean checkForInsert() {
    String temp = this.query;
    String str = new String("insert");
    int i=0,in_word = 0,start = 0,end = 0,count = 0;
    for(i=6;i<temp.length();i++) {
      if(temp.charAt(i) != ' ' && in_word == 0) {
        in_word = 1;
        start = i;
      }
      else if((temp.charAt(i) == ' ' || i==temp.length()-1) && in_word == 1) {
        in_word = 0;
        end = i-1;
        if(i == temp.length()-1) {
          end = i;
        }
        count++;
        if(count == 1) {
          if(temp.substring(start,end+1).equalsIgnoreCase("into")) {
            str += " into";
          }
          else {
            return false;
          }
        }
        break;
      }
    }
    in_word = 0;
    for(i=end+1;i<temp.length();i++) {
      if(temp.charAt(i) != ' ' && in_word == 0) {
        start = i;
        in_word = 1;
      }
      else if(temp.charAt(i) == ' ' && in_word == 1) {
        end = i-1;
        in_word = 0;
        break;
      }
      else if(i == (temp.length()-1)) {
        return false;
      }
    }
    if(tableExist(temp.substring(start,end+1)) == false) {
      display_errm = 0;
      errorMessage("'"+temp.substring(start,end+1)+"' table does not exist !");
      return false;
    }
    str += " "+temp.substring(start,end+1);
    start = temp.indexOf('(');
    end = temp.indexOf(')');
    if(start == -1 || end == -1 || start > end || (end-start == 1)) {
      return false;
    }
    int col_count = sanitizeString(temp,start,end).split(",").length;
    if(checkColumns(str.split(" ")[2],sanitizeString(temp,start,end).split(",")) == false) {
      display_errm = 0;
      return false;
    }
    str += " ( "+sanitizeString(temp,start,end)+" )";
    in_word = 0;
    for(i=end+1;i<temp.length();i++) {
      if(temp.charAt(i) != ' ' && in_word == 0) {
        in_word = 1;
        start = i;
      }
      else if((temp.charAt(i) == ' ' || i == temp.length()-1) && in_word == 1) {
        if(i == temp.length()-1) {
          return false;
        }
        in_word = 0;
        end = i-1;
        if(temp.substring(start,end+1).equalsIgnoreCase("values")) {
          str += " values";
          break;
        }
        else {
          return false;
        }
      }
    }
    start = temp.indexOf("(",end);
    end = temp.indexOf(")",end);
    if(start == -1 || end == -1 || start > end || (end-start == 1)) {
      return false;
    }
    if(sanitizeString(temp,start,end).split(",").length != col_count) {
      return false;
    }
    str += " ( "+sanitizeString(temp,start,end)+" )";
    this.query = str;
    return true;
  }

  private String sanitizeString(String temp,int j,int k) {
    String str = new String();
    int in_word = 0;
    int start = 0,end = 0;
    for(int i = j+1;i<k;i++) {
      if((temp.charAt(i) != ' ' && temp.charAt(i) != ',') && in_word == 0) {
        in_word = 1;
        start = i;
      }
      else if((temp.charAt(i) == ' ' || temp.charAt(i) == ',' || i == k-1) && in_word == 1) {
        in_word = 0;
        end = i-1;
        if(i == k-1) {
          end = i;
        }
        str += temp.substring(start,end+1) + ",";
      }
    }
    return str.substring(0,str.length()-1);
  }

  private boolean tableExist(String tableName) {
    File[] listOfFiles = target.listFiles();
    for(int i = 0;i<listOfFiles.length;i++) {
      if(listOfFiles[i].isFile() && listOfFiles[i].getName().equals(tableName+".txt")) {
        return true;
      }
    }
    return false;
  }

  private boolean checkColumns(String tableName,String[] columns) {
    int ch;
    FileReader fr = null;
    try {
      fr = new FileReader(new File(target,tableName+".txt"));
    } catch (FileNotFoundException ex) {
      return false;
    }
    String s = new String();
    try {
      while((ch = fr.read()) != -1) {
        s += (char)ch;
      }
    } catch(IOException ex) {
      return false;
    }
    String[] columnsFetched =s.split(";",2)[0].split(",");
    if(columnsFetched.length < columns.length) {
      errorMessage("Colums don't match !");
      return false;
    }
    for(int i = 0;i<columns.length;i++) {
      if(Arrays.asList(columnsFetched).contains(columns[i]) == false) {
        errorMessage("Colums don't match !");
        return false;
      }
    }
    return true;
  }

  private void insertInto(String tableName,String[] columns,String[] values) {
    long startTime = System.currentTimeMillis();
    int ch;
    FileReader fr = null;
    try {
      fr = new FileReader(new File(target,tableName+".txt"));
    } catch (FileNotFoundException ex) { }
    String s = new String();
    try {
      while((ch = fr.read()) != -1) {
        s += (char)ch;
      }
      fr.close();
    } catch(IOException ex) { }
    String str = new String();
    String[] stdCols = s.split(";",2)[0].split(",");
    for(int i=0;i<stdCols.length;i++) {
      int index = Arrays.asList(columns).indexOf(stdCols[i]);
      if(i == stdCols.length-1) {
        str += values[index]+";";
      }
      else {
        str += values[index]+",";
      }
    }
    try {
      FileWriter fw = new FileWriter(new File(target,tableName+".txt"),true);
      fw.write(str);
      fw.close();
    } catch(IOException ex) { }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime-startTime;
    System.out.println("1 row inserted in "+tableName+" !! (0.00"+elapsedTime+" s)");
  }

  private void errorMessage(String msg) {
    System.out.println("Error : "+msg);
  }
}

class JavaDB {
  public static void main(String[] args) {
    Scanner sc = new Scanner(System.in);
    String str;
    str = sc.nextLine();
    QueryProcessor qp = new QueryProcessor(str);
    System.out.println(qp.getQuery());
  }
}
