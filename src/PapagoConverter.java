import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;


public class PapagoConverter extends JFrame {
	JButton convertBtn, cancelBtn;
	JTextArea textIn, textOut;
	
	public PapagoConverter() {
		super("텍스트 변환");
		
		textIn = new JTextArea(10, 14);
		textOut = new JTextArea(10, 14);
		textIn.setLineWrap(true);
		textOut.setLineWrap(true);
		textOut.setEnabled(false);
		
		JPanel textAreaPanel = new JPanel(new GridLayout(1, 2, 20, 20));
		textAreaPanel.add(textIn);
		textAreaPanel.add(textOut);
		
		convertBtn = new JButton("변환");
		cancelBtn = new JButton("취소");
		convertBtn.addActionListener(e -> {
			textOut.setText("");
			String result = getTransSentence(textIn.getText());
			textOut.append(subStringResult(result));
		});
		cancelBtn.addActionListener(e -> {
			textOut.setText("");
		});
		
		JPanel buttonPanel = new JPanel(new FlowLayout());
		buttonPanel.add(convertBtn);
		buttonPanel.add(cancelBtn);
		
		JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
		mainPanel.add(BorderLayout.CENTER, textAreaPanel);
		mainPanel.add(BorderLayout.SOUTH, buttonPanel);
		
		setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
		add(mainPanel);
		pack();
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}
	
//	public void actionPerformed(ActionEvent e) {
//		if (e.getSource() == convertBtn) {
//			textOut.setText("");
//			String result = getTransSentence(textIn.getText());
//			textOut.append(subStringResult(result));
//		}
//		
//		if (e.getSource() == cancelBtn) {
//			textOut.setText("");
//		}
//	}
	
	public String subStringResult(String s)
    {
        
		String token = "{";
        ArrayList<String> list1 = new ArrayList<>();
        StringTokenizer str = new StringTokenizer(s, token);
        while(str.hasMoreTokens())
        	list1.add(str.nextToken());
        
        ArrayList<String> list2 = new ArrayList<>();
        token = ",";
        str = new StringTokenizer(list1.get(2),token);
        while(str.hasMoreTokens())
        	list2.add(str.nextToken());
        
        ArrayList<String> list3 = new ArrayList<>();
        token = ":";
        str = new StringTokenizer(list2.get(2),token);
        while(str.hasMoreTokens())
        	list3.add(str.nextToken());
        String answer = "";
        for(int i =1;i<list3.size();i++) {
        	answer= answer+list3.get(i);
        }

        System.out.println(answer.substring(1,answer.length()-1));
        return answer.substring(1,answer.length()-1);
        
    }
	
	public static void main(String args[]) {
		PapagoConverter p = new PapagoConverter();
	}
	
	public String getTransSentence(String s){

        String clientId = "tv3vA7pz4Tznw9vyL_Fy";//애플리케이션 클라이언트 아이디값";
        String clientSecret = "ELbkIPA80q";//애플리케이션 클라이언트 시크릿값";

        String apiURL = "https://openapi.naver.com/v1/papago/n2mt";
        String text;
        try {
            text = URLEncoder.encode(s, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("인코딩 실패", e);
        }

        Map<String, String> requestHeaders = new HashMap<>();
        requestHeaders.put("X-Naver-Client-Id", clientId);
        requestHeaders.put("X-Naver-Client-Secret", clientSecret);

        String responseBody = post(apiURL, requestHeaders, text);
        
        return responseBody;
    }
	
	private static String post(String apiUrl, Map<String, String> requestHeaders, String text){
        HttpURLConnection con = connect(apiUrl);
        String postParams = "source=ko&target=en&text=" + text; //원본언어: 한국어 (ko) -> 목적언어: 영어 (en)
        try {
            con.setRequestMethod("POST");
            for(Map.Entry<String, String> header :requestHeaders.entrySet()) {
                con.setRequestProperty(header.getKey(), header.getValue());
            }

            con.setDoOutput(true);
            try (DataOutputStream wr = new DataOutputStream(con.getOutputStream())) {
                wr.write(postParams.getBytes());
                wr.flush();
            }

            int responseCode = con.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) { // 정상 응답
                return readBody(con.getInputStream());
            } else {  // 에러 응답
                return readBody(con.getErrorStream());
            }
        } catch (IOException e) {
            throw new RuntimeException("API 요청과 응답 실패", e);
        } finally {
            con.disconnect();
        }
    }
	
	private static HttpURLConnection connect(String apiUrl){
        try {
            URL url = new URL(apiUrl);
            return (HttpURLConnection)url.openConnection();
        } catch (MalformedURLException e) {
            throw new RuntimeException("API URL이 잘못되었습니다. : " + apiUrl, e);
        } catch (IOException e) {
            throw new RuntimeException("연결이 실패했습니다. : " + apiUrl, e);
        }
    }

    private static String readBody(InputStream body){
        InputStreamReader streamReader = new InputStreamReader(body);

        try (BufferedReader lineReader = new BufferedReader(streamReader)) {
            StringBuilder responseBody = new StringBuilder();

            String line;
            while ((line = lineReader.readLine()) != null) {
                responseBody.append(line);
            }

            return responseBody.toString();
        } catch (IOException e) {
            throw new RuntimeException("API 응답을 읽는데 실패했습니다.", e);
        }
    }
}
