package site.metacoding.chat_v2;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class MyServerSocket {

    // 리스너(연결받기) - 메인스레드
    ServerSocket serverSocket;
    List<고객전담스레드> 고객리스트;

    // 메시지 받아서 보내기 (클라이언트 수마다 만들기)

    public MyServerSocket() {
        try {
            serverSocket = new ServerSocket(2000);
            고객리스트 = new Vector<>(); // 동기화가 처리된 ArrayList

            while (true) {
                // 포트 연결시 소켓 생성
                Socket socket = serverSocket.accept();
                System.out.println("클라이언트 연결됨");
                고객전담스레드 t = new 고객전담스레드(socket);
                고객리스트.add(t);
                System.out.println("고객리스트 크기 : " + 고객리스트.size());
                new Thread(t).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class 고객전담스레드 implements Runnable {

        // 익명클래스로 만들면 관리가 불가능해서
        Socket socket;
        BufferedReader reader;
        BufferedWriter writer;
        boolean isLogin = true;

        public 고객전담스레드(Socket socket) {
            this.socket = socket;

            // 버퍼 만들기
            try {
                reader = new BufferedReader(
                        new InputStreamReader(socket.getInputStream()));
                writer = new BufferedWriter(
                        new OutputStreamWriter(socket.getOutputStream()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (isLogin) {
                try {
                    String inputData = reader.readLine();
                    System.out.println("from 클라이언트 : " + inputData);

                    // for문 돌려서 List에 있는 다른 모든 클라이언트에게 메시지 전송
                    for (고객전담스레드 t : 고객리스트) {
                        if (t != this) {
                            t.writer.write(inputData + "\n");
                            t.writer.flush();
                        }
                    }
                } catch (Exception e) {
                    // e.printStackTrace();
                    System.out.println("오류내용 : " + e.getMessage());
                    try {
                        System.out.println("클라이언트 연결 해제 중");
                        isLogin = false; // while문 종료
                        고객리스트.remove(this); // 리스트에 담겨있는 소켓을 날려줘야한다.
                        writer.close(); // GarbageCollection이 일어나려면
                        reader.close();// 시간이 걸리는데 통신(IO가 생기기 때문)의 부하가
                        socket.close();// GarbageCollection의 부하가 더 크기 때문에 Garbage Collection을 직접 해준다.
                    } catch (Exception f) {
                        // f.printStackTrace();
                        System.out.println("연결 해제 실패 : " + f.getMessage());
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        new MyServerSocket();
    }
}
