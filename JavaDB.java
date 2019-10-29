import java.io.FileNotFoundException; 
import java.io.FileWriter; 
import java.io.IOException;
import java.io.File;
import java.util.Scanner;

class QueryProcessor {
  private String query;

  public QueryProcessor(String str) {
    setQuery(str);
  }

  public void setQuery(String str) {
    this.query = str;
    lTrim();
    rTrim();
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
          // str+=temp.substring(start,end+1);
          str += "create";
        }
        else if(word_count == 2 && temp.substring(start,end+1).equalsIgnoreCase("table")) {
          // str+=" "+temp.substring(start,end+1);
          str += " table";
          break;
        }
        else {
          return false;
        }
      }
    }
    in_word=0;
    word_count = 0;
    for(i=end+1;i<temp.length();i++) {
      if(temp.charAt(i) != ' ' && in_word == 0) {
        in_word = 1;
        start = i;
        word_count++;
      }
      else if((temp.charAt(i) == ' ' || temp.charAt(i) == '(') && in_word == 1) {
        in_word = 0;
        end = i-1;
        str+=" "+temp.substring(start,end+1)+" ( ";
        word_count++;
        break;
      }
    }
    if(word_count != 2) {
      return false;
    }
    word_count = 0;
    for(i=end+1;i<temp.length();i++) {
      if(temp.charAt(i) != ' ') {
        if(temp.charAt(i) == '(' && word_count == 0) {
          word_count++;
        }
        else if(word_count == 0) {
          return false;
        }
        else {
          start = i;
          break;
        }
      }
    }
    int flag=0;
    while(flag == 0) {
      in_word=0;
      for(i=start;i<temp.length();i++) {
        if(temp.charAt(i) != ' ' && in_word == 0) {
          in_word = 1;
          start = i;
        }
        else if((temp.charAt(i) == ' ' || temp.charAt(i) == ',' || temp.charAt(i) == ')') && in_word == 1) {
          end = i-1;
          in_word = 0;
          str+=temp.substring(start,end+1);
          if(temp.charAt(i) == ' ') {
            i++;
            while(true) {
              if(i<temp.length()  && temp.charAt(i) != ' ') {
                break;
              }
              else {
                i++;
              }
            }
          }
          if(temp.charAt(i) == ',') {
            str+=",";
          }
          else if(temp.charAt(i) == ')') {
            str+=" )";
            this.query = str;
            return true;
          }
          else {
            return false;
          }
        }
      }
    }
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
      File target = new File("/home/hardik/Desktop/JavaDB/tables",table_name+".txt");
      FileWriter fw = new FileWriter(target);
      for(int i = 0;i<str.length();i++) {
        fw.write(str.charAt(i));
      }
      fw.close();
    } catch(IOException ex) { }
    long stopTime = System.currentTimeMillis();
    long elapsedTime = stopTime-startTime;
    System.out.println("Table create successfully!! (0.00"+elapsedTime+" s)");
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
