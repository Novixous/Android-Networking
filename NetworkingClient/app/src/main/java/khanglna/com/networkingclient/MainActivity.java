package khanglna.com.networkingclient;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends Activity {
    final private int REQUEST_INTERNET = 123;
    private ImageView img;
    private TextView txt;
    private EditText edtWord;
    private static TextView txtMsgReceived;
    private EditText edtMsg;
    public static final String KNIGHT_IMG = "";
    public static final String BLADE_IMG = "";
    public static final String TEXT_URL = "";
    public static final String WORD_DEFINITION = "";
    public static final String JSON_URL = "";
    public static final String IP_ADDRESS = "10.0.2.15";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView) findViewById(R.id.img);
        txt = (TextView) findViewById(R.id.txtText);
        edtWord = (EditText) findViewById(R.id.edtWord);
        edtMsg = (EditText) findViewById(R.id.editMessage);
        txtMsgReceived = (TextView) findViewById(R.id.txtMsgReceived);

    }


    //Download Image
    public void clickToViewImg(View view) {
        new DownloadImageTask().execute("https://www.elcivics.com/transportation_corvette_car.jpg\n");
    }

    //Download Text
    public void clickToViewText(View view) {
        //Starting with Android 9 (API level 28), cleartext support is disabled by default.
        //java.io.IOException: Cleartext HTTP traffic to * not permitted
        //To fix -> add android:usesCleartextTraffic="true" in <Application></Application>
        //in AndroidManifest.xml
        new DownloadTextTask().execute("http://jfdimarzio.com/test.htm");
    }

    //Consume XML Web Services
    public void clickToConsumeXml(View view) {
        new AccessWebServiceTask().execute(edtWord.getText().toString());
    }

    //Consume JSON Web Services
    public void clickToConsumeJson(View view) {
        new ReadJSONFeedTask().execute("http://extjs.org.cn/extjs/examples/grid/survey.html");
    }

    //Socket Programing
    public void clickToSend(View view) {
        MyClientTask client = new MyClientTask(IP_ADDRESS, Integer.parseInt(edtMsg.getText().toString()));
        client.execute();
    }

    private class MyClientTask extends AsyncTask<Void, Void, Void> {
        private String dstAddress;
        private int dstPort;
        private String response = "";

        public MyClientTask(String dstAddress, int dstPort) {
            this.dstAddress = dstAddress;
            this.dstPort = dstPort;
        }

        @Override
        protected Void doInBackground(Void... params) {
            Socket socket = null;
            try {
                socket = new Socket(dstAddress, dstPort);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
                byte[] buffer = new byte[1024];
                int byteRead;
                InputStream inputStream = socket.getInputStream();
                while ((byteRead = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, byteRead);
                    response += byteArrayOutputStream.toString("UTF-8");
                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
                response = "UnknowHostException: " + e.toString();
            } catch (IOException e) {
                e.printStackTrace();
                response = "IOException: " + e.toString();
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            txtMsgReceived.setText(response);
            super.onPostExecute(aVoid);
        }
    }

    private InputStream OpenHttpConnection(String urlString) throws IOException {
        InputStream in = null;
        int response = -1;
        URL url = new URL(urlString);
        URLConnection conn = url.openConnection();
        if (!(conn instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");
        try {
            HttpURLConnection httpConn = (HttpURLConnection) conn;
            httpConn.setAllowUserInteraction(false);
            httpConn.setInstanceFollowRedirects(true);
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            response = httpConn.getResponseCode();
            if (response == HttpURLConnection.HTTP_OK) {
                in = httpConn.getInputStream();
            }
        } catch (Exception ex) {
            Log.d("Networking", ex.getLocalizedMessage());
            throw new IOException("Error connecting");
        }
        return in;
    }

    private Bitmap DownloadImage(String URL) {
        Bitmap bitmap = null;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
            bitmap = BitmapFactory.decodeStream(in);
            in.close();
        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }
        return bitmap;
    }

    private class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
        protected Bitmap doInBackground(String... urls) {
            return DownloadImage(urls[0]);
        }


        @Override
        protected void onPostExecute(Bitmap result) {
            img.setImageBitmap(result);
        }
    }

    private String DownloadText(String URL) {
        int BUFFER_SIZE = 2000;
        InputStream in = null;
        try {
            in = OpenHttpConnection(URL);
        } catch (IOException e) {
            Log.d("Networking", e.getLocalizedMessage());
            return "";
        }
        InputStreamReader isr = new InputStreamReader(in);
        int charRead;
        String str = "";
        char[] inputBuffer = new char[BUFFER_SIZE];
        try {
            while ((charRead = isr.read(inputBuffer)) > 0) {
                //---convert the chars to a String---
                String readString = String.copyValueOf(inputBuffer, 0, charRead);
                str += readString;
                inputBuffer = new char[BUFFER_SIZE];
            }
            in.close();
        } catch (IOException e) {
            Log.d("Networking", e.getLocalizedMessage());
            return "";
        }
        return str;
    }

    private class DownloadTextTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return DownloadText(urls[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            txt.setText(result);
        }
    }

    private List<String> WordDefinition(String word) {
        InputStream in = null;
        List<String> stringResult = new ArrayList<>();
        try {
            if (StringUtils.isEmpty(word)) {
                in = OpenHttpConnection("https://gorest.co.in/public-api/users?_format=xml&access-token=qZgOPFVn4rSm4dGYK0e5k-0Sc_QEhEq0ARiw");

            } else {
                in = OpenHttpConnection("https://gorest.co.in/public-api/users?_format=xml&access-token=qZgOPFVn4rSm4dGYK0e5k-0Sc_QEhEq0ARiw&first_name=" + word);

            }
            Document doc = null;
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //to get localname
            dbf.setNamespaceAware(true);
            DocumentBuilder db;
            try {
                db = dbf.newDocumentBuilder();
                doc = db.parse(in);
            } catch (ParserConfigurationException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            doc.getDocumentElement().normalize();
            //---retrieve all the <Definition> elements---
            NodeList itemList = doc.getElementsByTagName("item");
            for (int i = 0; i < itemList.getLength(); i++) {
                Node item = itemList.item(i);
                if (item.getNodeType() == Node.ELEMENT_NODE) {
                    Element itemElement = (Element) item;
                    NodeList list = itemElement.getChildNodes();
                    String temp = "";
                    for (int j = 0; j < list.getLength(); j++) {
                        Node listItem = list.item(j);
                        if (!listItem.getLocalName().equals("_links")) {
                            temp += listItem.getLocalName() + ": " + listItem.getChildNodes().item(0).getNodeValue() + "\n";
                        } else {
                            Element linkElement = (Element) list.item(j);
                            NodeList linkList = linkElement.getChildNodes();
                            for (int k = 0; k < linkList.getLength(); k++) {
                                Node linkNode = linkList.item(k);
                                temp += linkNode.getLocalName() + " " + k + ": " + linkNode.getChildNodes().item(0).getChildNodes().item(0).getNodeValue() + "\n";
                            }
                        }
                    }
                    temp += "\n";
                    stringResult.add(temp);
                }
            }

            //---iterate through each <Definition> elements---

        } catch (IOException e1) {
            Log.d("NetworkingActivity", e1.getLocalizedMessage());
        }
        //---return the definitions of the word---
        return stringResult;
    }

    private void goToTextContent(ArrayList<String> content) {
        Intent intent = new Intent(this, TextContent.class);
        intent.putStringArrayListExtra("content", content);
        startActivity(intent);
    }

    private class AccessWebServiceTask extends AsyncTask<String, Void, List<String>> {
        protected List<String> doInBackground(String... urls) {
            return WordDefinition(urls[0]);
        }

        protected void onPostExecute(List<String> result) {
            goToTextContent(new ArrayList<String>(result));
        }
    }

    public String readJSONFeed(String address) {
        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        StringBuilder stringBuilder = new StringBuilder();
        HttpURLConnection urlConnection = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            InputStream content = new BufferedInputStream(urlConnection.getInputStream());
            BufferedReader reader = new BufferedReader(new InputStreamReader(content));
            String line;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            urlConnection.disconnect();
        }
        return stringBuilder.toString();
    }

    private class ReadJSONFeedTask extends AsyncTask<String, Void, String> {
        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }

        protected void onPostExecute(String result) {
            try {
                JSONArray jsonArray = new JSONArray(result);
                Log.i("JSON", "Number of surveys in feed: " + jsonArray.length());
                // print out the content of the json feed
                ArrayList<String> content = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String temp = "";
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    temp += "appeId: " + jsonObject.getString("appeId") + "\n";
                    temp += "survId: " + jsonObject.getString("survId") + "\n";
                    temp += "location: " + jsonObject.getString("location") + "\n";
                    temp += "surveyDate: " + jsonObject.getString("surveyDate") + "\n";
                    temp += "surveyTime: " + jsonObject.getString("surveyTime") + "\n";
                    temp += "inputUserId: " + jsonObject.getString("inputUserId") + "\n";
                    temp += "inputTime: " + jsonObject.getString("inputTime") + "\n";
                    temp += "modifyTime: " + jsonObject.getString("modifyTime") + "\n\n";
                    content.add(temp);
                }
                goToTextContent(content);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

