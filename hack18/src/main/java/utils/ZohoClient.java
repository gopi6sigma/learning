package utils;

import org.apache.http.client.HttpClient;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


public class ZohoClient {

    private static final String EXPENSES_URL = "https://expense.zoho.com/api/v1/expenses";
    private static final String REFRESH_TOKEN=
            "1000.7cffe4d132107551feb2cd774c174211.1a4f5b49a8bba69bcb22cce5007d0856";
    private static final String OAUTH_TOKEN=
            "1000.bc3e5cc0c9163c56b06e8daf1e7b94b5.b5349f6e53168881866cc187210f5842";


    // curl -X GET --header "Authorization: Bearer $1"
    // -H "Content-Type: application/json"
    // "https://expense.zoho.com/api/v1/expensereports"
    // -H "X-com-zoho-expense-organizationid: 669111168"

        private static final String CLIENT_ID = "1000.YY1UTTIASRLH126476VBBRCFLA17FE";
    private static final String CLIENT_SECRET = "6c6e5c6f0c5e1c89e314b5bf17a66be3bfcd27cf77";
    private static final String REDIRECT_URI = "https://www.google.com/";

//    private static final String CLIENT_ID = "1000.R9FLGBRX7FUZ82394IF9HKW04SII8O132";
//    private static final String CLIENT_SECRET = "665bc73a3f1169c659ff218050166092abdb0e9fc5";
//    private static final String REDIRECT_URI = "http://www.sadisha.org";

    private static final String GRANT_TYPE = "authorization_code";
    private static final String RESPONSE_TYPE = "code";
    public static void main(String[] args) throws Exception {

        HttpURLConnection request = null;
//        HttpClient client = HttpClient


        String parameters = "client_id=" + CLIENT_ID + "&client_secret"
                + CLIENT_SECRET + "&redirect_uri=" + REDIRECT_URI + "&grant_type=" + GRANT_TYPE
//                + "&response_type=" + RESPONSE_TYPE
                + "&access_type=offline"
                + "&authtoken=" + OAUTH_TOKEN
//                + "&code=" + CODE;
        ;



        URL obj = new URL(EXPENSES_URL);
        HttpsURLConnection con = (HttpsURLConnection) obj.openConnection();

        //add reuqest header
        con.setRequestMethod("POST");
//        con.setRequestProperty("User-Agent", USER_AGENT);
        con.setRequestProperty("Accept-Language", "en-US,en;q=0.5");
//        con.setRequestProperty("authtoken", OAUTH_TOKEN);

//        String urlParameters = "sn=C02G8416DRJM&cn=&locale=&caller=&num=12345";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(parameters);
        wr.flush();
        wr.close();

        int responseCode = con.getResponseCode();
        System.out.println("\nSending 'POST' request to URL : " + EXPENSES_URL);
        System.out.println("Post parameters : " + parameters);
        System.out.println("Response Code : " + responseCode);

        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        //print result
        System.out.println(response.toString());

    }
}


/*


import java.net.HttpURLConnection;
        import java.net.URL;
        import java.io.*;
class TimesheetDemo
{
    public static void main(String[] args)
    {
        URL url;
        HttpURLConnection request = null;
        String authtoken = AUTHTOKEN;
        String method = "GET";
        String parameters = "";
        try
        {

            if("GET".equals(method))
            {
                parameters = "authtoken=" + authtoken + "&users_list=all&view_type=week&date=
                05-11-2014&bill_status=All&component_type=general";
                url = new URL("https://projectsapi.zoho.com/restapi/portal/[PORTALID]/projects/
                        [PROJECTID]/logs/?" + parameters);
                        request = (HttpURLConnection) url.openConnection();
                request.setRequestMethod("GET");
            }
            else if("POST".equals(method))
            {
                parameters = "authtoken=" + authtoken + "&name=Registration_Document_33A&date=
                05-14-2014&bill_status=Billable&hours=02:20";
                url = new URL("https://projectsapi.zoho.com/restapi/portal/[PORTALID]/projects/
                        [PROJECTID]/logs/?" + parameters);
                        request = (HttpURLConnection) url.openConnection();
                request.setRequestMethod("POST");
            }
            else if("DELETE".equals(method))
            {
                parameters = "authtoken=" + authtoken;
                url = new URL("https://projectsapi.zoho.com/restapi/portal/[PORTALID]/projects/
                        [PROJECTID]/logs/[LOGID]/?" + parameters);
                request = (HttpURLConnection) url.openConnection();
                request.setRequestMethod("DELETE");
            }
            // add request header
            request.setRequestProperty("Accept", "application/json");
            request.setDoOutput(true);
            request.setDoInput(true);
            request.connect();
            // Get Response
            BufferedReader bf = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while((line = bf.readLine())!=null) {
                response.append(line);
                response.append('\r');
            }
            bf.close();
            // Response HTTP Status Code
            System.out.println("Response HTTP Status Code : " + request.getResponseCode());
            // Response Body
            System.out.println("Response Body : " + response.toString());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            if(request!=null)
            {
                request.disconnect();
            }
        }
    }
}
 */