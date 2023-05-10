import java.sql.SQLOutput;
import java.util.*;

class Main {
    public static void main(String args[]){
        Scanner scan = new Scanner(System.in);
        String str = scan.next();
        int startIdx = 0;
        int endIdx = 10;
        for(int i = 0; i < str.length(); i++){
            System.out.println(str.substring(i,i+10));
            i += 10;
        }
    }
}