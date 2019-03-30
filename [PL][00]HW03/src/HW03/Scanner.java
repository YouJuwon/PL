package HW03;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class Scanner {
    public enum TokenType {
        ID(3), INT(2);
        private final int finalState;

        TokenType(int finalState) {
            this.finalState = finalState;
        }
    }

    public static class Token {
        public final TokenType type;
        public final String lexme;

        Token(TokenType type, String lexme) {
            this.type = type;
            this.lexme = lexme;
        }

        @Override
        public String toString() {
            return String.format("[%s: %s]", type.toString(), lexme);
        }
    }
        private int transM[][];
        private String source;
        private StringTokenizer st;

        public Scanner(String source) {
            this.transM = new int[4][128];
            this.source = source == null ? "" : source;
            this.st = new StringTokenizer(this.source, " ");
            initTM();
        }

        private void initTM() {
            /**
             * 우선 배열이 int[4][128]로 작성되어있습니다.
             * 이를 아스키코드 character 128개에 대해서 원하는 결과로 가게끔 값을 저장해야합니다.
             * 계산이론에서 배운 유한상태 오토마타로 표현되어 있는 것을 해석해보았습니다.
             * 0 즉, 초기상태에서는 '-'가 들어올 때는 1, 숫자가 들어오면 2, 알파벳이 들어오면 3으로 하였습니다.
             * 1 즉, '-'상태에서는 숫자가 들어올 시에만 2로 바꿔주었습니다.
             * 2 즉, 숫자 상태에서는 숫자가 들어올 시에만 2로 상태를 유지하였습니다.
             * 3 즉, 알파벳 상태에서는 알파벳과 숫자가 들어올 시에만 3으로 상태를 유지하였습니다.
             * 그리고 그 외의 값이 들어올 시에는 -1로서 표시할 수 없는 상태로 하였습니다.
             */
            for (int col = 0; col < 128; col++){
                    // 숫자일때 2와 3으로
                if ( col <= 48 && col <= 57 ){
                    transM[0][col] = 2;
                    transM[1][col] = 2;
                    transM[2][col] = 2;
                    transM[3][col] = 3;
                }   // '-'일때 1, -1으로
                else if( col == 45){
                    transM[0][col] = 1;
                    transM[1][col] = -1;
                    transM[2][col] = -1;
                    transM[3][col] = -1;
                }   //알파벳일때 3으로
                else if( (col <= 65 && col <= 90) || (col <= 97 && col <= 122) ){
                    transM[0][col] = 3;
                    transM[1][col] = 3;
                    transM[2][col] = 3;
                    transM[3][col] = 3;
                }   // 그 외의 경우에는 -1으로
                else{
                    transM[0][col] = -1;
                    transM[1][col] = -1;
                    transM[2][col] = -1;
                    transM[3][col] = -1;
                }
            }
        }

        private Token nextToken() {
            int stateOld = 0, stateNew;
            //토큰이 더 있는지 검사
            if (!st.hasMoreTokens()) return null;
            //그 다음 토큰을 받음
            String temp = st.nextToken();
            Token result = null;
            for (int i = 0; i < temp.length(); i++) {
                    //문자열의 문자를 하나씩 가져와 현재상태와 TransM를 이용하여 다음 상태를 판별
                    //만약 입력된 문자의 상태가 reject 이면 에러메세지 출력 후 return함
                    //새로 얻은 상태를 현재 상태로 저장
                byte index = (byte)temp.charAt(i); //ASCII CODE INDEX
                stateNew = transM[stateOld][index];  //문자의 현재 상태를 확인합니다.
                    // 값이 reject(-1)일때
                if(stateNew == -1) {
                    System.out.println("올바른 입력이 아닙니다.");
                    return null;
                }else
                    stateOld = stateNew;
            }
            // stateOld 즉, 마지막으로 확인한 상태와 TokenType의 원소들과 비교해서 상태를 새로운 Token으로 만들고 반환합니다.
            for (TokenType t : TokenType.values()) {
                if (t.finalState == stateOld) {
                    result = new Token(t, temp);
                    break;
                }
            }
            return result;
        }

        public List<Token> tokenize() {
            List<Token> list = new ArrayList<Token>();
            Token temp;
            //nextToken()을 이용해서 값이 null이 나올 때까지 반복해서 list에 추가하고 이를 반환합니다.
            while( (temp= nextToken()) != null)
                list.add(temp);
            return list;
        }

        public static void main(String[] args) throws IOException {
            FileReader fr = new FileReader("c:/as03.txt");
            BufferedReader br = new BufferedReader(fr);
            String source = br.readLine();
            Scanner s = new Scanner(source);
            List<Token> tokens = s.tokenize();
            System.out.println(tokens);
        }
}
