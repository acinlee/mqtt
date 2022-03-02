import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Consumer;

public class Main {
    public static void main(String [] args){
        final Consumer<HashMap<Object, Object>> pdk = (arg)->{  //메시지를 받는 콜백 행위
            arg.forEach((key, value)->{
                System.out.println( String.format("메시지 도착 : 키 -> %s, 값 -> %s", key, value) );
            });
        };

        CustomMqttClient client = new CustomMqttClient(pdk); //해당 함수를 생성자로 넣어준다.

        client.init("test1", "1234", "tcp://127.0.0.1:1883", "test1")
                .subscribe(new String[]{"new_topic"});

        client.init("test2", "1234", "tcp://127.0.0.1:1883", "test2")
                .subscribe(new String[]{"new_topic"});


        client.initConnectionLost( (arg)->{  //콜백행위1, 서버와의 연결이 끊기면 동작
            arg.forEach((key, value)->{
                System.out.println( String.format("커넥션 끊김~! 키 -> %s, 값 -> %s", key, value) );
            });
        });

        client.initDeliveryComplete((arg)-> {  //콜백행위2, 메시지를 전송한 이후 동작
            arg.forEach((key, value)->{
                System.out.println( String.format("메시지 전달 완료~! 키 -> %s, 값 -> %s", key, value) );
            });
        });


        new Thread( ()->{
            try {
                Scanner sc = new Scanner(System.in);
                String msg = "";
                while(true) {
                    System.out.println("보낼 메시지 입력하세요 : ");
                    msg = sc.next();
                    client.sender("new_topic", msg);  //이런식으로 보낸다.
                    client.sender("new_topic2", msg);  //이런식으로 보낸다.
                    if(msg.equals("bye")) {
                        break;
                    }

                }
                client.close();  //종료는 이렇게!
            } catch (Exception e) {
                e.printStackTrace();
            }
        } ).start();

    }
}
